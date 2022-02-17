inherit phygittag
require recipes-bsp/u-boot/u-boot-ti.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=5a7450c57ffe5ae63fd732446b988025"

GIT_URL = "git://github.com/phytec/u-boot-phytec-ti.git;protocol=https"
BRANCH = "v2021.01-phy"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot:"
SRC_URI_append_phyboard-electra-am64xx-1 = "\
    file://0001-HACK-board-phycore_am64xx-Add-Set-CLKOUT0-to-25MHz.patch \
"
SRC_URI_append_phyboard-electra-am64xx-1-k3r5 = "\
    file://0001-HACK-board-phycore_am64xx-Add-Set-CLKOUT0-to-25MHz.patch \
"

PR = "r0"
SRCREV = "d41a57f8241cfc7f74162777f7a2a28055ab5186"

SPL_UART_BINARY_k3r5 = "u-boot-spl.bin"

do_deploy_append_k3r5 () {
	mv ${DEPLOYDIR}/tiboot3.bin ${DEPLOYDIR}/tiboot3-r5spl.bin || true
	mv ${DEPLOYDIR}/u-boot-spl.bin ${DEPLOYDIR}/u-boot-spl-r5spl.bin || true
}

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "phyboard-electra-am64xx-1"
COMPATIBLE_MACHINE .= "|phyboard-electra-am64xx-1-k3r5"
COMPATIBLE_MACHINE .= ")$"
