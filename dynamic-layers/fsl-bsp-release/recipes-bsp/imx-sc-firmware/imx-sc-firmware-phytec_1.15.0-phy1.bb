# Copyright (C) 2016 Freescale Semiconductor
# Copyright 2017-2022 NXP
# Copyright 2023 PHYTEC Messtechnick GmbH

# The firmware is build outside of yocto.
# Steps how the firmware was created
# Used toolchain: gcc-arm-none-eabi-6-2017-q2-update
# 1. tar xvjf Downloads/gcc-arm-none-eabi-6-2017-q2-update-linux.tar.bz2
# 2. export TOOLS=~/Downloads/
# 3. make qx B=phycore DL=5 V=1 R=B0 U=2 D=1 M=1  (R=B0 for both B0 and C0 revisions)

DESCRIPTION = "i.MX System Controller Firmware for PHYTEC boards"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=5a0bf11f745e68024f37b4724a5364fe"
SECTION = "BSP"

inherit pkgconfig deploy

SRC_URI = "https://download.phytec.de/Software/Linux/Driver/imx-sc-firmware_${PV}.tar.gz"

SRC_URI[md5sum] = "a6cd313850b39d0684d1941bf626662d"
SRC_URI[sha256sum] = "1c63e976576babfd9fb109b9362fdac98831d2aa2a44d5877eff9a95563b6d13"

S = "${WORKDIR}/imx-sc-firmware_${PV}"
BOARD_TYPE ?= "${MACHINE}"
SC_FIRMWARE_NAME ?= "INVALID"
SC_FIRMWARE_NAME:mx8qxp-nxp-bsp = "mx8qx-${BOARD_TYPE}-scfw-tcm.bin"

symlink_name = "scfw_tcm.bin"

BOOT_TOOLS = "imx-boot-tools"

do_compile[noexec] = "1"

do_install[noexec] = "1"

do_deploy() {
    install -Dm 0644 ${S}/${SC_FIRMWARE_NAME} ${DEPLOYDIR}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME}
    ln -sf ${SC_FIRMWARE_NAME} ${DEPLOYDIR}/${BOOT_TOOLS}/${symlink_name}
}
addtask deploy after do_install

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "mx8qxp-nxp-bsp"
