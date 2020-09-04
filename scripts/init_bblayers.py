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

    def init_bblayers(self):
        layers_to_add = self.project_paths
        print("Layers to add:", layers_to_add)
        with open(self.bblayers_conf, "a") as f:
            f.write("BBLAYERS += \"\\\n")
            for l in layers_to_add:
                f.write("  ${OEROOT}/../%s \\\n" % l)
            f.write("  \"\n")

        return True

##############
# Executable #
##############

def main():
    parser = argparse.ArgumentParser(description='This script dynamically creates '
             'the bblayers.conf. Active layers are defined by all projects in the '
             'manifest.xml and, in addition, the sublayers entries. Projects in '
             'the manifest can be deactivated with the <ignorebaselayer/> tag.')
    args = parser.parse_args()

    bsp = BSP_BBLayer()
    if not bsp.init_bblayers():
        sys.exit(1)

if __name__ == "__main__":
    main()
