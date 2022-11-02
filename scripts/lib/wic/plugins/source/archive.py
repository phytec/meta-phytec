#
# Copyright (c) 2014, Intel Corporation.
# Copyright (c) 2022, PHYTEC Messtechnik GmbH
#
# SPDX-License-Identifier: GPL-2.0-only
#
# DESCRIPTION
# This plugin takes the content from a tar file defined in sourceparams and
# writes it into a new partition. Based on the rootfs plugin.
#
# AUTHORS
# Leonard Anderweit <l.anderweit (at] phytec.de>
#

import os

from wic import WicError
from wic.pluginbase import SourcePlugin
from wic.misc import get_bitbake_var, exec_native_cmd

class ConfigPartitionPlugin(SourcePlugin):
    """
    Populate partition content from a tar file
    """

    name = 'archive'

    @staticmethod
    def __get_pseudo(native_sysroot, rootfs, pseudo_dir):
        pseudo = "export PSEUDO_PREFIX=%s/usr;" % native_sysroot
        pseudo += "export PSEUDO_LOCALSTATEDIR=%s;" % pseudo_dir
        pseudo += "export PSEUDO_PASSWD=%s;" % rootfs
        pseudo += "export PSEUDO_NOSYMLINKEXP=1;"
        pseudo += "%s " % get_bitbake_var("FAKEROOTCMD")
        return pseudo

    @classmethod
    def do_prepare_partition(cls, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             krootfs_dir, native_sysroot):
        """
        Populate partition from tar archive
        """
        if 'file' not in source_params:
            raise WicError("No tar file specified")
        tar_file = os.path.join(kernel_dir, source_params['file'])
        if not os.path.exists(tar_file):
            raise WicError("Couldn't find tar file at %s" % tar_file)

        rootfs = os.path.realpath(os.path.join(cr_workdir, "rootfs%d" % part.lineno))
        os.mkdir(rootfs)
        pseudo_dir = os.path.realpath(os.path.join(cr_workdir, "pseudo%d" % part.lineno))
        os.mkdir(pseudo_dir)
        untar_cmd = "tar axf %s -C %s" % (tar_file, rootfs)
        pseudo = cls.__get_pseudo(native_sysroot, rootfs, pseudo_dir)
        exec_native_cmd(untar_cmd, native_sysroot, pseudo)

        part.prepare_rootfs(cr_workdir, oe_builddir,
            rootfs, native_sysroot, pseudo_dir = pseudo_dir)
