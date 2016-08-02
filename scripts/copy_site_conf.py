#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import sys
import argparse
import os
import shutil
from phylib import *


class BSP_SiteConfLoader(BoardSupportPackage):
    """Extends the BoardSupportPackage class with functionalty to
    manage a site.conf. This file is Host or user specific and defines
    settings very specific to the location where the bsp is getting built
    """
    def __init__(self):
        super(BSP_SiteConfLoader, self).__init__()

    def copy_site_conf(self, arg=None):
        if arg is None:
            arg = self.probe_for_siteconf()
        if arg is None:
            print 'No site.conf found on host.'
            return False
        target = os.path.join(self.build_dir, 'conf/site.conf')
        print "site.conf setup: Copying " + arg + " to " + target
        shutil.copyfile(arg, target)
        return True

    def probe_for_siteconf(self):
        locations = ["~/.site.conf",
                     "/home/share/tools/yocto/site.conf",
                     "/etc/yocto/site.conf"]
        for l in locations:
            if os.path.isfile(os.path.expanduser(l)):
                return os.path.expanduser(l)
        return None


##############
# Executable #
##############

def main():
    """This script starts the site.conf mechanism and copies the choosen site.conf
    in your build/conf directory
    """
    parser = argparse.ArgumentParser(description='copy a site.conf into your conf dir')
    parser.add_argument('-f', dest='filename', help='set the site.conf file location manually')

    args = parser.parse_args()

    bsp = BSP_SiteConfLoader()
    if not bsp.copy_site_conf(args.filename):
        # An error has happened. Report it back to calling program.
        sys.exit(1)

if __name__ == "__main__":
    main()
