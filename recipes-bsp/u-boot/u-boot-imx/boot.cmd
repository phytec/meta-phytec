setenv image "fitImage"
setenv loadimage "fatload mmc ${devnum}:${distro_bootpart} ${loadaddr} ${image}"
setenv mmcargs "setenv bootargs console=${console} root=/dev/mmcblk${devnum}p${mmcroot} rootwait rw"
setenv netargs "setenv bootargs console=${console} root=/dev/nfs ip='${nfsip}' nfsroot=${serverip}:'${nfsroot}',v3,tcp"
setenv mmcautodetect "yes"
setenv fitboot "\
if test ${no_extensions} = 0; then \
    if env exists overlays; then \
        bootm ${loadaddr}#${fit_fdtconf}${fit_extensions}#'${overlays}'; \
    else \
        bootm ${loadaddr}#${fit_fdtconf}${fit_extensions}; \
    fi; \
else \
    if env exists overlays; then \
        bootm ${loadaddr}#${fit_fdtconf}#'${overlays}'; \
    else \
        bootm ${loadaddr}#${fit_fdtconf}; \
    fi; \
fi;"

if test ${devtype} = mmc; then
	mmc dev ${devnum};
	if run loadimage; then
		echo Booting from mmc ...;
		if test ${no_bootenv} = 0; then
			if run mmc_load_bootenv; then
				env import -t ${bootenv_addr_r} ${filesize};
			fi;
		fi;
		run mmcargs;
		run fitboot;
	fi;
else
	if test ${devtype} = ethernet; then
		if test ${ip_dyn} = yes; then
			setenv nfsip dhcp;
			setenv get_cmd dhcp;
		else
			setenv nfsip ${ipaddr}:${serverip}::${netmask}::eth0:on;
			setenv get_cmd tftp;
		fi;
		if ${get_cmd} ${loadaddr} ${image}; then
			echo Booting from net ...;
			if test ${no_bootenv} = 0; then
				if run net_load_bootenv; then
					env import -t ${bootenv_addr_r} ${filesize};
				fi;
			fi;
			run netargs;
			run fitboot;
		fi;
	else
		echo Ampliphy only supports mmc and ethernet boot, not ${devtype} boot;
	fi;
fi;
