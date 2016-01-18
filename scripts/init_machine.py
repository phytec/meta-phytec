#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import sys
import os
from switch_machine import *


def main():
    bsp = BSP_Switcher()
    if "MACHINE" in os.environ:
        # overwrite machine selection with env variable
        if not bsp.switch_machine(os.environ['MACHINE']):
            sys.exit(1)
    elif not bsp.selected_machine or "UNASSIGNED" in bsp.selected_machine:
        # we could not parse a machine from manifest xml
        # we assume we set up an unified bsp and need to
        # ask the user for a machine to be set up
        if not bsp.switch_machine():
            sys.exit(1)
    else:
        # set machine from manifest xml
        if not bsp.write_machine_to_localconf():
            sys.exit(1)

if __name__ == "__main__":
    main()
