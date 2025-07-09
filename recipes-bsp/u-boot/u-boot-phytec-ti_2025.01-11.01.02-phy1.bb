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

SRC_URI:append:am62lxx-libra-fpsc-2 = " \
    file://0001-dts-arm64-am62l-phycore-fpsc-Configure-1GiB-RAM.patch \
"

PR = "r0"
SRCREV = "9665efacb477c087a10ed09f4f2c5bb2f63323c4"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .=  "am62lx-libra"
COMPATIBLE_MACHINE .= ")$"
