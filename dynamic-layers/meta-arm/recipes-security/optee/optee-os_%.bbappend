FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit features_check
REQUIRED_MACHINE_FEATURES = "optee"

OPTEE_PKCS11_TA_HEAP_SIZE ??= "262144"
OPTEE_CORE_LOG_LEVEL ??= "1"
OPTEE_TA_LOG_LEVEL ??= "0"
OPTEE_IN_TREE_EARLY_TAS ??= "pkcs11/fd02c9da-306c-48c7-a49c-bbd827ae86ee trusted_keys/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c"
OPTEE_IN_TREE_EARLY_TAS:k3 ?= "trusted_keys/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c"

# General Settings
EXTRA_OEMAKE:append = " \
    CFG_WERROR=y \
"

# General Settings for Debug and Tests
EXTRA_OEMAKE:append = " \
    CFG_TEE_CORE_LOG_LEVEL=${OPTEE_CORE_LOG_LEVEL} \
    CFG_TEE_TA_LOG_LEVEL=${OPTEE_TA_LOG_LEVEL} \
    ${@bb.utils.contains('OPTEE_CORE_LOG_LEVEL' , '0', 'CFG_ENABLE_EMBEDDED_TESTS=n', 'CFG_ENABLE_EMBEDDED_TESTS=y', d)} \
"

# Compiled In TA
EXTRA_OEMAKE:append:mx8m-generic-bsp = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
    OPENSSL_MODULES=${STAGING_LIBDIR_NATIVE}/ossl-modules \
    CFG_PKCS11_TA_HEAP_SIZE=${OPTEE_PKCS11_TA_HEAP_SIZE} \
'

EXTRA_OEMAKE:append:mx6ul-generic-bsp = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
    OPENSSL_MODULES=${STAGING_LIBDIR_NATIVE}/ossl-modules \
'

EXTRA_OEMAKE:append:k3 = ' \
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
    CFG_TZDRAM_START=0x56000000 \
    CFG_CORE_LARGE_PHYS_ADDR=y CFG_CORE_ARM64_PA_BITS=36 \
    CFG_VIRTUALIZATION=n \
"
# HACK: disable HWRNG
EXTRA_OEMAKE:append:mx8m-generic-bsp = " \
    CFG_WITH_SOFTWARE_PRNG=y \
    CFG_HWRNG_PTA=n \
"

EXTRA_OEMAKE:append:mx8mm-nxp-bsp = " \
    CFG_UART_BASE=UART3_BASE \
    CFG_DDR_SIZE=0x100000000 \
"

EXTRA_OEMAKE:append:mx8mn-nxp-bsp = " \
    CFG_UART_BASE=UART3_BASE \
    CFG_DDR_SIZE=0x100000000 \
"

EXTRA_OEMAKE:append:mx8mp-nxp-bsp = " \
    CFG_UART_BASE=UART1_BASE \
"

EXTRA_OEMAKE:append:k3 = " \
    CFG_WITH_SOFTWARE_PRNG=n \
"

EXTRA_OEMAKE:append:mx6ul-generic-bsp = " \
    CFG_WITH_SOFTWARE_PRNG=n \
    ${@bb.utils.contains('MACHINE_FEATURES', 'caam', '', 'CFG_IMX_RNGB=y', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'emmc', '', 'CFG_RPMB_FS=n', d)} \
"
