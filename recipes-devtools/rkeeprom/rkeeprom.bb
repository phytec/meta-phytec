DESCRIPTION = "rkeeprom.py is a configuration tool for PHYTEC's RK3288 SoMs"
HOMEPAGE = "http://www.phytec.de"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SECTION = "devel"
PR = "r0"

SRC_URI = "file://rkeeprom.py"

S = "${WORKDIR}"

RDEPENDS_${PN} = "python python-smbus python-argparse"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 rkeeprom.py ${D}${bindir}
}

FILES_${PN} = "${bindir}"

COMPATIBLE_MACHINE = "rk3288"
