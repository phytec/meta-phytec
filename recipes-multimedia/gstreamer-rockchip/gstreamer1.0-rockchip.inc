# Copyright (C) 2016 - 2017 Randy Li <ayaka@soulik.info>
# Released under the GNU GENERAL PUBLIC LICENSE Version 2
# (see COPYING.GPLv2 for the terms)

require recipes-multimedia/gstreamer/gstreamer1.0-plugins.inc

DESCRIPTION = "GStreamer 1.0 plugins for Rockchip platforms"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d1e4aa87f6192354d3de840cf774d93"
DEPENDS += "gstreamer1.0-plugins-base rockchip-mpp"

SRC_URI_remove = " \
    file://0001-introspection.m4-prefix-pkgconfig-paths-with-PKG_CON.patch \
    file://gtk-doc-tweaks.patch \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
FILESPATH_prepend := "${THISDIR}/${PN}:"

inherit gettext autotools pkgconfig

PACKAGECONFIG ??= " \
    mpp             \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'drm', '', d)} \
"
PACKAGECONFIG[mpp]    = "--enable-rockchipmpp,--disable-rockchipmpp,rockchip-mpp"
PACKAGECONFIG[vpudec] = "--enable-vpudec,--disable-vpudec,rockchip-vpu"
PACKAGECONFIG[drm]    = "--enable-drmrockchip,--disable-drmrockchip,libdrm"

EXTRA_OECONF += "    \
    --disable-kms    \
"
EXTRA_OECONF_remove = "--disable-gtk-doc"

do_configure[prefuncs] = " delete_pkg_m4_file"

do_configure() {
    NOCONFIGURE=true ${S}/autogen.sh
    oe_runconf
}
