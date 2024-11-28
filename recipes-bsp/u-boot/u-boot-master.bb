require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

inherit imx-boot-container

DEPENDS += "bc-native dtc-native gnutls-native"

GIT_URL = "git://source.denx.de/u-boot/u-boot.git;protocol=https"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

SRCREV = "${AUTOREV}"
BRANCH = "master"

DEFAULT_PREFERENCE = "-1"

PROVIDES += "u-boot"

COMPATIBLE_MACHINE = "^(phyboard-polis-imx8mm-5|phyboard-pollux-imx8mp-3|phygate-tauri-l-imx8mm-2)$"
