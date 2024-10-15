SUMMARY = "The barebox bootloader"
DESCRIPTION = "The barebox is a bootloader designed for embedded systems. It \
               runs on a variety of architectures including x86, ARM, MIPS, \
               PowerPC and others."
HOMEPAGE = "http://www.barebox.org"
SECTION = "bootloaders"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM ?= "file://COPYING;md5=f5125d13e000b9ca1f0d3364286c4192"
DEPENDS += "lzop-native coreutils-native bison-native"
PROVIDES = "virtual/bootloader"
# Increase INC_PR everytime the include file is changed.
INC_PR = "r7"

CVE_PRODUCT = "pengutronix:barebox"
CVE_VERSION = "${@d.getVar("PV").split('-')[0]}"

FILESEXTRAPATHS:prepend := "${THISDIR}/barebox:"
SRC_URI = "http://barebox.org/download/barebox-${PV}.tar.bz2"
PATCHTOOL = "git"

SRC_URI:append = " \
    ${@oe.utils.conditional('DEBUG_BUILD','1','file://debugging.cfg','',d)} \
"

inherit kernel-arch deploy
inherit kconfig kconfig-set

# Barebox doesn't seperate arm 32bit and 64bit. So we have to set
# ARCH to arm for 64bit.
ARCH:aarch64 = "arm"

# verbose build 0/1
VERBOSE_BUILD ?= "0"
EXTRA_OEMAKE = 'CROSS_COMPILE="${TARGET_PREFIX}" CC="${TARGET_PREFIX}gcc" V="${VERBOSE_BUILD}"'
B = "${WORKDIR}/build"

# name of the image created by barebox Makefile
BAREBOX_BASE_BIN ?= "barebox"
BAREBOX_BIN ?= "images/${BAREBOX_BASE_BIN}.img"
# filename of file to be deployed by this recipe
BAREBOX_IMAGE_NAME ?= "barebox-${MACHINE}-${PKGV}-${PKGR}${IMAGE_VERSION_SUFFIX}"
BAREBOX_IMAGE_NAME[vardepsexclude] += "IMAGE_VERSION_SUFFIX"
BAREBOX_BIN_LINK_NAME ?= "barebox.bin"

do_configure:append() {
    # Don't compile target tools for barebox here
    kconfig_set BAREBOXENV_TARGET n
    kconfig_set BAREBOXCRC32_TARGET n
    kconfig_set KERNEL_INSTALL_TARGET n
    kconfig_set IMD_TARGET n
}

do_compile () {
    if [ -n "${CST_KEY_SOURCE}" -a "${CST_KEY_SOURCE}" = "token" ] ; then
        setup_pkcs11_env
    fi

    unset CFLAGS
    export LDFLAGS="--no-dynamic-linker"
    oe_runmake all
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_deploy () {
    bbnote "Deploying barebox"
    install -m 644 ${B}/${BAREBOX_BIN} ${DEPLOYDIR}/${BAREBOX_IMAGE_NAME}.bin
    ln -sf ${BAREBOX_IMAGE_NAME}.bin ${DEPLOYDIR}/${BAREBOX_BIN_LINK_NAME}

    bbnote "Installing host barebox toolset"
    install -d ${STAGING_BINDIR_NATIVE}
    install -m 755 ${B}/scripts/bareboxenv ${STAGING_BINDIR_NATIVE}/

    install -m 644 ${B}/.config ${DEPLOYDIR}/${BAREBOX_IMAGE_NAME}.config
    ln -sf ${BAREBOX_IMAGE_NAME}.config ${DEPLOYDIR}/${PN}.config
}
do_deploy[prefuncs] += "package_get_auto_pr"
addtask deploy before do_build after do_compile
