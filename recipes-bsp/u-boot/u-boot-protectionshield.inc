PROTECTION_SHIELD_ROOT_PASSWORD ??= "root"
PROTECTION_SHIELD_BOOTLOADER_TIMEOUT ??= "3"

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

patch_enable_password() {
    PASSWORD_HASH=$( echo -n "${1}" | sha256sum | awk '{print $1}')
    kconfig_del AUTOBOOT
    kconfig_set BOOTDELAY ${PROTECTION_SHIELD_BOOTLOADER_TIMEOUT}
    kconfig_set AUTOBOOT_KEYED y
    kconfig_set AUTOBOOT_PROMPT '"Enter password within %d seconds to stop autoboot and access console\\n"'
    kconfig_set CMD_HASH y
    kconfig_set AUTOBOOT_ENCRYPTION y
    kconfig_set AUTOBOOT_STOP_STR_SHA256 "\"${PASSWORD_HASH}\""
}

set_autoboot_password_root() {
    patch_enable_password "${PROTECTION_SHIELD_ROOT_PASSWORD}"
}


set_autoboot_password_random() {
    patch_enable_password "$(head -c 64 /dev/urandom | tr -d '\0')"
}

do_patch_append_shieldlow () {
    bb.build.exec_func("set_autoboot_password_root", d)
}

do_patch_append_shieldmedium () {
    bb.build.exec_func("set_autoboot_password_root", d)
}

do_patch_append_shieldhigh() {
    bb.build.exec_func("set_autoboot_password_random", d)
}
