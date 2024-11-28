# optee-os overrides for PHYTEC TI SoMs
OPTEE_IN_TREE_EARLY_TAS ?= "trusted_keys/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c"

EXTRA_OEMAKE:append = ' \
    CFG_IN_TREE_EARLY_TAS="${OPTEE_IN_TREE_EARLY_TAS}" \
    OPENSSL_MODULES=${STAGING_LIBDIR_NATIVE}/ossl-modules \
    CFG_WITH_SOFTWARE_PRNG=n \
'
