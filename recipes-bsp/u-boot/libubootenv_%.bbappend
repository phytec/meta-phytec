FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/:"

SRC_URI += "file://fw_env.config"

do_install_append () {
	install -d ${D}${sysconfdir}
	install -m 644 ${S}/../fw_env.config ${D}${sysconfdir}/fw_env.config
}

FILES_${PN} += "${sysconfdir}/fw_env.config"
