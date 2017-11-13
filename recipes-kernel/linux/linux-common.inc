# Copyright (C) 2014-2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
DESCRIPTION = "Linux Kernel Phytec common base"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
inherit kconfig

INC_PR = "r0"

DEPENDS += "lzop-native lz4-native"
PATCHTOOL = "git"

SRCTREECOVEREDTASKS ?= "do_unpack do_fetch"

# Copy .dts and .dtsi from SRC_URI to the kernel boot/dts path
# This should go to poky/meta/classes/kernel-devicetree.bbclass
# returns all the elements from the src uri that are .dts or .dtsi files
def find_dtss(d):
    sources=src_patches(d, True)
    dtss_list=[]
    for s in sources:
        base, ext = os.path.splitext(s)
        if ext in [".dtsi", ".dts"]:
            dtss_list.append(s)

    return dtss_list

do_patch_append () {
    import shutil
    srcdir = d.getVar('STAGING_KERNEL_DIR', True)
    arch = d.getVar('ARCH', True)
    for dts in find_dtss(d):
        cptarget=os.path.join(srcdir, "arch", arch, "boot", "dts",
                             os.path.basename(dts))
        bb.note("copying dts from: %s to: %s" % (dts, cptarget))
        shutil.copyfile(dts, cptarget)
}

KERNEL_IMAGE_BASE_NAME = "${KERNEL_IMAGETYPE}-${PN}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
do_deploy_append() {
    install -m 644 ${B}/.config ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.config
    ln -sf ${KERNEL_IMAGE_BASE_NAME}.config ${DEPLOYDIR}/${KERNEL_IMAGETYPE}.config
}
