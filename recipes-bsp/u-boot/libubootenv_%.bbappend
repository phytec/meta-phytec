FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}/:"

SRC_URI += "file://fw_env.config"

EMMC_DEV ??= "0"

do_configure_append () {
	sed -i -e 's/@MMCDEV@/${EMMC_DEV}/g' ${WORKDIR}/fw_env.config
}

do_install_append () {
	install -d ${D}${sysconfdir}
	install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}

FILES_${PN} += "${sysconfdir}/fw_env.config"
