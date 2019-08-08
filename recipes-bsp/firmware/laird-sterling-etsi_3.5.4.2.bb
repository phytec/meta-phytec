DESCRIPTION = "Propriety firmware binaries needed for the Laird Sterling-LWB Wifi/BT Module"
LICENSE = "LICENSE.laird"
LIC_FILES_CHKSUM = "file://LICENSE.laird;md5=ddab19c1a336cc25309c3de36ff9b4b0"

SRC_URI = "ftp://ftp.phytec.de/pub/Software/Linux/Driver/Sterling-bcm43430.zip "

SRC_URI[md5sum] = "dade11bdc823fa7557c730319413c5e0"
SRC_URI[sha256sum] = "a74bb5208f86167f1bf2f723e2de82eca91c19f8939a332d8efae816600b0aef"

RCONFLICTS_${PN} = "linux-firmware-bcm43430"

S = "${WORKDIR}/Sterling-bcm43430"

FIRMWARE_PATH = "/lib/firmware/brcm"

do_install_append() {
	install -d ${D}${FIRMWARE_PATH}
	install -m 0644 ${S}/lib/firmware/brcm/4343w.hcd ${D}${FIRMWARE_PATH}/
	install -m 0644 ${S}/lib/firmware/brcm/brcmfmac43430-sdio.txt ${D}${FIRMWARE_PATH}/
	install -m 0644 ${S}/lib/firmware/brcm/brcmfmac43430-sdio.bin ${D}${FIRMWARE_PATH}/
	install -m 0644 ${S}/lib/firmware/brcm/brcmfmac43430-sdio.clm_blob ${D}${FIRMWARE_PATH}/
}

FILES_${PN} = "${FIRMWARE_PATH}/4343w.hcd \
	${FIRMWARE_PATH}/brcmfmac43430-sdio.txt \
	${FIRMWARE_PATH}/brcmfmac43430-sdio.bin \
	${FIRMWARE_PATH}/brcmfmac43430-sdio.clm_blob \
"
