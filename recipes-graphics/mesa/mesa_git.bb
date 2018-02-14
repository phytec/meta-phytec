require ${BPN}.inc

SRCREV_base = "a1d568c8302b0c775fda583beefbbe7725f13dec"

DEPENDS += "python-mako-native"

inherit pythonnative

SRC_URI = " \
      git://github.com/mesa3d/mesa.git;protocol=https;branch=master;name=base \
           file://replace_glibc_check_with_linux.patch \
           file://disable-asm-on-non-gcc.patch \
           file://0001-Use-wayland-scanner-in-the-path.patch \
           file://0002-hardware-gloat.patch \
           file://llvm-config-version.patch \
           file://0001-winsys-svga-drm-Include-sys-types.h.patch \
           file://0001-Makefile.vulkan.am-explictly-add-lib-expat-to-intel-.patch \
      "
S = "${WORKDIR}/git"

#because we cannot rely on the fact that all apps will use pkgconfig,
#make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
do_install_append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi
}
