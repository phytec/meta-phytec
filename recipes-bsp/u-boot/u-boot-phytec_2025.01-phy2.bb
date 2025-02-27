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

PATCHES = ""
PATCHES:am62xx = " \
    file://0001-HACK-board-phytec-phycore_am62x-Enable-OLDI0-AUDIO_R.patch \
"
PATCHES:k3r5-am62xx = " \
    file://0001-HACK-board-phytec-phycore_am62x-Enable-OLDI0-AUDIO_R.patch \
"
SRC_URI:append:phyboard-lyra = " ${PATCHES}"

PATCHES:am64xx = " \
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"
PATCHES:k3r5-am64xx = " \
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"
SRC_URI:append:phyboard-electra = " ${PATCHES}"

PR = "r0"
SRCREV = "6980061fa87574c69f5f37a9d0948545eb958552"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .=  "phyboard-electra"
COMPATIBLE_MACHINE .= "|phyboard-lyra"
COMPATIBLE_MACHINE .= ")$"
