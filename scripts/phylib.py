#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>
#
# This library contains handler of the meta data for our software
# releases. A release is fully described by a manifest.xml file. It
# contains commit ids to all relevant software repositories.
# In this library we have two main classes:
#
# - Sourcecode
# - BoardSupportPackage (BSP)
#
# A BSP has a Sourecode and some configuration. A Sourcecode does not
# know about its release until a BSP sets it up.
#
# How it works:
# When you instantiate the BSP class, it searches for the manifest.xml,
# parses its information and configures the Sourcecode class, which
# itself sets up the information about the Sourcecode.
#
# usage:
# If you want to extend the functionality, you have to inherit th
# BoardSupportPackage class and add members. For examples see the
# accompanying scripts.

import os
import pprint
import collections
import shutil
import subprocess
import re
import sys
import xml.etree.ElementTree as ET

if sys.version_info < (3, 0):
    input = raw_input

def call(cmd):
    print('$', cmd)
    returncode = subprocess.call(cmd,shell=True)
    print('')
    return returncode

def userquery_yes_no(default=True):
    print('[yes/no]:')
    yes = set(['y', 'ye', 'yes'])
    no = set(['n', 'no'])
    if default:
        yes.add('')
    else:
        no.add('')
    while True:
        user_input = input('$ ').lower()
        if user_input in yes:
            return True
        elif user_input in no:
            return False
        else:
            print('Please type y or n')


# Stack overflow helper Classes
class Bunch(object):
    """This class can be used on dictionaries
    to have its content accessible in the namespace.
    """
    def __init__(self, adict):
        self.__dict__.update(adict)


class Vividict(dict):
    """This class can be used to create dicts of dicts in a
    nice an manageable way.
    """
    def __missing__(self, key):
        value = self[key] = type(self)()
        return value


# Main Classes
class Sourcecode(object):
    def __init__(self):
        # Interface
        self.machines = Vividict()
        self.bsp_dir = ""
        self._poky_version = None

        try:
            #v2 Implementation
            cwd = self.search_for_bsp_dir()
            self.bsp_dir = cwd
            self.init_machines()
        except (IOError, OSError) as e:
            print("Could not find necessary file: ", e)
            raise SystemExit

    @property
    def poky_version(self):
        if self._poky_version:
            return self._poky_version
        repo = 'poky' if self.is_poky() else 'oe-core'
        p = subprocess.Popen(["git", "tag", "-l", "yocto-*", "--merged", "HEAD"],
                            cwd=os.path.join(self.bsp_dir, "sources", repo),
                            stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        (out, error) = p.communicate()
        if p.returncode != 0:
            raise Exception("ERROR in git: %d '%s'" % (p.returncode, error))
        taglist = out.decode('utf-8').split('\n')
        taglist_sorted = []
        for t in taglist:
            t = t.lstrip('yocto-')
            t = [e.split('_') for e in t.split('.')]
            # flatten list
            t = [item for sublist in t for item in sublist]
            taglist_sorted.append(tuple(t))
        taglist_sorted.sort()
        self._poky_version = taglist_sorted[-1]
        return self._poky_version

    def is_poky(self):
        sources = os.path.join(self.bsp_dir, 'sources')
        return os.path.exists(os.path.join(sources, 'poky'))

    def get_init_env(self):
        sources = os.path.join(self.bsp_dir, 'sources')
        if self.is_poky():
            return os.path.join(sources, 'poky/oe-init-build-env')
        else:
            return os.path.join(sources, 'oe-core/oe-init-build-env')

    def search_for_bsp_dir(self):
        path = os.path.normpath(os.getcwd())
        while(path != "/"):
            d = os.listdir(path)
            for files in d:
                if files.endswith('.repo'):
                    return path
            path, tail = os.path.split(path)
        raise IOError('No .repo directory found. Are you inside a BSP folder?')

    def get_repo_dir(self):
        return os.path.join(self.bsp_dir, '.repo')

    def init_machines(self):
        for root, dirs, files in os.walk(os.path.join(self.bsp_dir, "sources")):
            for name in files:
                if name.endswith('.conf') and root.endswith('conf/machine'):
                    machname = os.path.splitext(name)[0]
                    path = os.path.join(root, name)
                    self.machines[machname]['abs_path'] = path
                    self.parse_machine_info(machname)
        return True

    def parse_machine_info(self, machine):
        # we read the machine information from @TAGS provided
        # by the machine.conf files.
        file = open(self.machines[machine]['abs_path'])
        for line in file:
            m = re.search(r'(@[A-Z]+:)', line)
            if m:
                data = line.split(m.group(0))[1]
                # remove leading @ and tailing : from tag
                tag = m.group(0)[1:-1]
                self.machines[machine][tag] = data.strip()

class BoardSupportPackage(object):
    def __init__(self):
        #Interface
        self.src = Sourcecode()
        self.xml = ""
        self.pdn = "UNASSIGNED"
        self.soc = "all"
        self.selected_machine = "UNASSIGNED"
        self.selected_distro = "ampliphy"
        self.supported_builds = []
        self.supported_configs = []
        self.local_conf = ""
        self.build_dir = ""
        self.image_base_dir = ""
        self.project_paths = []

        try:
            #v2 Implementation
            self.local_conf = os.path.join(self.src.bsp_dir, "build/conf/local.conf")
            self.build_dir = os.path.join(self.src.bsp_dir, "build")
            self.image_base_dir = os.path.join(self.src.bsp_dir, "build/deploy/images")
            self.probe_selected_release()
            self.release_fallback()

        except (IOError, OSError) as e:
            print("Could not find necessary file: ", e)
            self.release_fallback()
            raise SystemExit

    def release_fallback(self):
        """ This function will cover two cases.
        If we have a manifest and are not on a release, we don't have any supported
        builds. In that case we are most probably on the HEAD of a branch and should
        just use the machine meta information for the selection script.
        On the other hand, it could be that we have no manifest at all. In that case
        we also just parse the meta data from the machines."""

        # don't do anything if we get the data from a release manifest
        if len(self.supported_builds) > 0:
            return True

        # add supported_builds based on machine meta data
        for x in self.src.machines:
            # if a soc is set in the manifest, filter non matching machines
            if self.soc != "all" and self.soc not in x:
                continue

            if self.src.machines[x]['SUPPORTEDIMAGE']:
                for target in map(str.strip, self.src.machines[x]['SUPPORTEDIMAGE'].split(',')):
                    distro = 'ampliphy'
                    var = target.split('/')
                    if len(var) > 1:
                        target = var[0]
                        distro = var[1]
                    self.supported_builds.append((x, target, distro))

            self.supported_builds.sort()
            self.supported_configs = self.__get_supported_configs()

    def set_manifest(self, manifest_abs_path):
        self.xml = manifest_abs_path
        root = ET.parse(self.xml).getroot()
        release_info = {}
        projects = [{}]
        for child in root:
            if child.tag == "phytec":
                release_info = child.attrib
            elif child.tag == "project":
                if not child.findall("ignorebaselayer"):
                    projects.append(child.attrib)
                for sublayer in child.iter("sublayer"):
                    subproject = sublayer.attrib
                    subproject["path"] = os.path.join(child.attrib["path"], subproject["path"])
                    projects.append(subproject)

        # import meta data, some keys need special treatments
        for key in list(release_info.keys()):
            if key == "supported_builds":
                for b in filter(None, release_info[key].split(',')):
                    self.supported_builds.append(b.strip().split('/'))
            else:
                setattr(self, key, release_info[key])

        self.supported_builds.sort()
        self.supported_configs = self.__get_supported_configs()

        # allow capitalization for soc in manifest
        self.soc = self.soc.lower()

        # format and store project paths
        for project in projects:
            for key in list(project.keys()):
                if key == "path":
                    path = project[key].split('/')[1:]
                    path = os.path.join(*path)
                    self.project_paths.append(path)

    def probe_selected_release(self):
        repo_dir = self.src.get_repo_dir()
        if os.path.islink(os.path.join(repo_dir, 'manifest.xml')):
            f = os.readlink(os.path.join(repo_dir, 'manifest.xml'))
        else:
            repo_manifest = open(os.path.join(repo_dir, 'manifest.xml'), 'r')
            lines_manifest = repo_manifest.readlines()
            repo_manifest.close()
            for line in lines_manifest:
                xline = line.split('"')
                for elem in xline:
                    if ".xml" in elem:
                        phymanifest = elem
            f = os.path.join(repo_dir, 'manifests/' + phymanifest)
        f = os.path.join(repo_dir, f)
        self.set_manifest(f)

    def write_machine_to_localconf(self):
        lconf = self.local_conf
        lconftmp = lconf + '~'
        shutil.copyfile(lconf, lconftmp)
        fw = open(lconf, 'w')
        fr = open(lconftmp, 'r')
        frl = fr.readlines()
        for line in frl:
            if line.strip().startswith("#"):
                fw.write(line)
            elif line.strip().startswith("MACHINE "):
                print('set MACHINE in local.conf to %s' % self.selected_machine)
                fw.write('MACHINE ?= "' + self.selected_machine + '"\n')
            elif line.strip().startswith("DISTRO "):
                print('set DISTRO in local.conf to %s' % self.selected_distro)
                fw.write('DISTRO ?= "' + self.selected_distro + '"\n')
            else:
                fw.write(line)
        fr.close()
        fw.close()
        os.remove(lconftmp)
        return True

    def __get_supported_configs(self):
        configs = {}
        for (machine, target, distro) in self.supported_builds:
            configs.setdefault((machine, distro), []).append(target)
        configs = [(m, d, t) for ((m, d), t) in list(configs.items())]
        configs.sort()
        return configs
