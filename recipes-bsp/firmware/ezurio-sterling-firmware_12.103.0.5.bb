SUMMARY = "Ezurio Sterling LWB firmware files for broadcom (Infineon cypress) 2.4G and 5G radio module"
SECTION = "kernel"

inherit allarch

LRD_LWB_URI_BASE = "https://github.com/Ezurio/SonaIF-Release-Packages/releases/download/LRD-REL-${PV}"

SRC_URI:append = " \
    ${LRD_LWB_URI_BASE}/summit-lwb-etsi-firmware-${PV}.tar.bz2;name=laird \
    ${LRD_LWB_URI_BASE}/summit-lwb5-etsi-firmware-${PV}.tar.bz2;name=laird5G \
"

SRC_URI[laird.md5sum] = "e4dda97ce1258e1699e0b5b086328ef8"
SRC_URI[laird.sha256sum] = "0ea5081d0bc5232e6979d884652e9e1d855c9ad2c50eef899aba90c9378e8076"
SRC_URI[laird5G.md5sum] = "79f0bfbe9351612d91f7798910bbb614"
SRC_URI[laird5G.sha256sum] = "13e6c54a70d4b2637f257bb40a1124ce259a66cdf5444c177d918fc1dbd4941a"

RCONFLICTS:${PN} = "linux-firmware-bcm43430 linux-firmware-bcm4339"
RREPLACES:${PN} = "linux-firmware-bcm43430 linux-firmware-bcm4339"
RPROVIDES:${PN} = "linux-firmware-bcm43430 linux-firmware-bcm4339"

LICENSE = "Cypress & Ezurio"
NO_GENERIC_LICENSE[Cypress] = "LICENSE.cypress"
NO_GENERIC_LICENSE[Ezurio] = "LICENSE.ezurio"
LIC_FILES_CHKSUM = " \
    file://LICENSE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7 \
    file://LICENSE.ezurio;md5=fd3dd0630b215465b6f50540642d5b93 \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

S = "${UNPACKDIR}"
FIRMWARE_PATH = "/lib/firmware/brcm"

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

FILES:${PN}:append = " \
    ${nonarch_base_libdir}/firmware/brcm/BCM43430A1.hcd \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.bin \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.clm_blob \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.txt \
    ${nonarch_base_libdir}/firmware/brcm/BCM4335C0.hcd \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.bin \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.txt \
"
