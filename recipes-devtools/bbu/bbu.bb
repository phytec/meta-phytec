DESCRIPTION = "The barebox update script is intended to be the userspace tool \
		for barebox updates in our BSP."
HOMEPAGE = "http://www.phytec.de"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SECTION = "devel"
PR = "r0"

SRC_URI = "file://bbu.sh"

S = "${WORKDIR}"

# Depends on tools like hexdump, tr, grep, awk which are part of our
# busybox configuration
RDEPENDS_${PN} = "busybox mtd-utils"
RDEPENDS_${PN}_append_imx = " imx-kobs"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 bbu.sh ${D}${bindir}
}

FILES_${PN} = "${bindir}"

COMPATIBLE_MACHINE = "(ti33x|imx)"
