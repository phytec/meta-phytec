FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}/:"

SRC_URI += "file://fw_env.config"

EMMC_DEV ??= "0"
ENV_OFFSET ??= "0x3c0000"
ENV_OFFSET:mx93-generic-bsp = "0x700000"
ENV_OFFSET:k3 = "0x680000"
ENV_OFFSET_REDUND ??= "0x3e0000"
ENV_OFFSET_REDUND:mx93-generic-bsp = "0x720000"
ENV_OFFSET_REDUND:k3 = "0x6c0000"
ENV_SIZE ??= "0x10000"
ENV_SIZE:k3 = "0x20000"
ENV_SIZE:j721s2 = "0x40000"

do_configure:append () {
	sed -i \
		-e 's/@MMCDEV@/${EMMC_DEV}/g' \
		-e 's/@ENV_OFFSET@/${ENV_OFFSET}/g' \
		-e 's/@ENV_OFFSET_REDUND@/${ENV_OFFSET_REDUND}/g' \
		-e 's/@ENV_SIZE@/${ENV_SIZE}/g' \
		${WORKDIR}/fw_env.config
}

do_install:append () {
	install -d ${D}${sysconfdir}
	install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}

FILES:${PN} += "${sysconfdir}/fw_env.config"
