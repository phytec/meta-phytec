COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "mx8-mainline-bsp"
COMPATIBLE_MACHINE .= "|mx93-mainline-bsp"
COMPATIBLE_MACHINE .= ")$"

#i.MX8M specific settings
EXTRA_OEMAKE:append:mx8-mainline-bsp = "\
        IMX_BOOT_UART_BASE=auto \
"
