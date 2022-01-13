DESCRIPTION = "The barebox update script is intended to be the userspace tool \
		for barebox updates in our BSP."
HOMEPAGE = "http://www.phytec.de"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
SECTION = "devel"
PR = "r0"

SRC_URI = "file://bbu.sh"
SRC_URI_append_mx6 = " file://bbu_emmc.sh"
SRC_URI_append_mx6ul = " file://bbu_emmc.sh"

S = "${WORKDIR}"

# Depends on tools like hexdump, tr, grep, awk which are part of our
# busybox configuration
RDEPENDS_${PN} = "busybox mtd-utils"
RDEPENDS_${PN}_append_mx6 = " barebox-targettools"
RDEPENDS_${PN}_append_mx6ul = " barebox-targettools"
RDEPENDS_${PN}_append_ti33x = " barebox-targettools"
RRECOMMENDS_${PN}_append_mx6 = " barebox"
RRECOMMENDS_${PN}_append_mx6ul = " barebox"
RRECOMMENDS_${PN}_append_ti33x = " barebox"
RDEPENDS_${PN}_append_imx = " imx-kobs"
RDEPENDS_${PN}_append_mx6 = "${@bb.utils.contains('MACHINE_FEATURES', 'emmc', ' mmc-utils', '', d)}"
RDEPENDS_${PN}_append_mx6ul = "${@bb.utils.contains('MACHINE_FEATURES', 'emmc', ' mmc-utils', '', d)}"

BBU = "bbu.sh"
BBU_mx6 = "${@bb.utils.contains('MACHINE_FEATURES', 'emmc', 'bbu_emmc.sh', 'bbu.sh', d)}"
BBU_mx6ul = "${@bb.utils.contains('MACHINE_FEATURES', 'emmc', 'bbu_emmc.sh', 'bbu.sh', d)}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${BBU} ${D}${bindir}/bbu.sh
}

FILES_${PN} = "${bindir}"

COMPATIBLE_MACHINE = "(ti33x|imx)"
