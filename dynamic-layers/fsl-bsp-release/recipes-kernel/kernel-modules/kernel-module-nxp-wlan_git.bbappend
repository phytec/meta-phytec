FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:phyboard-nash = " file://peb-wlbt-07.conf"
SRC_URI:append:mx95-nxp-bsp = " file://nxpiw612-sdio.conf"

do_install:append:phyboard-nash () {
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${UNPACKDIR}/peb-wlbt-07.conf ${D}${sysconfdir}/modprobe.d
}

do_install:append:mx95-nxp-bsp () {
    if echo "${MACHINE_FEATURES}" | grep -q "nxpiw612-sdio"; then
        install -d ${D}${sysconfdir}/modprobe.d
        install -m 0644 ${UNPACKDIR}/nxpiw612-sdio.conf ${D}${sysconfdir}/modprobe.d
    fi
}

FILES:${PN} += "${sysconfdir}/modprobe.d"
