FILESEXTRAPATHS_prepend := "${THISDIR}:"

SRC_URI += "file://can-pre-up"

do_install_append() {
	install -m 0755 ${WORKDIR}/can-pre-up ${D}${sysconfdir}/network/can-pre-up
}
