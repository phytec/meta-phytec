FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://0001-tools-Makefile-improve-cross_tools-target-usability.patch \
    file://0002-tools-Makefile-add-override-statements-for-cross_too.patch \
"

EXTRA_OEMAKE = 'CROSS_COMPILE="${TARGET_PREFIX}" CC="${CC}" CFLAGS="${CFLAGS}" LDFLAGS="${LDFLAGS}" STRIP=true V=1'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC}" HOSTCFLAGS="${BUILD_CFLAGS}" HOSTLDFLAGS="${BUILD_LDFLAGS}"'
