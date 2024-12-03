# optee-os overrides for PHYTEC TI SoMs
OPTEE_IN_TREE_EARLY_TAS ?= "trusted_keys/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c"

EXTRA_OEMAKE:remove = "CFG_MAP_EXT_DT_SECURE=y"
EXTRA_OEMAKE:append = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
    OPENSSL_MODULES=${STAGING_LIBDIR_NATIVE}/ossl-modules \
    CFG_WITH_SOFTWARE_PRNG=n \
'
EXTRA_OEMAKE:append:j721s2 = "${@ ' CFG_CONSOLE_UART='+ d.getVar('OPTEE_K3_USART') if d.getVar('OPTEE_K3_USART') else ''}"

# This is needed for bl32.elf
INSANE_SKIP:${PN}:append = " textrel"
