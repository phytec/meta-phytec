#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

from phylib import *


def main():
    bsp = BoardSupportPackage()
    bsp.write_bsp_version_to_localconf()

if __name__ == "__main__":
    main()
