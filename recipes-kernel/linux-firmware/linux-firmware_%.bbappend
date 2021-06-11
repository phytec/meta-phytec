SRC_DIR = "${WORKDIR}/Sterling-bcm43430_v1.1"
FIRMWARE_PATH = "${nonarch_base_libdir}/firmware/brcm"

SRC_URI_append_mx6 = " \
	https://download.phytec.de/Software/Linux/Driver/Sterling-bcm43430_v1.1.zip;name=laird \
"

SRC_URI[laird.sha256sum] = "fe52f348c819f4cde62db3153b2c68ee5eaa828fe34a8857ecd0f6c2bb23bb93"

do_install_append_mx6() {
    install -m 0644 ${SRC_DIR}${FIRMWARE_PATH}/brcmfmac43430-sdio.txt ${D}${FIRMWARE_PATH}/brcmfmac43430-sdio.txt
	install -m 0644 ${SRC_DIR}${FIRMWARE_PATH}/4343w.hcd ${D}${FIRMWARE_PATH}/BCM43430A1.hcd
}

FILES_${PN}-bcm43430_append_mx6 = " \
  ${FIRMWARE_PATH}/BCM43430A1.hcd \
"
