SUMMARY = "PHYTEC Devicetree Overlays for phyCORE-STM32MP1"
HOMEPAGE = "http://www.phytec.de/"
SECTION = "kernel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(stm32mpcommon)"

inherit devicetree

SRC_URI += "file://README_stm32mp15xx_sargas.md"
SRC_URI += "file://README_stm32mp13xx_segin.md"
SRC_URI += "file://README_stm32mp25xx_libra.md"

README:phycore-stm32mp15 = "README_stm32mp15xx_sargas.md"
README:phycore-stm32mp13 = "README_stm32mp13xx_segin.md"
README:stm32mp25x-libra = "README_stm32mp25xx_libra.md"

DT_FILES_PATH = "${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts/st/overlays/"

DT_OVERLAYS_INSTALL_DIR ?= "boot/overlays"

PHY_EXPANSIONS ?= ""
DEFAULT_PHY_EXPANSIONS ?= ""

do_install() {
    install -d ${D}/${DT_OVERLAYS_INSTALL_DIR}/
    echo "overlay=${DEFAULT_PHY_EXPANSIONS}" > ${WORKDIR}/${LINUX_VERSION}/overlays.txt
    install -m 0644 ${WORKDIR}/${LINUX_VERSION}/overlays.txt ${D}/${DT_OVERLAYS_INSTALL_DIR}/overlays.txt

    for DTB_FILE in ${DT_OVERLAYS_INSTALL}; do
        install -m 0644 ${B}/${DTB_FILE} ${D}/${DT_OVERLAYS_INSTALL_DIR}/${DTB_FILE}
    done

    install -m 0644 ${WORKDIR}/${README} ${D}/${DT_OVERLAYS_INSTALL_DIR}/README.md
}

do_deploy() {
    for DTB_FILE in ${DT_OVERLAYS_INSTALL}; do
        install -Dm 0644 ${B}/${DTB_FILE} ${DEPLOYDIR}/dt-overlays/${DTB_FILE}
    done
}

PACKAGES =+ "${PN}-imagebootfs"
FILES:${PN}-imagebootfs = "${DT_OVERLAYS_INSTALL_DIR}/ ${DT_OVERLAYS_INSTALL_DIR}/*.dtbo ${DT_OVERLAYS_INSTALL_DIR}/overlays.txt"
