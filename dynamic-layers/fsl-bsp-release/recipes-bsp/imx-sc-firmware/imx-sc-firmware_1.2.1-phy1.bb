# Copyright (C) 2016 Freescale Semiconductor
# Copyright 2017-2019 NXP

# The firmware is build outside of yocto.
# Steps how the firmware was created
# Used toolchain: gcc-arm-none-eabi-6-2017-q2-update
# 1. tar xvjf Downloads/gcc-arm-none-eabi-6-2017-q2-update-linux.tar.bz2
# 2. export TOOLS=~/Downloads/
# 3. make qm B=phycore DL=5 V=1 R=B0

DESCRIPTION = "i.MX System Controller Firmware"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb0303e4ee8b0e71c094171e2272bd44"
SECTION = "BSP"

inherit fsl-eula2-unpack2 pkgconfig deploy

SRC_URI = "ftp://ftp.phytec.de/pub/Software/Linux/Driver/${PN}_${PV}.tar.gz"

SRC_URI[md5sum] = "9ee6ef2e2a28f0428455bd4869cfe965"
SRC_URI[sha256sum] = "4bdbe5c36249e3c8eee7312c1b1fb61682bde8f5837067c95ee7844b0ec6f142"

S = "${WORKDIR}/${PN}_${PV}"
BOARD_TYPE ?= "mek"
SC_FIRMWARE_NAME ?= "mx8qm-mek-scfw-tcm.bin"
SC_FIRMWARE_NAME_mx8qm = "mx8qm-${BOARD_TYPE}-scfw-tcm.bin"
SC_FIRMWARE_NAME_mx8qxp = "mx8qx-${BOARD_TYPE}-scfw-tcm.bin"
SC_FIRMWARE_NAME_mx8dxl-phantom = "mx8dxl-phantom-${BOARD_TYPE}-scfw-tcm.bin"
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

COMPATIBLE_MACHINE = "(mx8qm|mx8x)"
