# Copyright (C) 2021 Stefan Riedmueller <s.riedmueller@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "PHYTEC Devicetree Overlays"
HOMEPAGE = "http://www.phytec.de/"
SECTION = "base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit devicetree

SRC_URI += "file://README.md"

DT_FILES_PATH = "${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts/overlays/"
DT_OVERLAYS_INSTALL ?= ""

do_patch[depends] += "virtual/kernel:do_shared_workdir"
do_patch[noexec] = "1"
do_package[depends] += "virtual/kernel:do_populate_sysroot"

do_install() {
    install -d ${D}/overlays/

    for DTB_FILE in ${DT_OVERLAYS_INSTALL}; do
        install -m 0644 ${B}/${DTB_FILE} ${D}/overlays/${DTB_FILE}
    done

    install -m 0644 ${WORKDIR}/README.md ${D}/overlays/README.md
}

do_deploy() {
    for DTB_FILE in ${DT_OVERLAYS_INSTALL}; do
        install -Dm 0644 ${B}/${DTB_FILE} ${DEPLOYDIR}/dt-overlays/${DTB_FILE}
    done
}

FILES_${PN} = "/overlays/*.dtbo /overlays/README.md"

COMPATIBLE_MACHINE  = "^(mx6|mx6ul)$"
