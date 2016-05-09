#!/usr/bin/env python2
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
import xml.etree.ElementTree as ET


def call(cmd):
    print '$', cmd
    returncode = subprocess.call(cmd,shell=True)
    print ''
    return returncode

def userquery_yes_no(default=True):
    print '[yes/no]:'
    yes = set(['y', 'ye', 'yes'])
    no = set(['n', 'no'])
    if default:
        yes.add('')
    else:
        no.add('')
    while True:
        user_input = raw_input('$ ').lower()
        if user_input in yes:
            return True
        elif user_input in no:
            return False
        else:
            print 'Please type y or n'


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
        self.meta_phytec_dir = ""

        try:
            #v2 Implementation
            cwd = self.search_for_bsp_dir()
            self.bsp_dir = cwd
            self.meta_phytec_dir = os.path.join(cwd, "sources/meta-phytec")
            self.init_machines()
        except (IOError, OSError), e:
            print "Could not find necessary file: ", e
            raise SystemExit

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
        d = os.listdir(os.path.join(self.meta_phytec_dir, 'conf/machine'))
        d.sort()
        for j in d:
            if j.endswith('.conf'):
                machname = os.path.splitext(j)[0]
                path = os.path.join(self.meta_phytec_dir, 'conf/machine', j)
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
        self.supported_machines = []
        self.local_conf = ""
        self.build_dir = ""
        self.image_base_dir = ""

        try:
            #v2 Implementation
            self.local_conf = os.path.join(self.src.bsp_dir, "build/conf/local.conf")
            self.build_dir = os.path.join(self.src.bsp_dir, "build")
            self.image_base_dir = os.path.join(self.src.bsp_dir, "build/deploy/images")
            self.probe_selected_release()
            self.supported_machines = []
            for x in self.src.machines:
                if self.src.machines[x]['SUPPORTEDIMAGE']:
                    if self.soc in x or self.soc == "all":
                        self.supported_machines.append(x)

        except (IOError, OSError) as e:
            print "Could not find necessary file: ", e
            raise SystemExit

    def set_manifest(self, manifest_abs_path):
        self.xml = manifest_abs_path
        root = ET.parse(self.xml).getroot()
        release_info = {}
        for child in root:
            if child.tag == "phytec":
                release_info = child.attrib
        # source settings
        try:
            self.pdn = release_info["pdn"]
            self.soc = release_info["soc"].lower()
            # BSP settings
            self.selected_machine = release_info["machine"]
        except KeyError, e:
            #There can be measures taken, if a key is not set
            #print e
            pass

    def probe_selected_release(self):
        repo_dir = self.src.get_repo_dir()
        f = os.readlink(os.path.join(repo_dir, 'manifest.xml'))
        f = os.path.join(repo_dir, f)
        self.set_manifest(f)

    def write_machine_to_localconf(self):
        lconf = self.local_conf
        lconftmp = lconf + '~'
        shutil.copyfile(lconf, lconftmp)
        fw = open(lconf, 'w')
        fr = open(lconftmp, 'r')
        frl = fr.readlines()
        set_already = False
        for line in frl:
            if "MACHINE" in line and not line.strip().startswith("#") and not set_already:
                print 'set MACHINE in local.conf to ', self.selected_machine
                fw.write('MACHINE ?= "' + self.selected_machine + '"\n')
                set_already = True
            else:
                fw.write(line)
        fr.close()
        fw.close()
        os.remove(lconftmp)
        if not set_already:
            print 'Could not set MACHINE in local.conf'
            return False
        return True
