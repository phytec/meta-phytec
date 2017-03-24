require barebox_${PV}.bb

SUMMERY = "barebox userspace tools"
PROVIDES = "${PN}"
FILESEXTRAPATHS_prepend := "${THISDIR}/barebox/:"

PR = "${INC_PR}.0"

export TARGETCFLAGS="${TARGET_CC_ARCH} ${TOOLCHAIN_OPTIONS} ${CFLAGS} ${LDFLAGS}"
INTREE_DEFCONFIG = "${INTREE_DEFCONFIG_pn-barebox}"

do_configure_append() {
    # Compile target tools for barebox
    kconfig_set BAREBOXENV_TARGET y
    kconfig_set BAREBOXCRC32_TARGET y
    kconfig_set KERNEL_INSTALL_TARGET y
    kconfig_set IMD y
    kconfig_set IMD_TARGET y
}

do_install () {
    # remove all stuff from the barebox build
    rm -rf ${D}/
    mkdir -p ${B}/

    install -d ${D}${base_sbindir}
    install -m 744 ${B}/scripts/bareboxenv-target ${D}${base_sbindir}/bareboxenv
    install -m 744 ${B}/scripts/bareboxcrc32-target ${D}${base_sbindir}/bareboxcrc32
    install -m 744 ${B}/scripts/kernel-install-target ${D}${base_sbindir}/bareboxkernelinstall
    install -m 744 ${B}/scripts/bareboximd-target ${D}${base_sbindir}/bareboximd
}

PACKAGE_ARCH = "${TUNE_PKGARCH}"

FILES_${PN} = "${base_sbindir}"

do_deploy () {
}
