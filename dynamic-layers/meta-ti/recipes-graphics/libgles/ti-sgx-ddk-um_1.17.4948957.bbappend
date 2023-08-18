FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://rc.pvr.service \
"

SYSTEMD_SERVICE:${PN} = "rc.pvr.service"

do_install_append () {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/rc.pvr.service ${D}${systemd_system_unitdir}
}

FILES_${PN} += "${systemd_system_unitdir}/rc.pvr.service"
