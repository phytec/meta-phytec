COMPATIBLE_MACHINE = "mx8-mainline-bsp"

#i.MX8M specific settings
EXTRA_OEMAKE:append:mx8-mainline-bsp = "\
        IMX_BOOT_UART_BASE=${IMX_UART_BASE} \
"
TFA_BUILD_TARGET:mx8-mainline-bsp = "bl31"
TFA_PLATFORM:mx8-mainline-bsp = "${ATF_PLATFORM}"
