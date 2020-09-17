DESCRIPTION = "Propriety firmware binaries needed for the Laird Sterling-LWB Wifi/BT Module"
LICENSE = "LICENSE.laird"
LIC_FILES_CHKSUM = "file://LICENSE.laird;md5=ddab19c1a336cc25309c3de36ff9b4b0"

SRC_URI = "ftp://ftp.phytec.de/pub/Software/Linux/Driver/Sterling-bcm43430_v1.1.zip "

SRC_URI[md5sum] = "8213f4feee96560592746a8bea5f985c"
SRC_URI[sha256sum] = "fe52f348c819f4cde62db3153b2c68ee5eaa828fe34a8857ecd0f6c2bb23bb93"

RDEPENDS_${PN} = "brcm-patchram-plus"
RCONFLICTS_${PN} = "linux-firmware-bcm43430"
RREPLACES_${PN} = "linux-firmware-bcm43430"
RPROVIDES_${PN} = "linux-firmware-bcm43430"

S = "${WORKDIR}/Sterling-bcm43430_v1.1"

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

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phycore-imx8-2"
COMPATIBLE_MACHINE .= "|phyboard-polaris-imx8m-3"
COMPATIBLE_MACHINE .= ")$"
