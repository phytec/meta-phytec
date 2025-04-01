FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:phyboard-nash = " file://peb-wlbt-07.conf"

do_install:append:phyboard-nash () {
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${UNPACKDIR}/peb-wlbt-07.conf ${D}${sysconfdir}/modprobe.d
}

FILES:${PN} += "${sysconfdir}/modprobe.d"
