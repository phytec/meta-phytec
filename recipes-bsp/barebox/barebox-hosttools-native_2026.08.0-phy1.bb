require barebox_${PV}.bb

SUMMERY = "barebox host tools"
PROVIDES = "${PN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/barebox/:"

DEPENDS += "libusb1-native openssl-native zlib-native "

inherit deploy pkgconfig
inherit kconfig-set

do_configure:append() {
    kconfig_set ARCH_IMX_IMXIMAGE y
    kconfig_set ARCH_IMX_IMXIMAGE_SSL_SUPPORT y
    kconfig_set ARCH_IMX_USBLOADER y
}

do_compile() {
    # To build the USB loaders, pkg-config needs to know about libusb-1.0
    export PKG_CONFIG_LIBDIR="${STAGING_DIR_NATIVE}${libdir}/pkgconfig:${STAGING_DIR_NATIVE}/usr/share/pkgconfig"
    export PKG_CONFIG_SYSROOT_DIR=
    export PKG_CONFIG_PATH="${STAGING_DIR_NATIVE}"

    unset LDFLAGS
    unset CFLAGS
    unset CPPFLAGS
    unset CXXFLAGS
    unset MACHINE

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
