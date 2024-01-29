DESCRIPTION = "U-Boot supporting PHYTEC boards."

inherit phygittag
require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '1', 'imx-boot-container', '')}

DEPENDS:append = " flex-native bison-native bc-native dtc-native python3-setuptools-native"

GIT_URL = "https://github.com/phytec/u-boot-phytec.git"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

PR = "r0"
SRCREV = "866ca972d6c3cabeaf6dbac431e8e08bb30b3c8e"

PROVIDES += "u-boot"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"
