# optee-os overrides for PHYTEC TI SoMs
EXTRA_OEMAKE:remove = "CFG_MAP_EXT_DT_SECURE=y"
EXTRA_OEMAKE:append = ' \
    OPENSSL_MODULES=${STAGING_LIBDIR_NATIVE}/ossl-modules \
    CFG_WITH_SOFTWARE_PRNG=n \
'
EXTRA_OEMAKE:append:j721s2 = "${@ ' CFG_CONSOLE_UART='+ d.getVar('OPTEE_K3_USART') if d.getVar('OPTEE_K3_USART') else ''}"
EXTRA_OEMAKE:append:am62lxx = ' \
    CFG_WITH_SOFTWARE_PRNG=y \
'

# This is needed for bl32.elf
INSANE_SKIP:${PN}:append = " textrel"
