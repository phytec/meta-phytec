# Copyright (C) 2014-2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
DESCRIPTION = "Linux Kernel Phytec common base"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM ?= "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

CVE_VERSION = "${@d.getVar("PV").split('-')[0]}"
KERNEL_LOCALVERSION ?= "-${BRANCH}-g${@oe.utils.conditional('SRCREV', 'AUTOINC', 'AUTOINC', d.getVar('SRCREV', False).__str__()[:8], d)}"

INC_PR = "r0"

DEPENDS += "lzop-native lz4-native"
PATCHTOOL = "git"

SRCTREECOVEREDTASKS ?= "do_unpack do_fetch"

include linux-common-secureboot.inc
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

def set_path(d, dts):
    k_dt = d.getVar('KERNEL_DEVICETREE', '')
    k_dt_list = k_dt.split()
    dtsb = os.path.basename(dts)
    base, ext = os.path.splitext(dtsb)
    dtsb = base + ".dtb"
    for tmp in k_dt_list:
        if dtsb in tmp:
            head, tail = os.path.split(tmp)
            return head
    return ''

python do_dtsfixup () {
    import shutil
    srcdir = d.getVar('STAGING_KERNEL_DIR')
    arch = d.getVar('ARCH')
    for dts in find_dtss(d):
        cptarget=os.path.join(srcdir, "arch", arch, "boot", "dts",
                              set_path(d, dts), os.path.basename(dts))
        bb.note("copying dts from: %s to: %s" % (dts, cptarget))
        shutil.copyfile(dts, cptarget)
}

addtask dtsfixup after do_symlink_kernsrc before do_compile

def get_oftree(d):
    kdt = d.getVar('KERNEL_DEVICETREE', '')
    return os.path.basename(kdt.split()[0])

KERNEL_ARTIFACT_LINK_NAME = ""
KERNEL_IMAGE_NAME = "${KERNEL_IMAGETYPE}-${PN}-${PKGV}-${PKGR}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
FIRST_DTS = "${@get_oftree(d)}"
do_deploy:append() {
    install -m 644 ${B}/.config ${DEPLOYDIR}/${KERNEL_IMAGE_NAME}.config
    ln -sf ${KERNEL_IMAGE_NAME}.config ${DEPLOYDIR}/${KERNEL_IMAGETYPE}.config
    if [ -z "${EXTERNAL_KERNEL_DEVICETREE}" ]; then
        ln -sf ${FIRST_DTS}  ${DEPLOYDIR}/oftree
    fi
}

# taken from meta-freescale/recipes-kernel/linux/linux-imx.inc
# A function to strip the new 32-bit dtb sub-folders in KERNEL_DEVICETREE
# for older kernel builds.
# Set KERNEL_DEVICETREE_32BIT_COMPATIBILITY_UPDATE = "1" to enable.
KERNEL_DEVICETREE_32BIT_COMPATIBILITY_UPDATE ?= "0"
python kernel_devicetree_32bit_compatibility_update() {
    import os.path
    import re
    if d.getVar('KERNEL_DEVICETREE_32BIT_COMPATIBILITY_UPDATE') != "1" or d.getVar('TUNE_ARCH') != "arm":
        return
    input = d.getVar('KERNEL_DEVICETREE').split()
    output = ""
    stripped = ""
    for original in input:
        if re.match("^.*/", original):
            stripped = os.path.basename(original)
            output += stripped + " "
            bb.debug(1, "Devicetrees are moved to sub-folder, stripping the sub-folder for older kernel: %s -> %s" % (original, stripped))
        else:
            output += original + " "
    if stripped:
        bb.warn("Updating KERNEL_DEVICETREE, removing sub-folders for older kernel. Use -D for more details. Set KERNEL_DEVICETREE_32BIT_COMPATIBILITY_UPDATE = \"0\" to disable.")
        d.setVar('KERNEL_DEVICETREE', output)
}
addhandler kernel_devicetree_32bit_compatibility_update
kernel_devicetree_32bit_compatibility_update[eventmask] = "bb.event.RecipeParsed"
