FILESEXTRAPATHS:prepend := "${THISDIR}/features:"

DEPENDS:append = " \
    ti-sci-fw \
    python3-pyelftools-native \
    python3-pyyaml-native \
    python3-jsonschema-native \
    python3-yamllint-native \
"
DEPENDS:append:k3 = " u-boot-mkenvimage-native"

# TI's DM firmware is currently hosted in
# git://git.ti.com/processor-firmware/ti-linux-firmware.git
DM_FIRMWARE = "ipc_echo_testb_mcu1_0_release_strip.xer5f"
DM_FIRMWARE:am62axx = "dm_edgeai_mcu1_0_release_strip.out"

DM_FIRMWARE = "ipc_echo_testb_mcu1_0_release_strip.xer5f"
PLAT_SFX = ""
PLAT_SFX:am64xx = "am64xx"
PLAT_SFX:am62xx = "am62xx"
PLAT_SFX:am62axx = "am62axx"
PLAT_SFX:am62pxx = "am62pxx"
PLAT_SFX:j722s = "j722s"
PLAT_SFX:am62lxx = "am62lxx"
PLAT_SFX:j721s2 = "j721s2"

PACKAGECONFIG[atf] = "BL31=${STAGING_DIR_HOST}/firmware/trusted-firmware-a/bl31.bin,,trusted-firmware-a"
PACKAGECONFIG[ap-trusted-rom] = "BL1=${STAGING_DIR_HOST}/firmware/trusted-firmware-a/bl1.bin,,trusted-firmware-a"
PACKAGECONFIG[optee] = "TEE=${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/tee-pager_v2.bin,,optee-os"
PACKAGECONFIG[dm] = "DM=${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/ti-dm/${PLAT_SFX}/${DM_FIRMWARE},,ti-dm-fw"

PACKAGECONFIG:append:aarch64 = " atf optee"
PACKAGECONFIG:append:am62xx = " dm"
PACKAGECONFIG:append:am62axx = " dm"
PACKAGECONFIG:append:am62pxx = " dm"
PACKAGECONFIG:append:j722s = " dm"
PACKAGECONFIG:append:am62lxx = " ap-trusted-rom"
PACKAGECONFIG:append:j721s2 = " dm"

BINMAN_INDIRS = "${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware"

EXTRA_OEMAKE += "${PACKAGECONFIG_CONFARGS} BINMAN_INDIRS=${BINMAN_INDIRS}"

SRC_URI:append:k3r5 = " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ecc', 'file://k3-inline-ecc.cfg', '', d)} \
"

do_deploy:append:k3 () {
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        for f in ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-*; do
            if [ -f "$f" ] && [ ! -L "$f" ]; then
                mkenvimage -r -s ${UBOOT_ENV_SIZE} -o "$f".bin "$f"
                ln -sf $(basename "$f".bin) ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}.bin
            fi
        done
    fi
}

do_deploy:append:k3r5 () {
    rm -f ${DEPLOYDIR}/u-boot-initial-env*
    rm -f ${DEPLOYDIR}/${PN}-initial-env*
    rm -f ${DEPLOYDIR}/u-boot-initial-env-${MACHINE}*
    rm -f ${DEPLOYDIR}/${PN}-initial-env-${MACHINE}*
    rm -f ${DEPLOYDIR}/u-boot-initial-env-${MACHINE}-${PV}-${PR}*
    rm -f ${DEPLOYDIR}/${PN}-initial-env-${MACHINE}-${PV}-${PR}*
}

do_deploy:append:am62lxx () {
        for f in ${B}/tiboot3*.bin; do
                if [ -f "$f" ]; then
                        install -m 0644 $f ${DEPLOYDIR}/
                        ln -sf $(basename $f) ${DEPLOYDIR}/tiboot3-${MACHINE}.bin
                fi
        done
}

do_configure:prepend:secureenforced () {
    ln -sf ${BOOTLOADER_TI_K3_MPK_KEY} ${S}/arch/arm/mach-k3/keys/custMpk.pem
    ln -sf ${BOOTLOADER_TI_K3_DEGENERATE_KEY} ${S}/arch/arm/mach-k3/keys/ti-degenerate-key.pem
}

DEPENDS:append:secureenforced = " phytec-dev-ca-native"
