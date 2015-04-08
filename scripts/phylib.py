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
        self.soc = []
        self.phylinux_api = 0
        self.init_script = ""
        self.machines = Vividict()
        self.bsp_dir = ""
        self.meta_phytec_dir = ""
        self.soc_layers_top_dir = ""

        try:
            #v2 Implementation
            cwd = self.search_for_bsp_dir()
            self.bsp_dir = cwd
            self.meta_phytec_dir = os.path.join(cwd, "sources/meta-phytec")
            self.soc_layers_top_dir = self.meta_phytec_dir
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
        soc_bsp_folders = []
        machines = []
        d = os.listdir(self.soc_layers_top_dir)
        for i in d:
            if 'meta-phy' in i:
                soc_bsp_folders.append(i)
        for i in soc_bsp_folders:
            d = os.listdir(os.path.join(self.soc_layers_top_dir, i, 'conf/machine'))
            d.sort()
            for j in d:
                if j.endswith('.conf'):
                    bsp_id = len(self.machines) + 1
                    machname = os.path.splitext(j)[0]
                    path = os.path.join(self.soc_layers_top_dir, i, 'conf/machine', j)
                    self.machines[machname]['abs_path'] = path
                    self.machines[machname]['bsp_id'] = bsp_id
                    self.machine_parse_description(machname)
        #print "Added Machines: "
        #pprint.pprint(self.machines)
        return True

    def machine_parse_description(self, machine):
        # at the moment we get the machine description by extracting
        # the @DESCRIPTION tag from machine.conf file
        desc = "no description found"
        file = open(self.machines[machine]['abs_path'])
        for line in file:
            if '@DESCRIPTION' in line:
                desc = line.split(":", 1)[1][:-1]
                break
        self.machines[machine]['description'] = desc

    def get_machine_by_bsp_id(self, bsp_id):
        for machine in self.machines:
            if self.machines[machine]['bsp_id'] == bsp_id:
                return machine
        return -1

    def get_machines_by_property(self, prop, value):
        machines = sorted(self.machines)
        return filter(lambda x: self.machines[x][prop] == value, machines)


class BoardSupportPackage(object):
    def __init__(self):
        #Interface
        self.src = Sourcecode()
        self.init_script = "tools/init"
        self.xml = ""
        self.uid = ""
        self.pdn = ""
        self.soc = ""
        self.selected_machine = ""
        self.local_conf = ""
        self.build_dir = ""
        self.image_base_dir = ""
        self.image_dir_deploy_N_lw = ""
        self.image_dir_ftp = ""

        try:
            #v2 Implementation
            self.local_conf = os.path.join(self.src.bsp_dir, "build/conf/local.conf")
            self.build_dir = os.path.join(self.src.bsp_dir, "build")
            self.image_base_dir = os.path.join(self.src.bsp_dir, "build/deploy/images")
            self.image_dir_deploy_N_lw = ""
            self.image_dir_ftp = "ftp://ftp.phytec.de"
            self.probe_selected_release()
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
            self.uid = release_info["release_uid"]
            self.pdn = release_info["pdn"]
            self.soc = release_info["soc"]
            self.src.phylinux_api = release_info["phylinux_api"]
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

    def write_bsp_version_to_localconf(self):
        lconf = self.local_conf
        lconftmp = lconf + '~'
        shutil.copyfile(lconf, lconftmp)
        fw = open(lconf, 'w')
        fr = open(lconftmp, 'r')
        frl = fr.readlines()
        set_already = False
        for line in frl:
            if "BSP_VERSION" in line and not line.strip().startswith("#") and not set_already:
                print 'set BSP_VERSION in local.conf to ', self.uid
                fw.write('BSP_VERSION = "' + self.uid + '"\n')
                set_already = True
            else:
                fw.write(line)
        fr.close()
        fw.close()
        os.remove(lconftmp)
        if not set_already:
            print 'Could not set BSP_VERSION in local.conf'
            return False
        return True

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

    def sanity_check_configuration(self):
        if self.pdn != os.path.splitext(os.path.basename(self.xml))[0]:
            print 'There is a misconfiguration in your Release!'
            print 'The manifest.xml target:', self.xml
            print 'does not have the PD number:', self.pdn, 'as filename.'
