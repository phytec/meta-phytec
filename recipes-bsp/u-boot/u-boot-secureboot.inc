UBOOT_ENABLE_ENV_SUPPORT ??= "true"

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
    if [ "${UBOOT_ENABLE_ENV_SUPPORT}" = "false" ] ; then
        kconfig_del ENV_IS_ #do not allow any environment storage that is preset
        kconfig_set ENV_IS_NOWHERE y #be explicit, we only want the built-in env storage. all others are unsigned and cannot be trusted.
    fi
    #As this is a secureboot build, change the kernel name from "Image" to "Image-signed" to avoid confusion.
    sed -e 's/image=Image\\0/image=Image-signed\\0/g' -i ${S}/include/configs/phycore_*.h
}

do_patch_append_secureboot () {
    bb.build.exec_func("patch_ubootconfig", d)
}

