SUMMARY = "EDID Firmware files for use with Linux kernel DRM KMS Helper"
SECTION = "kernel"
AUTHOR = "Dom VOVARD <dom.vovard@linrt.com>"
LICENSE = "GPL-2.0 & LGPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit allarch

SRC_URI = " \
    https://download.phytec.de/Software/Linux/Driver/${BPN}_${PV}.tar.gz \
"
SRC_URI[md5sum] = "4661ff009a30087d8a83bed74ba0668a"
SRC_URI[sha256sum] = "09547c211ce25aba4cdf4f35b1cf65cc07676e61eaa5b8f9adaf7b43def74b52"

CLEANBROKEN = "1"

S="${WORKDIR}"

do_compile() {
	:
}

do_install() {
	install -d  ${D}${nonarch_base_libdir}/firmware/edid/
	cp -r *.bin ${D}${nonarch_base_libdir}/firmware/edid/
}

FILES_${PN} += " \
	${nonarch_base_libdir}/firmware/ \
"
