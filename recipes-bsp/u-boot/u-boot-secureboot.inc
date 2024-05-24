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
}

set_image_to_fitimage () {
    sed_safely "s/image=Image\\0/image=fitImage\\0/g" include/configs/phycore_*.h
}

set_loadaddress () {
    sed_safely "s/CONFIG_SYS_LOAD_ADDR=0x.*/CONFIG_SYS_LOAD_ADDR=${UBOOT_ENTRYPOINT}/g" configs/phycore-imx8m*
    sed_safely "s/CONFIG_SYS_LOAD_ADDR=0x.*/CONFIG_SYS_LOAD_ADDR=${UBOOT_ENTRYPOINT}/g" configs/imx8mm-phygate-tauri_defconfig
}

set_fitimage_key() {
    kconfig_set DEVICE_TREE_INCLUDES '"${FITIMAGE_PUBKEY_SIGNATURE_PATH}"'
}

python do_patch:append:secureboot () {
    bb.build.exec_func("set_dofitboot_1", d)
    bb.build.exec_func("set_image_to_fitimage", d)
    bb.build.exec_func("set_loadaddress", d)

    if oe.data.typed_value("FITIMAGE_SIGN", d) and d.getVar('FITIMAGE_SIGN_ENGINE') == 'software':
        bb.build.exec_func("set_fitimage_key", d)
}
