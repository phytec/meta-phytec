inherit phygittag
require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot-ti.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=5a7450c57ffe5ae63fd732446b988025"

GIT_URL = "git://github.com/phytec/u-boot-phytec-ti.git;protocol=https"
BRANCH = "v2021.01_08.06.00.002-phy"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot:"
SRC_URI:append:phyboard-electra-am64xx-1 = "\
    file://0001-HACK-board-phycore_am64x-Add-Set-CLKOUT0-to-25MHz.patch \
"
SRC_URI:append:phyboard-electra-am64xx-1-k3r5 = "\
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

PR = "r0"
SRCREV = "d9e835f13531b5083d437b98012e398d7595198f"

SPL_UART_BINARY_k3r5 = "u-boot-spl.bin"

do_deploy:append:k3r5 () {
	mv ${DEPLOYDIR}/tiboot3.bin ${DEPLOYDIR}/tiboot3-r5spl.bin || true
	mv ${DEPLOYDIR}/u-boot-spl.bin ${DEPLOYDIR}/u-boot-spl-r5spl.bin || true
}

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-lyra-am62xx-1"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-1-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-2"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62xx-2-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-1"
COMPATIBLE_MACHINE .= "|phyboard-lyra-am62axx-1-k3r5"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-1-k3r5"
COMPATIBLE_MACHINE .= ")$"
