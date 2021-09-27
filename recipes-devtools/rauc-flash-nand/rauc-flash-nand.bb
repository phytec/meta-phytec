DESCRIPTION = "Initialize NAND flash with a redundant A/B volume layout."
HOMEPAGE = "https://www.phytec.de"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://rauc-flash-nand.sh"

S = "${WORKDIR}"

RDEPENDS_${PN} = "busybox mtd-utils"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 rauc-flash-nand.sh ${D}${bindir}/${PN}
}

FILES_${PN} = "${bindir}"
