FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI_ti-soc = " \
    git://github.com/OP-TEE/optee_os.git;protocol=https \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
"
