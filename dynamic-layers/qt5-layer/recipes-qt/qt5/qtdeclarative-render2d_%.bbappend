FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/:"

SRC_URI += "file://profile.sh"

do_install_append() {
    install -d ${D}${sysconfdir}/profile.d/
    install -m 0755 ${WORKDIR}/profile.sh ${D}${sysconfdir}/profile.d/${PN}.sh
}
