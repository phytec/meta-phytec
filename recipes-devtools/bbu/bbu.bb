DESCRIPTION = "The barebox update script is intended to be the userspace tool \
		for barebox updates in our BSP."
HOMEPAGE = "http://www.phytec.de"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
SECTION = "devel"
PR = "r0"

SRC_URI = "file://bbu.sh"
SRC_URI:append:mx6-generic-bsp = " file://bbu_emmc.sh"
SRC_URI:append:mx6ul-generic-bsp = " file://bbu_emmc.sh"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

# Depends on tools like hexdump, tr, grep, awk which are part of our
# busybox configuration
RDEPENDS:${PN} = "busybox mtd-utils"
RDEPENDS:${PN}:append:mx6-generic-bsp = " barebox-targettools"
RDEPENDS:${PN}:append:mx6ul-generic-bsp = " barebox-targettools"
RDEPENDS:${PN}:append:ti33x = " barebox-targettools"
RRECOMMENDS:${PN}:append:mx6-generic-bsp = " barebox"
RRECOMMENDS:${PN}:append:mx6ul-generic-bsp = " barebox"
RRECOMMENDS:${PN}:append:ti33x = " barebox"
RDEPENDS:${PN}:append:imx-generic-bsp = " imx-kobs"
RDEPENDS:${PN}:append:mx6-generic-bsp = "${@bb.utils.contains('MACHINE_FEATURES', 'emmc', ' mmc-utils', '', d)}"
RDEPENDS:${PN}:append:mx6ul-generic-bsp = "${@bb.utils.contains('MACHINE_FEATURES', 'emmc', ' mmc-utils', '', d)}"

BBU = "bbu.sh"
BBU:mx6-generic-bsp = "${@bb.utils.contains('MACHINE_FEATURES', 'emmc', 'bbu_emmc.sh', 'bbu.sh', d)}"
BBU:mx6ul-generic-bsp = "${@bb.utils.contains('MACHINE_FEATURES', 'emmc', 'bbu_emmc.sh', 'bbu.sh', d)}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${BBU} ${D}${bindir}/bbu.sh
}

FILES:${PN} = "${bindir}"

COMPATIBLE_MACHINE = "(ti33x|imx-generic-bsp)"
