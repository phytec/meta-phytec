FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:phygate-tauri-l-imx8mm-2 = "\
    file://0003-plat-imx8mm-tauri-l-use-uart4-as-rs485.patch \
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
#    install -Dm 0644 ${S}/build/${PLATFORM}/debug/bl31.bin ${DEPLOYDIR}/${BOOT_TOOLS}/bl31-${PLATFORM}.bin
#    if ${BUILD_OPTEE}; then
#       install -m 0644 ${S}/build-optee/${PLATFORM}/debug/bl31.bin ${DEPLOYDIR}/${BOOT_TOOLS}/bl31-${PLATFORM}.bin-optee
#    fi
#}
