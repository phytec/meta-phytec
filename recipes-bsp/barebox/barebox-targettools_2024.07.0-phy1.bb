require barebox_${PV}.bb
inherit kconfig-set

SUMMERY = "barebox userspace tools"
PROVIDES = "${PN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/barebox/:"

PR = "${INC_PR}.0"

export userccflags = "${TARGET_CC_ARCH} ${TOOLCHAIN_OPTIONS} ${CFLAGS} ${LDFLAGS}"

do_configure:append() {
    oe_runmake ARCH=sandbox targettools_defconfig
}

do_configure:append:ti33x() {
    kconfig_set ARCH_OMAP_MULTI y
    kconfig_set MACH_PHYTEC_SOM_AM335X y
}

do_compile () {
    oe_runmake scripts
}

do_install () {
    mkdir -p ${B}/

    bbnote "Installing barebox targettools on target rootfs"
    install -d ${D}${base_sbindir}
    install -m 744 ${B}/scripts/bareboxenv-target ${D}${base_sbindir}/bareboxenv
    install -m 744 ${B}/scripts/bareboxcrc32-target ${D}${base_sbindir}/bareboxcrc32
    install -m 744 ${B}/scripts/kernel-install-target ${D}${base_sbindir}/bareboxkernelinstall
    install -m 744 ${B}/scripts/bareboximd-target ${D}${base_sbindir}/bareboximd
}

PACKAGE_ARCH = "${TUNE_PKGARCH}"

FILES:${PN} = "${base_sbindir}"

do_deploy () {
}
