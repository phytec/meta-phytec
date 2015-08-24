#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import argparse
from phylib import *


class BSP_Sanity(BoardSupportPackage):
    """This class extends the bsp with functionality to run our internal
    sanity checks on releases, so we don't mess them up ;-) This is done
    to extend the build system with features and checks for out namingschemes and conventions
    which cannot properly be integrated into the code.
    """
    def __init__(self):
        super(BSP_Sanity, self).__init__()

    def sanity_check(self, args):
        if self.src.pdn != os.path.splitext(os.path.basename(self.xml))[0]:
            print 'The manifest.xml target:', self.xml
            print 'does not have the correct PD number:', self.src.pdn, 'as filename.'
        if self.selected_machine:
            print 'Do you really want to release a SOC bsp with a selected machine:',
            print self.selected_machine


##############
# Executable #
##############

def main():
    """
    """
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('-m', dest='machine', help='set the machine string')

    args = parser.parse_args()

    bsp = BSP_Sanity()
    bsp.sanity_check(args)

if __name__ == "__main__":
    main()
