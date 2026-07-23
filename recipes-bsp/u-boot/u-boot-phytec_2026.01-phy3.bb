DESCRIPTION = "U-Boot supporting PHYTEC boards."
LICENSE = "GPL-2.0-or-later"

inherit phygittag
require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc
require u-boot-common-phytec.inc

VENDOR_INC = ""
VENDOR_INC:ti-soc = "u-boot-phytec-ti.inc"
require ${VENDOR_INC}

inherit ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '1', 'imx-boot-container', '')}

GIT_URL = "git://github.com/phytec/u-boot-phytec.git;protocol=https"
GIT_URL:phynext = "git://git@github.com/phytec/u-boot-phytec-dev.git;protocol=ssh"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PR = "r0"
SRCREV = "8fae1d8ea0d17d192d116e300bda7cad263a4103"

CVE_STATUS[CVE-2026-33243] = "backported-patch: fixed by commit 5dedd3da in the \
phytec fork, a backport of upstream commit 2092322b31cc"

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .=  "phyboard-electra"
COMPATIBLE_MACHINE .= "|phyboard-lyra"
COMPATIBLE_MACHINE .= "|phyboard-pollux-imx8mp-3"
COMPATIBLE_MACHINE .= ")$"
