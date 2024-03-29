DESCRIPTION = "U-Boot supporting PHYTEC boards."
LICENSE = "GPL-2.0-or-later"

inherit phygittag
require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc
require u-boot-rauc.inc

inherit ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '1', 'imx-boot-container', '')}

DEPENDS:append = " flex-native bison-native bc-native dtc-native python3-setuptools-native"

GIT_URL = "git://github.com/phytec/u-boot-phytec.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "r0"
SRCREV = "51f4f0c241010a3eaf91f6a971e2fd4ac20b69f2"

PROVIDES += "u-boot"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"
