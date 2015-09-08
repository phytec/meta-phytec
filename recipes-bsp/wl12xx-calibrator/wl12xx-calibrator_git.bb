# Copyright (C) 2015 Wadim Egorov <w.egorov@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Configuration utility for TI wireless drivers (wl12xx)"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=4725015cb0be7be389cf06deeae3683d"

DEPENDS += "libnl"

PV = "R8.5+git${SRCPV}"
PR = "r0"

# Tag: R8.5
SRCREV = "dcf0800f30ba449cd7f3a20f8b3f4853dc829652"
SRC_URI = "git://git.ti.com/wilink8-wlan/18xx-ti-utils.git"

S = "${WORKDIR}/git"

CFLAGS += "-I${STAGING_INCDIR}/libnl3 -DCONFIG_LIBNL32"

do_compile() {
	oe_runmake all NLVER=3
}

FILES_${PN} = "${datadir}/wl127x-inis/* \
	${bindir}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 calibrator ${D}${bindir}

	install -d ${D}${datadir}/wl127x-inis
	install -m 0644 hw/ini_files/127x/*.ini ${D}${datadir}/wl127x-inis
}
