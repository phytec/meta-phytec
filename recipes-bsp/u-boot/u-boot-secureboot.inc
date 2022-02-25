kconfig_del() {
    bbnote "Removing all lines $1 prefixes in configs"
    for config in ${UBOOT_MACHINE}; do
        sed -e "/$1/d" -i "${S}/configs/${config}"
    done
}

kconfig_set() {
    bbnote "Setting $1 in configs to $2"
    if [ "$2" = "n" ]; then
        line="# CONFIG_$1 is not set"
    else
        line="CONFIG_$1=$2"
    fi

    for config in ${UBOOT_MACHINE}; do
    if [ "$(grep -E CONFIG_$1[=\ ] ${S}/configs/${config})" ]; then
        sed -i "/CONFIG_$1[= ]/c\\$line" "${S}/configs/${config}"
    else
        echo "$line" >> "${S}/configs/${config}"
    fi
    done
}

patch_ubootconfig () {
    kconfig_set IMX_HAB y #enable HAB
    kconfig_set ENV_IS_NOWHERE y #enable built-in env
    #don't allow reading/writing arbitrary vars from/to env storage.
    #only allow appending variables in general.
    kconfig_set ENV_APPEND y
    #only allow variables with the 'w' access flag to be set.
    kconfig_set ENV_WRITEABLE_LIST y
    kconfig_set ENV_ACCESS_IGNORE_FORCE y
    kconfig_set CMD_ENV_CALLBACK y
    kconfig_set CMD_ENV_FLAGS y
    kconfig_set CMD_GO n
    kconfig_set CMD_MEMORY n
    kconfig_set CMD_PROVISION_KEY n

    if [ "${FITIMAGE_SIGN}" = "true" ]; then
        kconfig_set FIT y
        kconfig_set FIT_ENABLE_SHA256_SUPPORT y
        kconfig_set FIT_BEST_MATCH y
        kconfig_set FIT_VERBOSE n
        kconfig_set LEGACY_IMAGE_FORMAT n

        sed -i "s/\(setenv dofitboot \)[01]/\11/" ${S}/include/configs/phycore_*.h
        sed -e 's/image=Image\\0/image=fitImage\\0/g' -i ${S}/include/configs/phycore_*.h
    else
        sed -e 's/image=Image\\0/image=Image-signed\\0/g' -i ${S}/include/configs/phycore_*.h
    fi
    if [ "${FITIMAGE_NO_DTB_OVERLAYS}" == "true" ]; then
        sed 's/mmc_load_overlay=/no_extensions=1\\0no_overlays=1\\0mmc_load_overlay=/g' -i ${S}/include/configs/phycore_*.h
    else
        sed 's/mmc_load_overlay=/no_extensions=1\\0no_overlays=0\\0mmc_load_overlay=/   g' -i ${S}/include/configs/phycore_*.h
    fi
}

do_patch_append_secureboot () {
    bb.build.exec_func("patch_ubootconfig", d)
}

