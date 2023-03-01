FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
EXTRA_OEMAKE_append_am62xx = " CFG_WITH_SOFTWARE_PRNG=y CFG_TEE_CORE_LOG_LEVEL=1"

SRC_URI_ti-soc = " \
    git://github.com/OP-TEE/optee_os.git;protocol=https \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
"
