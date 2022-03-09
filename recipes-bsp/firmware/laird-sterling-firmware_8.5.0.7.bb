SUMMARY = "Laird Sterling LWB firmware files for braodcom (cypress) 2.4G and 5G radio module"
SECTION = "kernel"

inherit allarch

LRD_LWB_URI_BASE = "https://github.com/LairdCP/Sterling-LWB-and-LWB5-Release-Packages/releases/download/LRD-REL-${PV}"

SRC_URI_append = " \
    ${LRD_LWB_URI_BASE}/laird-lwb-etsi-firmware-${PV}.tar.bz2;name=laird \
    ${LRD_LWB_URI_BASE}/laird-lwb5-etsi-firmware-${PV}.tar.bz2;name=laird5G \
"

SRC_URI[laird.md5sum] = "7654dabc934e535a97eda31d5475a8b1"
SRC_URI[laird.sha256sum] = "12ccc8931ce0ed90cab897ab6d165a97db021271d99fe03b5495583eb1ec4d94"
SRC_URI[laird5G.md5sum] = "2b10109554f8677aeb942c7f74d39383"
SRC_URI[laird5G.sha256sum] = "bd609b249b176500f03cf21c76a88356d09ee4d8a1623ced2f781c8bd32b3f9c"

LICENSE = "Laird"
NO_GENERIC_LICENSE[Laird] = "LICENSE"
LIC_FILES_CHKSUM = "file://LICENSE;md5=53d3628b28a0bc3caea61587feade5f9"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

S = "${WORKDIR}"
FIRMWARE_PATH = "${nonarch_base_libdir}/firmware/brcm"

do_install() {
    # Clear country code entry
    sed -i 's:\(ccode=\).\+:\1:' ${S}${FIRMWARE_PATH}/*-sdio.txt

    install -d ${D}${nonarch_base_libdir}/firmware/brcm/

    install -m 644 ${S}${FIRMWARE_PATH}/BCM43430A1.hcd ${D}${nonarch_base_libdir}/firmware/brcm/
    install -m 644 ${S}${FIRMWARE_PATH}/brcmfmac43430-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/
    install -m 644 ${S}${FIRMWARE_PATH}/brcmfmac43430-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/
    install -m 644 ${S}${FIRMWARE_PATH}/brcmfmac43430-sdio.txt ${D}${nonarch_base_libdir}/firmware/brcm/

    install -m 644 ${S}${FIRMWARE_PATH}/BCM4335C0.hcd ${D}${nonarch_base_libdir}/firmware/brcm/
    install -m 644 ${S}${FIRMWARE_PATH}/brcmfmac4339-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/
    install -m 644 ${S}${FIRMWARE_PATH}/brcmfmac4339-sdio.txt ${D}${nonarch_base_libdir}/firmware/brcm/
}

FILES_${PN}_append = " \
    ${nonarch_base_libdir}/firmware/brcm/BCM43430A1.hcd \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.bin \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.clm_blob \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.txt \
    ${nonarch_base_libdir}/firmware/brcm/BCM4335C0.hcd \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.bin \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.txt \
"
