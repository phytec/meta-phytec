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

PR = "r0"
SRCREV = "a6744b4edc2c0614ebad14a4b7cb54d43e306909"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .=  "am62lx-phyflex-libra-rdk"
COMPATIBLE_MACHINE .= "|am62px-phyflex-libra-rdk"
COMPATIBLE_MACHINE .= "|phyboard-izar"
COMPATIBLE_MACHINE .= "|phyboard-lynx"
COMPATIBLE_MACHINE .= "|phyboard-rigel"
COMPATIBLE_MACHINE .= "|phycore-am57xx-1"
COMPATIBLE_MACHINE .= "|phycore-am57xx-2"
COMPATIBLE_MACHINE .= "|phycore-am57xx-3"
COMPATIBLE_MACHINE .= "|phycore-am57xx-4"
COMPATIBLE_MACHINE .= "|phycore-am57xx-5"
COMPATIBLE_MACHINE .= "|phycore-am57xx-6"
COMPATIBLE_MACHINE .= ")$"
