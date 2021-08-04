FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}/:"

SRC_URI += "file://fw_env.config"

do_install:append () {
	install -d ${D}${sysconfdir}
	install -m 644 ${S}/../fw_env.config ${D}${sysconfdir}/fw_env.config
}

FILES:${PN} += "${sysconfdir}/fw_env.config"
