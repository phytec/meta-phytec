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
PATCHES:am64xx = " \
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"
PATCHES:k3r5-am64xx = " \
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"
SRC_URI:append:phyboard-electra = " ${PATCHES}"

PR = "r0"
SRCREV = "5ba0e9f74e586332b11e8bfc61d886913584986d"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .=  "phyboard-lyra"
COMPATIBLE_MACHINE .= "|phyboard-electra"
COMPATIBLE_MACHINE .= "|phyboard-lynx"
COMPATIBLE_MACHINE .= "|phyboard-rigel"
COMPATIBLE_MACHINE .= ")$"
