COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "mx8-mainline-bsp"
COMPATIBLE_MACHINE .= "|mx93-mainline-bsp"
COMPATIBLE_MACHINE .= ")$"

#i.MX8M specific settings
EXTRA_OEMAKE:append:mx8-mainline-bsp = "\
        IMX_BOOT_UART_BASE=auto \
        BL32_BASE=${ATF_BL32_BASE} \
"

do_deploy:append() {
    ln -srn ${DEPLOYDIR}/${PN}/bl31${TFA_INSTALL_SUFFIX}.bin ${DEPLOYDIR}/bl31${TFA_INSTALL_SUFFIX}.bin
}

# When built with opteed, tf-a will jump to BL32_BASE load address.
# Thus, boards will not boot when OP-TEE had not been loaded to the BL32_BASE address.
EXTRA_OEMAKE:append = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee', 'SPD=opteed' , '' , d)} \
"
