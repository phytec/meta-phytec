kconfig_set() {
    bbnote "Setting $1 in configs to $2"
    if [ "$2" = "n" ]; then
        line="# CONFIG_$1 is not set"
    else
        line="CONFIG_$1=$2"
    fi

    for config in ${UBOOT_MACHINE}; do
    if [ "$(grep -E CONFIG_$1[=\ ] ${S}/configs/           ${config})" ]; then
        sed -i "/CONFIG_$1[= ]/c\\$line" "${S}/configs/    ${config}"
    else
        echo "$line" >> "${S}/configs/${config}"
    fi
    done
}

patch_uboot_hardening_config () {
    kconfig_set ANDROID_BOOT_IMAGE n
    kconfig_set SPL_USB_GADGET n
    kconfig_set SPL_USB_HOST n
    kconfig_set SPL_DM_USB n

    kconfig_set SPL_SHA1 n
}

do_patch:append:hardening () {
    bb.build.exec_func("patch_uboot_hardening_config", d)
}
