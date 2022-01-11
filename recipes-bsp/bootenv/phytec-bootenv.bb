SUMMARY = "Provides machine depended external environment for u-boot on i.MX8M PHYTEC hardware"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
	file://bootenv.txt \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}"

inherit deploy
do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/bootenv.txt ${DEPLOYDIR}
}
addtask deploy before do_build after do_unpack

COMPATIBLE_MACHINE = "mx8m"
