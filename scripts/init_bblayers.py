#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Copyright 2017, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import sys
import argparse
from phylib import *

class BSP_BBLayer(BoardSupportPackage):
    """
    Setup bblayers conf
    """
    def __init__(self):
        super(BSP_BBLayer, self).__init__()
        self.bblayers_conf = os.path.join(self.src.bsp_dir, "build/conf/bblayers.conf")
        #layers in those project dirs will be controlled by the bblayers.conf.example
        self.project_filter = ["poky", "meta-openembedded"]

    def init_bblayers(self):
        layers_to_add = set(self.project_paths).difference(self.project_filter)
        print("Layers to add:", layers_to_add)
        with open(self.bblayers_conf, "a") as f:
            f.write("BBLAYERS += \"\\\n")
            for l in sorted(layers_to_add):
                f.write("  ${OEROOT}/../%s \\\n" % l)
            f.write("  \"\n")

        return True

##############
# Executable #
##############

def main():
    parser = argparse.ArgumentParser(description='Init the bblayers.conf. Poky and meta-openembedded will be handled by the bblayers.conf.example. The other layers will be added from the repo manifest.xml')

    args = parser.parse_args()

    bsp = BSP_BBLayer()
    if not bsp.init_bblayers():
        sys.exit(1)

if __name__ == "__main__":
    main()
