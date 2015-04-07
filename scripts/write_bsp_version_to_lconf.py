#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import sys
from phylib import *


def main():
    bsp = BoardSupportPackage()
    if not bsp.write_bsp_version_to_localconf():
        # An error has happened. Report it back to calling program.
        sys.exit(1)


if __name__ == "__main__":
    main()
