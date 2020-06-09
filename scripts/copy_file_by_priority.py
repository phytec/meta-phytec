#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Copyright 2017, PHYTEC Messtechnik GmbH
# Author: Jan Remmet <j.remmet@phytec.de>

import sys
import argparse
import shutil
from phylib import *


class BSP_BBLayerFile(BoardSupportPackage):

    """
    Setup project priority list
    """

    def __init__(self):
        super(BSP_BBLayerFile, self).__init__()
        project_priority = []
        # ignore repos with layer.conf in unusual places
        project_filter = ["poky", "meta-openembedded", "meta-imx", "base"]
        for p in set(self.project_paths).difference(project_filter):
            abs_project_path = os.path.join(self.src.bsp_dir, 'sources', p)
            layer_conf = os.path.join(abs_project_path, 'conf/layer.conf')
            priority = self.__get_priority(layer_conf)
            if priority:
                project_priority.append((priority, abs_project_path))
        self.project_paths_priority = sorted(project_priority, reverse=True)

    def __get_priority(self, layer_conf):
        with open(layer_conf) as lcfile:
            for line in lcfile:
                name, val = line.partition("=")[::2]
                if 'BBFILE_PRIORITY_' in name:
                    priority = int(val.strip('" \n'))
                    lcfile.close
                    return priority
            lcfile.close
        return None

    def copy_file(self, search_file, destination):
        for _, ppath in self.project_paths_priority:
            found = os.path.join(ppath, search_file)
            try:
                shutil.copy2(found, destination)
                print("copy {} to {}".format(found, destination))
                return
            except IOError:
                pass
        raise IOError("file {} not found in any layer".format(search_file))

    def print_all_files(self, search_file):
        for prio, ppath in self.project_paths_priority:
            dest_file = os.path.join(ppath, search_file)
            if os.path.isfile(dest_file):
                print("{} priority:{}".format(dest_file, prio))


# Executable
def main():
    parser = argparse.ArgumentParser(
        description='Find file in all included layers. Copy it from the layer with highest BBFILE_PRIORITY to the destination')

    parser.add_argument('file', help="filename with relative path")
    parser.add_argument('dest', nargs='?', help="copy to destination")

    args = parser.parse_args()

    bsp = BSP_BBLayerFile()
    if args.dest:
        bsp.copy_file(args.file, args.dest)
    else:
        bsp.print_all_files(args.file)

if __name__ == "__main__":
    main()
