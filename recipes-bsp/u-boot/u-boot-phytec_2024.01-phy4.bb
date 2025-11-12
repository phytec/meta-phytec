DESCRIPTION = "U-Boot supporting PHYTEC boards."
LICENSE = "GPL-2.0-or-later"

inherit phygittag
require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc
require u-boot-common-phytec.inc
require u-boot-rauc.inc

inherit ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '1', 'imx-boot-container', '')}

GIT_URL = "git://github.com/phytec/u-boot-phytec.git;protocol=https"
GIT_URL:phynext = "git://git@github.com/phytec/u-boot-phytec-dev.git;protocol=ssh"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "r0"
SRCREV = "8c7a6525b138d45b0dddf1129703fd879efeb15f"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"
