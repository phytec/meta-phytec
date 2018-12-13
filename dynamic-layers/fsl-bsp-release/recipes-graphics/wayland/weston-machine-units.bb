SUMMARY = "Machine specific weston units"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r1"

SRC_URI = " file://weston"

do_install() {
	install -d ${D}${sysconfdir}/default
	install -m 0644 ${WORKDIR}/weston ${D}${sysconfdir}/default/
}

FILES_${PN} += "${sysconfdir}/default"
