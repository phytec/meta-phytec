FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:phygate-tauri-l-imx8mm-2 = "\
    file://0001-plat-imx8mm-disable-uart4-assigned-to-arm-cortex-m4.patch \
"
EXTRA_OEMAKE += " \
        IMX_BOOT_UART_BASE="${IMX_UART_BASE}" \
"

EXTRA_OEMAKE:append:mx8mm-generic-bsp = ' BL32_BASE="0x56000000" '
# NOTE: Uncomment for debug build:
#EXTRA_OEMAKE += " \
#	DEBUG=1 \
#	V=1 \
#	CRASH_REPORTING=1 \
#"
#do_deploy() {
#    install -Dm 0644 ${S}/build/${ATF_PLATFORM}/debug/bl31.bin ${DEPLOYDIR}/${BOOT_TOOLS}/bl31-${ATF_PLATFORM}.bin
#    if ${BUILD_OPTEE}; then
#       install -m 0644 ${S}/build-optee/${ATF_PLATFORM}/debug/bl31.bin ${DEPLOYDIR}/${BOOT_TOOLS}/bl31-${ATF_PLATFORM}.bin-optee
#    fi
#}
