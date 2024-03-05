DESCRIPTION = "i.MX U-Boot supporting i.MX reference boards."

inherit phygittag
require recipes-bsp/u-boot/u-boot.inc
inherit python3native
include u-boot-rauc.inc

PROVIDES += "u-boot"
DEPENDS:append = " flex-native bison-native bc-native dtc-native gnutls-native"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"
BRANCH = "v2023.04_2.2.0-phy"
GIT_URL = "git://git.phytec.de/${BPN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "r0"
SRCREV = "551902870176c1cef09117b6ad1e8bcd6127ad73"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-segin-imx93-2"
COMPATIBLE_MACHINE .= ")$"
