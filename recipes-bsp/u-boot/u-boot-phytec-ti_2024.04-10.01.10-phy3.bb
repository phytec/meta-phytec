inherit phygittag
require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc
require u-boot-phytec-ti.inc
require u-boot-common-phytec.inc
require u-boot-securiphy.inc

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

GIT_URL = "git://github.com/phytec/u-boot-phytec-ti.git;protocol=https"
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
SRCREV = "ba0360768438fc9ad3b0e8b58a89f3928ce8e399"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra"
COMPATIBLE_MACHINE .= "|phyboard-electra"
COMPATIBLE_MACHINE .= "|phyboard-lynx"
COMPATIBLE_MACHINE .= "|phyboard-rigel"
COMPATIBLE_MACHINE .= ")$"
