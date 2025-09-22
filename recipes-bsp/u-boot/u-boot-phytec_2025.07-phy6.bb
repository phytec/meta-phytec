DESCRIPTION = "U-Boot supporting PHYTEC boards."
LICENSE = "GPL-2.0-or-later"

inherit phygittag
require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc
require u-boot-common-phytec.inc
require u-boot-securiphy.inc
require u-boot-rauc.inc

VENDOR_INC = ""
VENDOR_INC:ti-soc = "u-boot-phytec-ti.inc"
require ${VENDOR_INC}

inherit ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '1', 'imx-boot-container', '')}

GIT_URL = "git://github.com/phytec/u-boot-phytec.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PR = "r0"
SRCREV = "c47d18a7e0b880eb02ceea6d1f200511dd6c2b0f"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .=  "phyboard-electra"
COMPATIBLE_MACHINE .= "|phyboard-lyra"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"
