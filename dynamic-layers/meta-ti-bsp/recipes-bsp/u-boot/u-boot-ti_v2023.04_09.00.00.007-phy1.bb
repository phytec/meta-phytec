inherit phygittag
require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot-ti.inc

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

GIT_URL = "git://github.com/phytec/u-boot-phytec-ti.git;protocol=https"
BRANCH = "v2023.04_09.00.00.007-phy"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot:"
SRC_URI:append:phyboard-electra-am64xx-1 = "\
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"
SRC_URI:append:phyboard-electra-am64xx-1-k3r5 = "\
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"

SRC_URI:append:phyboard-electra-am64xx-2 = "\
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"
SRC_URI:append:phyboard-electra-am64xx-2-k3r5 = "\
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"

SRC_URI:append:phyboard-lyra-am62xx-1 = "\
    file://0001-HACK-board-phytec-phycore_am62x-Enable-OLDI0-AUDIO_R.patch \
"
SRC_URI:append:phyboard-lyra-am62xx-1-k3r5 = "\
    file://0001-HACK-board-phytec-phycore_am62x-Enable-OLDI0-AUDIO_R.patch \
"

SRC_URI:append:phyboard-lyra-am62xx-2 = "\
    file://0001-HACK-board-phytec-phycore_am62x-Enable-OLDI0-AUDIO_R.patch \
"
SRC_URI:append:phyboard-lyra-am62xx-2-k3r5 = "\
    file://0001-HACK-board-phytec-phycore_am62x-Enable-OLDI0-AUDIO_R.patch \
"

SRC_URI:append:phyboard-lyra-am62xx-3 = "\
    file://0001-HACK-board-phytec-phycore_am62x-Enable-OLDI0-AUDIO_R.patch \
"
SRC_URI:append:phyboard-lyra-am62xx-3-k3r5 = "\
    file://0001-HACK-board-phytec-phycore_am62x-Enable-OLDI0-AUDIO_R.patch \
"

PR = "r0"
SRCREV = "680d7f0d2bca15cf6b20e3b1ddd6800c25de7ef3"

SPL_UART_BINARY_k3r5 = "u-boot-spl.bin"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-lyra-am62xx-1"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-1-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-2"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-2-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-3"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-3-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-1"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-1-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-2"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-2-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-1-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-2"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-2-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-izar-am68x-1"
COMPATIBLE_MACHINE .= "|phyboard-izar-am68x-1-k3r5"
COMPATIBLE_MACHINE .= ")$"