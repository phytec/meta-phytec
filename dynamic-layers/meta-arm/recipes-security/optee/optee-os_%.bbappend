VENDOR_INC = ""
VENDOR_INC:imx-generic-bsp = "optee-phytec-imx.inc"
VENDOR_INC:ti-soc = "optee-phytec-ti.inc"
VENDOR_INC:stm32mp1common = "optee-phytec-stm.inc"
require ${VENDOR_INC}

inherit features_check

REQUIRED_MACHINE_FEATURES = "optee"

OPTEE_CORE_LOG_LEVEL ??= "1"
OPTEE_TA_LOG_LEVEL ??= "0"
OPTEE_IN_TREE_EARLY_TAS ?= "\
    pkcs11/fd02c9da-306c-48c7-a49c-bbd827ae86ee \
    trusted_keys/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c \
"

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

EXTRA_OEMAKE:append = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
'
