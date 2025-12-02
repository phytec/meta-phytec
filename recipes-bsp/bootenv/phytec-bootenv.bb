SUMMARY = "Provides machine depended external environment for u-boot on PHYTEC hardware"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://bootenv.txt \
    file://overlays.txt \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

BOOTENV_OVERLAYS_APPEND ?= ""

BOOTENV_FILE ?= "bootenv.txt"
BOOTENV_FILE:k3 = "overlays.txt"
BOOTENV_FILE:mx91-generic-bsp = "overlays.txt"
BOOTENV_FILE:mx93-generic-bsp = "overlays.txt"

inherit deploy
do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/${BOOTENV_FILE} ${DEPLOYDIR}

    if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage"; then
        # Replace whitespaces by a single #.
        OVERLAYS_APPEND=$(echo "${BOOTENV_OVERLAYS_APPEND}" | sed -e "s/\s\+/#/g")

        sed -i -e "s/\(overlays=.*\)/\1#${OVERLAYS_APPEND}/" -e "s/\(overlays=#\)/\overlays=/" ${DEPLOYDIR}/${BOOTENV_FILE}

        # Remove trailing whitespaces.
        sed -i -e "s/\ *$//g" ${DEPLOYDIR}/${BOOTENV_FILE}

        # Remove trailing hashtags
        sed -i -e "s/#*$//g" ${DEPLOYDIR}/${BOOTENV_FILE}
    else
        # Replace multiple whitespaces by single one.
        OVERLAYS_APPEND=$(echo "${BOOTENV_OVERLAYS_APPEND}" | sed -e "s/\s\+/ /g")

        sed -i -e "s/\(overlays=.*\)/\1 ${OVERLAYS_APPEND}/" ${DEPLOYDIR}/${BOOTENV_FILE}

        # Remove trailing whitespaces.
        sed -i -e "s/\ *$//g" ${DEPLOYDIR}/${BOOTENV_FILE}
    fi
}
addtask deploy before do_build after do_unpack

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "mx7-nxp-bsp"
COMPATIBLE_MACHINE .= "|mx8m-nxp-bsp"
COMPATIBLE_MACHINE .= "|mx8x-nxp-bsp"
COMPATIBLE_MACHINE .= "|mx91-nxp-bsp"
COMPATIBLE_MACHINE .= "|mx93-nxp-bsp"
COMPATIBLE_MACHINE .= "|mx95-nxp-bsp"
COMPATIBLE_MACHINE .= "|k3"
COMPATIBLE_MACHINE .= "|phycore-stm32mp15"
COMPATIBLE_MACHINE .= "|phycore-stm32mp13"
COMPATIBLE_MACHINE .= "|stm32mp25x-libra"
COMPATIBLE_MACHINE .= ")$"
