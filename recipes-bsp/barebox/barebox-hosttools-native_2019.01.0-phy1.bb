require barebox_${PV}.bb

SUMMERY = "barebox host tools"
PROVIDES = "${PN}"
FILESEXTRAPATHS_prepend := "${THISDIR}/barebox/:"

DEPENDS += "libusb-native openssl-native zlib-native"

inherit deploy pkgconfig

do_patch_append() {
    bb.build.exec_func('do_fix_pkg_config', d)
}

do_fix_pkg_config() {
	find ${S}/scripts/ -name Makefile -print0 | xargs -0 sed -i 's/pkg-config/pkg-config-native/g'
}

do_configure_append() {
    kconfig_set ARCH_IMX_IMXIMAGE y
    kconfig_set ARCH_IMX_IMXIMAGE_SSL_SUPPORT y
    kconfig_set ARCH_IMX_USBLOADER y
}

do_compile() {
    oe_runmake scripts
}

deltask do_install

inherit nopackages

do_deploy[sstate-outputdirs] = "${DEPLOY_DIR_TOOLS}"
do_deploy () {
    install -d ${DEPLOY_DIR_TOOLS}
    install -m 755 ${B}/scripts/imx/imx-image ${DEPLOY_DIR_TOOLS}/imx-image-${PV}
    install -m 755 ${B}/scripts/imx/imx-usb-loader ${DEPLOY_DIR_TOOLS}/imx-usb-loader-${PV}
}
