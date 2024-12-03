DEPENDS:append = " \
    ti-sci-fw \
    python3-pyelftools-native \
    python3-pyyaml-native \
    python3-jsonschema-native \
    python3-yamllint-native \
"

# TI's DM firmware is currently hosted in
# git://git.ti.com/processor-firmware/ti-linux-firmware.git
DM_FIRMWARE = "ipc_echo_testb_mcu1_0_release_strip.xer5f"

PLAT_SFX = ""
PLAT_SFX:am64xx = "am64xx"
PLAT_SFX:am62xx = "am62xx"
PLAT_SFX:am62axx = "am62axx"

PACKAGECONFIG[atf] = "BL31=${STAGING_DIR_HOST}/firmware/bl31.bin,,trusted-firmware-a"
PACKAGECONFIG[optee] = "TEE=${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/tee-pager_v2.bin,,optee-os"
PACKAGECONFIG[dm] = "DM=${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/ti-dm/${PLAT_SFX}/${DM_FIRMWARE},,ti-dm-fw"

PACKAGECONFIG:append:aarch64 = " atf optee"
PACKAGECONFIG:append:am62xx = " dm"
PACKAGECONFIG:append:am62axx = " dm"

BINMAN_INDIRS = "${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware"

EXTRA_OEMAKE += "${PACKAGECONFIG_CONFARGS} BINMAN_INDIRS=${BINMAN_INDIRS}"

do_deploy:append:k3r5 () {
    rm -f ${DEPLOYDIR}/u-boot-initial-env*
    rm -f ${DEPLOYDIR}/${PN}-initial-env*
    rm -f ${DEPLOYDIR}/u-boot-initial-env-${MACHINE}*
    rm -f ${DEPLOYDIR}/${PN}-initial-env-${MACHINE}*
    rm -f ${DEPLOYDIR}/u-boot-initial-env-${MACHINE}-${PV}-${PR}*
    rm -f ${DEPLOYDIR}/${PN}-initial-env-${MACHINE}-${PV}-${PR}*
}
