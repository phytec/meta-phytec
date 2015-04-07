#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import sys
from switch_machine import *


def main():
    bsp = BSP_Switcher()
    if not bsp.selected_machine:
        # we could not parse a machine from manifest xml
        # we assume we set up an unified bsp and need to
        # ask the user for a machine to be set up
        bsp.switch_machine()
    if not bsp.write_machine_to_localconf():
        # An error has happened. Report it back to calling program.
        sys.exit(1)


if __name__ == "__main__":
    main()
