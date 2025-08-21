# optee-os overrides for PHYTEC NXP SoMs

OPTEE_PKCS11_TA_HEAP_SIZE ??= "32768"
OPTEE_IN_TREE_EARLY_TAS ??= "pkcs11/fd02c9da-306c-48c7-a49c-bbd827ae86ee trusted_keys/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c"

# Compiled In TA
EXTRA_OEMAKE:append:mx91-generic-bsp = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
'

EXTRA_OEMAKE:append:mx93-generic-bsp = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
'

EXTRA_OEMAKE:append:mx8m-generic-bsp = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
    OPENSSL_MODULES=${STAGING_LIBDIR_NATIVE}/ossl-modules \
    CFG_PKCS11_TA_HEAP_SIZE=${OPTEE_PKCS11_TA_HEAP_SIZE} \
'

EXTRA_OEMAKE:append:mx6ul-generic-bsp = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
    OPENSSL_MODULES=${STAGING_LIBDIR_NATIVE}/ossl-modules \
'

# SoC Settings
EXTRA_OEMAKE:append:mx8m-generic-bsp = " \
    CFG_NXP_CAAM=y \
    CFG_CRYPTO_DRIVER=y \
    CFG_WITH_SOFTWARE_PRNG=n \
    CFG_NXP_CAAM_RNG_DRV=y CFG_HWRNG_PTA=y CFG_HWRNG_QUALITY=1024\
    CFG_CORE_HUK_SUBKEY_COMPAT=y CFG_CORE_HUK_SUBKEY_COMPAT_USE_OTP_DIE_ID=y \
    CFG_VIRTUALIZATION=n \
"

EXTRA_OEMAKE:append:mx8mn-nxp-bsp = " \
    CFG_UART_BASE=UART3_BASE \
    CFG_TZDRAM_START=0x56000000 \
    CFG_DDR_SIZE=0x40000000 \
"

EXTRA_OEMAKE:append:mx6ul-generic-bsp = " \
    CFG_WITH_SOFTWARE_PRNG=n \
    ${@bb.utils.contains('MACHINE_FEATURES', 'caam', '', 'CFG_IMX_RNGB=y', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'emmc', '', 'CFG_RPMB_FS=n', d)} \
"
