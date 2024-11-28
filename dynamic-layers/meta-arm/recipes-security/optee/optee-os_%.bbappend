VENDOR_INC = ""
VENDOR_INC:imx-generic-bsp = "optee-phytec-imx.inc"
VENDOR_INC:ti-soc = "optee-phytec-ti.inc"
require ${VENDOR_INC}

inherit features_check

REQUIRED_MACHINE_FEATURES = "optee"

OPTEE_CORE_LOG_LEVEL ??= "1"
OPTEE_TA_LOG_LEVEL ??= "0"

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
