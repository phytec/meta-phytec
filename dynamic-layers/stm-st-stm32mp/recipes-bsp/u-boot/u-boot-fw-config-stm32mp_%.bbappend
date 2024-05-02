# Create symbolic link between one of the "fw_env.config.*" and "fw_env.config" file
# depending on boot device defined in machine config ('BOOTDEVICE_LABELS')
# When several boot devices are defined, the given priority to the default config is:
# 1. nand
# 2. emmc
# 3. nor-emmc
# 4. sdcard

do_install[postfuncs] += "create_default_fw_env_config"

create_default_fw_env_config() {
	case "${BOOTDEVICE_LABELS}" in
		*' nand'*|'nand '*|'nand')
			fw_env_file="fw_env.config.nand";;
		*' emmc'*|'emmc '*|'emmc')
			fw_env_file="fw_env.config.mmc";;
		*' nor-emmc'*|'nor-emmc '*|'nor-emmc')
			fw_env_file="fw_env.config.nor";;
		*' sdcard'*|'sdcard '*|'sdcard')
			fw_env_file="fw_env.config.mmc";;
		*)
			bbwarn "Unknown boot device label. No default fw_env.config created.";;
	esac

	if [ -n ${fw_env_file} ]; then
		ln -s ${sysconfdir}/${fw_env_file} ${D}${sysconfdir}/fw_env.config
	fi
}


