FILESEXTRAPATHS:prepend := "${THISDIR}/features:"

inherit u-boot-dts-pubkeynode

SRC_URI:append:secureboot = " \
    file://deselect-commands.cfg \
    file://default-env.cfg \
    ${@bb.utils.contains('UBOOT_SIGN_ENABLE','1','file://fitimage.cfg', '', d)} \
"

SRC_URI:append:mx8m-generic-bsp:secureboot = " \
    file://hab.cfg \
"
SRC_URI:append:mx8m-nxp-bsp:secureboot = " \
    ${@bb.utils.contains('FITIMAGE_SIGN_ENGINE','nxphab','file://disable-fit-signature.cfg', '', d)} \
"
SRC_URI:append:mx91-generic-bsp:secureboot = " \
    file://ahab.cfg \
"
SRC_URI:append:mx93-generic-bsp:secureboot = " \
    file://ahab.cfg \
"

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

set_fitimage_key() {
    kconfig_set DEVICE_TREE_INCLUDES '"signature_node.dtsi"'
    cp "${FITIMAGE_PUBKEY_SIGNATURE_PATH}" "${S}/arch/arm/dts/"
}

set_tikey_symlinks() {
    ln -sf ${BOOTLOADER_TI_K3_MPK_KEY} ${S}/arch/arm/mach-k3/keys/custMpk.pem
    ln -sf ${BOOTLOADER_TI_K3_DEGENERATE_KEY} ${S}/arch/arm/mach-k3/keys/ti-degenerate-key.pem
}

python do_patch:append:secureboot () {
    if d.getVar("UBOOT_SIGN_ENABLE") == "1" and d.getVar('FITIMAGE_SIGN_ENGINE') != 'nxphab':
        bb.build.exec_func("set_fitimage_key", d)
}

python do_unpack:append:secureenforced () {
    bb.build.exec_func('set_tikey_symlinks', d)
}

DEPENDS:append:secureenforced = " phytec-dev-ca-native"
do_patch[depends] += "${@bb.utils.contains("MACHINEOVERRIDES", "secureenforced", "phytec-dev-ca-native:do_install", "", d)}"

do_uboot_assemble_fitimage:prepend:secureboot () {
    if echo "${UBOOT_SIGN_KEYDIR}" | grep -q "pkcs11:"; then
        setup_pkcs11_env
    fi
}
