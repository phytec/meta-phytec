# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION = "Linux Kernel Phytec common base"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
inherit kernel-module-split-blacklist
inherit kconfig-prepare-configure
require recipes-kernel/linux/linux-dtb.inc

# Increase INC_PR everytime the include file is changed.
INC_PR = "r2"

DEPENDS += "lzop-native lz4-native"
KERNEL_LOCALVERSION ?= "-${DISTRO}-${DISTRO_VERSION}"
S = "${WORKDIR}/git"
PATCHTOOL = "git"

# FIXME Move to meta-yogurt when socs are unified.
FILESEXTRAPATHS_prepend = "${THISDIR}/features:"
SRC_URI_append = " file://mtd-tests.cfg"
KERNEL_MODULES_RDEPENDS_BLACKLIST += "${MTD_TEST_PACKAGES}"

# these variables need to be assigned in the machine.conf 
KERNEL_DEFAULT_DEFCONFIG ?= ""


python do_prepare_configure_append() {
    localversion = d.getVar('KERNEL_LOCALVERSION', True)
    bb.note("Setting LOCALVERSION in .config to %s" % localversion)
    cmd = 'kconfig-tweak --file %s --set-str LOCALVERSION "%s"' % (config, localversion)
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)

}

do_default_defconfig () {
    bbnote "generating .config for target ${KERNEL_DEFAULT_DEFCONFIG}"
    oe_runmake ${KERNEL_DEFAULT_DEFCONFIG}
}

do_savedefconfig() {
    bbplain "saving defconfig to ${WORKDIR}/defconfig.temp"
    oe_runmake savedefconfig
    install -m 644 ${B}/defconfig ${WORKDIR}/defconfig.temp
}
addtask savedefconfig after do_configure

KERNEL_IMAGE_BASE_NAME = "${KERNEL_IMAGETYPE}-${PN}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
KERNEL_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"
do_deploy_append() {
    install -m 644 ${B}/.config ${DEPLOYDIR}/${KERNEL_IMAGE_BASE_NAME}.config
    ln -sf ${KERNEL_IMAGE_BASE_NAME}.config ${DEPLOYDIR}/${KERNEL_IMAGETYPE}.config
}
