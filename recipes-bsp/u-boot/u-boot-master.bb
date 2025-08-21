require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc
VENDOR_INC = ""
VENDOR_INC:imx-generic-bsp = "u-boot-imx.inc"
require ${VENDOR_INC}

DEPENDS += "\
    bc-native \
    dtc-native \
    gnutls-native \
    u-boot-mkimage-native \
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'optee-os python3-pyelftools-native', '', d)} \
"

GIT_URL = "git://source.denx.de/u-boot/u-boot.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

PR = "r0"

SRCREV = "${AUTOREV}"
BRANCH = "master"

DEFAULT_PREFERENCE = "-1"

PROVIDES += "u-boot"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-polis-imx8mm-5"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= "|phygate-tauri-l-imx8mm-2"
COMPATIBLE_MACHINE .= "|imx8mp-libra-fpsc-1"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx93-2"
COMPATIBLE_MACHINE .= ")$"
