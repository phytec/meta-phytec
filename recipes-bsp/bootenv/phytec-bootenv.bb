SUMMARY = "Provides machine depended external environment for u-boot on i.MX8M PHYTEC hardware"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
	file://bootenv.txt \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}"

BOOTENV_OVERLAYS_APPEND ?= ""

inherit deploy
do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/bootenv.txt ${DEPLOYDIR}

    # Replace multiple whitespaces by single one.
    OVERLAYS_APPEND=$(echo "${BOOTENV_OVERLAYS_APPEND}" | sed -e "s/\s\+/ /g")

    sed -i -e "s/\(overlays=.*\)/\1 ${OVERLAYS_APPEND}/" ${DEPLOYDIR}/bootenv.txt

    # Remove trailing whitespaces.
    sed -i -e "s/\ *$//g" ${DEPLOYDIR}/bootenv.txt
}
addtask deploy before do_build after do_unpack

COMPATIBLE_MACHINE = "mx8m-nxp-bsp"