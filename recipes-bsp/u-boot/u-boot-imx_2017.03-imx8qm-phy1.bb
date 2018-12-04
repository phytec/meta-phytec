DESCRIPTION = "i.MX U-Boot suppporting i.MX reference boards."

inherit phygittag
require recipes-bsp/u-boot/u-boot.inc
inherit pythonnative

PROVIDES += "u-boot"
DEPENDS_append = " python dtc-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "r0"
SRCREV = "9d38a7db28e4461b851a74cb284bf9e21662c6b2"

S = "${WORKDIR}/git"

BOOT_TOOLS = "imx-boot-tools"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "phycore-imx8-1"

UBOOT_NAME_mx8 = "u-boot-${MACHINE}.bin-${UBOOT_CONFIG}"
