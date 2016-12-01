# Copyright (C) 2016 Wadim Egorov <w.egorov@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Userspace interface to the kernel DRM services"
DESCRIPTION = "The runtime library for accessing the kernel DRM services.  DRM \
stands for \"Direct Rendering Manager\", which is the kernel portion of the \
\"Direct Rendering Infrastructure\" (DRI).  DRI is required for many hardware \
accelerated OpenGL drivers."
HOMEPAGE = "http://dri.freedesktop.org"
SECTION = "x11/base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://xf86drm.c;beginline=9;endline=32;md5=c8a3b961af7667c530816761e949dc71"
PROVIDES = "drm"
DEPENDS = "libpthread-stubs udev libpciaccess"

SRC_URI = "git://chromium.googlesource.com/chromiumos/third_party/libdrm;branch=chromeos-2.4.66;protocol=https"
SRCREV = "bf23724d876ad4495ec455ad9a713fa197df758a"
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "rk3288"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-cairo-tests \
                 --enable-omap-experimental-api \
                 --enable-rockchip-experimental-api \
                 --enable-install-test-programs \
                 --disable-manpages \
                 --disable-valgrind \
                "

ALLOW_EMPTY_${PN}-drivers = "1"
PACKAGES =+ "${PN}-tests ${PN}-drivers ${PN}-radeon ${PN}-nouveau ${PN}-omap \
             ${PN}-intel ${PN}-exynos ${PN}-kms ${PN}-freedreno ${PN}-amdgpu \
             ${PN}-rockchip"

RRECOMMENDS_${PN}-drivers = "${PN}-radeon ${PN}-nouveau ${PN}-omap ${PN}-intel \
                             ${PN}-exynos ${PN}-freedreno ${PN}-amdgpu \
                             ${PN}-rockchip"

FILES_${PN}-tests = "${bindir}/*"
FILES_${PN}-radeon = "${libdir}/libdrm_radeon.so.*"
FILES_${PN}-nouveau = "${libdir}/libdrm_nouveau.so.*"
FILES_${PN}-omap = "${libdir}/libdrm_omap.so.*"
FILES_${PN}-intel = "${libdir}/libdrm_intel.so.*"
FILES_${PN}-exynos = "${libdir}/libdrm_exynos.so.*"
FILES_${PN}-kms = "${libdir}/libkms*.so.*"
FILES_${PN}-freedreno = "${libdir}/libdrm_freedreno.so.*"
FILES_${PN}-amdgpu = "${libdir}/libdrm_amdgpu.so.*"
FILES_${PN}-rockchip = "${libdir}/libdrm_rockchip.so.*"
