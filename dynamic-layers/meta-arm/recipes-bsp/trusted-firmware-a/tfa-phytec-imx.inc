COMPATIBLE_MACHINE = "mx8-mainline-bsp"

#i.MX8M specific settings
EXTRA_OEMAKE:append:mx8-mainline-bsp = "\
        IMX_BOOT_UART_BASE=auto \
"
