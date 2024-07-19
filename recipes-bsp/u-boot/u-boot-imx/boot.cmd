setenv bootcmd "mmc dev ${mmcdev}; if mmc rescan; then if run loadimage; then run mmcboot; fi; fi;"
setenv image "fitImage"
setenv loadimage "fatload mmc ${mmcdev}:${mmcpart} ${loadaddr} ${image}"
setenv mmcargs "setenv bootargs console=${console} root=/dev/mmcblk${mmcdev}p${mmcroot} rootwait rw"
setenv mmcautodetect "yes"
setenv mmcboot "echo Booting from mmc ...; run mmcargs; bootm ${loadaddr};"

run bootcmd
