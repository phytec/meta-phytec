FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append = "\
    file://0001-plat-imx8mm-use-uart3-as-console.patch \
    file://0002-plat-imx8mn-use-uart3-as-console.patch \
"
SRC_URI_append_phygate-tauri-l-imx8mm-2 = "\
    file://0003-plat-imx8mm-tauri-l-use-uart4-as-rs485.patch \
"
SRC_URI_append_phyboard-pollux-imx8mp-2 = " \
    file://0004-plat-imx8mp-Update-debug-UART.patch \
"
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
