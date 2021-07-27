# Copyright (C) 2021 Christophe Parant <c.parant@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "PHYTEC Devicetree Overlays for phyCORE-STM32MP1"
HOMEPAGE = "http://www.phytec.de/"
SECTION = "kernel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(stm32mpcommon)"

inherit devicetree

SRC_URI += "file://README.md"

DT_FILES_PATH = "${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts/overlays/"
DT_OVERLAYS_INSTALL_DIR ?= "boot/overlays"

PHY_EXPANSIONS ?= ""
DEFAULT_PHY_EXPANSIONS ?= ""

do_install() {
    install -d ${D}/${DT_OVERLAYS_INSTALL_DIR}/
    echo "overlay=${DEFAULT_PHY_EXPANSIONS}" > ${WORKDIR}/${LINUX_VERSION}/overlays.txt
    install -m 0644 ${WORKDIR}/${LINUX_VERSION}/overlays.txt ${D}/${DT_OVERLAYS_INSTALL_DIR}/overlays.txt

    for DTB_FILE in ${PHY_EXPANSIONS}; do
        install -m 0644 ${B}/${DTB_FILE} ${D}/${DT_OVERLAYS_INSTALL_DIR}/${DTB_FILE}
    done

    install -m 0644 ${WORKDIR}/README.md ${D}/${DT_OVERLAYS_INSTALL_DIR}/README.md
}

do_deploy() {
    for DTB_FILE in ${PHY_EXPANSIONS}; do
        install -Dm 0644 ${B}/${DTB_FILE} ${DEPLOYDIR}/dt-overlays/${DTB_FILE}
    done
}

PACKAGES =+ "${PN}-imagebootfs"
FILES_${PN}-imagebootfs = "${DT_OVERLAYS_INSTALL_DIR}/ ${DT_OVERLAYS_INSTALL_DIR}/*.dtbo ${DT_OVERLAYS_INSTALL_DIR}/overlays.txt"
