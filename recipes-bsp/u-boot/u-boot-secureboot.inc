FILESEXTRAPATHS:prepend := "${THISDIR}/features:"

inherit sed-safely
inherit u-boot-dts-pubkeynode

SRC_URI:append:secureboot = " \
    file://deselect-commands.cfg \
    file://default-env.cfg \
    ${@bb.utils.contains('FITIMAGE_SIGN','true','file://fitimage.cfg', '', d)} \
"

SRC_URI:append:mx8m-generic-bsp:secureboot = " \
    file://hab.cfg \
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

set_dofitboot_1 () {
    bbnote "Setting \"dofitboot\" to \"1\""
    sed_safely "s/\(setenv dofitboot \)[01]/\11/" include/configs/phycore_*.h
    sed_safely "s/\(setenv dofitboot \)[01]/\11/" configs/phycore-imx8m*
    sed_safely "s/\(setenv dofitboot \)[01]/\11/" configs/imx8mm-phygate-tauri_defconfig
    sed_safely "s/\(dofitboot=\)[01]/\11/" include/env/phytec/fit.env
    sed_safely "s/\(dofitboot=\)[01]/\11/" include/env/phytec/fitboot.env
}

set_image_to_fitimage () {
    sed_safely "s/image=Image\\0/image=fitImage\\0/g" include/configs/phycore_*.h
    sed_safely "s/image=Image/image=fitImage/g" board/phytec/phycore_*/*.env
}

set_fitimage_key() {
    kconfig_set DEVICE_TREE_INCLUDES '"signature_node.dtsi"'
}

set_tikey() {
    kconfig_set PHYTEC_K3_KEY_BLOB_COPY n
    kconfig_set PHYTEC_K3_MPK_KEY '"${BOOTLOADER_TI_K3_MPK_KEY}"'
    kconfig_set PHYTEC_K3_DEGENERATE_KEY '"${BOOTLOADER_TI_K3_DEGENERATE_KEY}"'
}

set_tikey_symlinks() {
    ln -sf ${BOOTLOADER_TI_K3_MPK_KEY} ${S}/arch/arm/mach-k3/keys/custMpk.pem
    ln -sf ${BOOTLOADER_TI_K3_DEGENERATE_KEY} ${S}/arch/arm/mach-k3/keys/ti-degenerate-key.pem
}

set_dolegacyboot_1 () {
    sed_safely "s/\(dolegacyboot=\)[01]/\11/" board/phytec/phycore_imx8mp/phycore_imx8mp.env
    sed_safely "s/\(dolegacyboot=\)[01]/\11/" board/phytec/phycore_imx8mm/phycore_imx8mm.env
}

python do_patch:append:secureboot () {
    bb.build.exec_func("set_dofitboot_1", d)
    bb.build.exec_func("set_image_to_fitimage", d)

    if oe.data.typed_value("FITIMAGE_SIGN", d) and d.getVar('FITIMAGE_SIGN_ENGINE') == 'software':
        bb.build.exec_func("set_fitimage_key", d)
}

python do_patch:append:secureenforced () {
    bb.build.exec_func("set_tikey", d)
}

python do_unpack:append:secureenforced () {
    bb.build.exec_func('set_tikey_symlinks', d)
}

DEPENDS:append:secureenforced = " phytec-dev-ca-native"
do_patch[depends] += "${@bb.utils.contains("MACHINEOVERRIDES", "secureenforced", "phytec-dev-ca-native:do_install", "", d)}"

python do_patch:append:mx8m-nxp-bsp:secureboot () {
    bb.build.exec_func("set_dolegacyboot_1", d)
}

do_configure:append:secureboot () {
    cp "${FITIMAGE_PUBKEY_SIGNATURE_PATH}" "${S}/arch/arm/dts/"
}
