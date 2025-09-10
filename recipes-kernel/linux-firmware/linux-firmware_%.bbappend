LICENSE += "& LICENCE.ti-connectivity"
LICENSE_${PN}-wl12xx = "LICENCE.ti-connectivity"
LICENSE_${PN}-wl18xx = "LICENCE.ti-connectivity"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-firmware:"
SRC_URI_append = " \
	file://wl1271-nvs.bin \
	file://wl127x-fw-5-plt.bin \
"

do_install_append () {
	install -m 0644 ${WORKDIR}/wl1271-nvs.bin ${D}/${nonarch_base_libdir}/firmware/ti-connectivity/wl1271-nvs.bin
	install -m 0644 ${WORKDIR}/wl127x-fw-5-plt.bin ${D}/${nonarch_base_libdir}/firmware/ti-connectivity/wl127x-fw-5-plt.bin
}
