# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>
#
# Based on the recipe in the layer meta-fsl-arm.
#   Copyright (C) 2012-2013 Freescale Semiconductor
#   Copyright (C) 2012-2014 O.S. Systems Software LTDA.
#   Released under the MIT license (see COPYING.MIT for the terms)

inherit fsl-bin-unpack

SUMMARY = "Userspace GPU driver for i.MX6"
DESCRIPTION = "Userspace GPU driver and apps for i.MX6"
SECTION = "libs"

# Year and version are from file COPYING in binary archive
LICENSE_FLAGS = "license-nxp_v14-june-2016"
LICENSE = "Proprietary & LICENCE.nxp-v14-june-2016"
LIC_FILES_CHKSUM = " \
    file://gpu-core/usr/include/gc_vdk.h;beginline=5;endline=11;md5=12c028cbbbedb4b8770267131500592c \
    file://COPYING;md5=d4f548f93b5fe0ee2bc86758c344412d \
"

# The recipe provides multiple packages for the Freescale demo programs,
# libgles1, libgles2 and egl support. The libgles1, libgles2 and libegl packages
# are mostly pulled in by dependences of other packages, like qtbase. The
# Freescale demo programs are not installed by default into the rootfs, because
# the package is roughly 600MB. To install it, put
#     IMAGE_INSTALL_append = " imx-gpu-viv-demos"
# into your local.conf.

# Nice tool from binary 'gmem_info'. Shows memory information of gpu:
#
#     $ gmem_info
#      Pid          Total      Reserved    Contiguous       Virtual      Nonpaged    Name
#       225    28,304,252    26,207,100             0     2,097,152             0    ./QtDemo
#      ------------------------------------------------------------------------------
#         1    28,304,252    26,207,100             0     2,097,152             0    Summary
#         -             -   108,010,628             -             -             -    Available
#     GPU Idle time:  0.000000 ms
#
# Install with
#     IMAGE_INSTALL_append = " imx-gpu-viv-tools"


# Require the same distro feature as the mesa recipe
REQUIRED_DISTRO_FEATURES = "opengl"

# We don't provide virtual/libgl yet. Library libGL.so depends on X symbols.
# NOTE: The virtual provider "virtual/opencl" is non-standard.
PROVIDES += "virtual/libgles2 virtual/libgles1 virtual/egl virtual/libg2d virtual/opencl"

PR = "r0"

SRC_URI = "${FSL_MIRROR}/${PN}-${PV}.bin;fsl-bin=true"
SRC_URI[md5sum] = "92fc34fc37b0865f61be1bd931f5166f"
SRC_URI[sha256sum] = "caaabd59a259e29aa5b7f9d1d7f3fe71cff9336ba44904485d258baef276351f"

S = "${WORKDIR}/${PN}-${PV}"

PACKAGES = " \
    imx-gpu-viv-demos-dbg imx-gpu-viv-demos \
    libegl-mx6 libegl-mx6-dev libegl-mx6-dbg \
    libgles2-mx6 libgles2-mx6-dev libgles2-mx6-dbg \
    libgles1-mx6 libgles1-mx6-dev libgles1-mx6-dbg \
    libgal-mx6 libgal-mx6-dev libgal-mx6-dbg \
    libvivante-mx6 libvivante-mx6-dev libvivante-mx6-dbg \
    libopenvg-mx6 libopenvg-mx6-dev libopenvg-mx6-dbg \
    libglslc-mx6 libglslc-mx6-dev libglslc-mx6-dbg \
    libvsc-mx6 libvsc-mx6-dev libvsc-mx6-dbg \
    libvdk-mx6 libvdk-mx6-dev libvdk-mx6-dbg \
    libclc-mx6 libclc-mx6-dev libclc-mx6-dbg \
    libopencl-mx6 libopencl-mx6-dev libopencl-mx6-dbg \
    imx-gpu-viv-tools imx-gpu-viv-tools-dbg \
    imx-gpu-viv-g2d imx-gpu-viv-g2d-dev imx-gpu-viv-g2d-dbg \
"

# Skip package if it does not match the machine float-point type in use
# Binaries were compiled with (extracted from debugging symbols):
#     GNU C 4.9.1 -march=armv7-a -mthumb-interwork -mfloat-abi=hard -mfpu=neon
#          -mtune=cortex-a9 -mtls-dialect=gnu -g -g -O2 -feliminate-unused-debug-types
inherit tune_features_check
REQUIRED_TUNE_FEATURES = "armv7a cortexa9 neon callconvention-hard"


# FIXME: The provided binary doesn't provide soname. If in future BSP
# release the libraries are fixed, we can drop this hack.

# For the packages that make up the OpenGL interfaces, inject variables so that
# they don't get Debian-renamed (which would remove the -mx6 suffix).
#
# FIXME: All binaries lack GNU_HASH in elf binary but as we don't have
# the source we cannot fix it. Disable the insane check for now.
python __anonymous() {
    packages = d.getVar('PACKAGES', True).split()
    for p in packages:
        d.appendVar("INSANE_SKIP_%s" % p, " ldflags")

    for p in (("libegl", "libegl1"), ("libgl", "libgl1"),
              ("libgles1", "libglesv1-cm1"), ("libgles2", "libglesv2-2"),
              ("libgles3",), ("libopencl",)):
        fullp = p[0] + "-mx6"
        pkgs = " ".join(p)
        d.setVar("DEBIAN_NOAUTONAME_" + fullp, "1")
        d.appendVar("RREPLACES_" + fullp, pkgs)
        d.appendVar("RPROVIDES_" + fullp, pkgs)
        d.appendVar("RCONFLICTS_" + fullp, pkgs)

        # For -dev, the first element is both the Debian and original name
        fullp += "-dev"
        pkgs = p[0] + "-dev"
        d.setVar("DEBIAN_NOAUTONAME_" + fullp, "1")
        d.appendVar("RREPLACES_" + fullp, pkgs)
        d.appendVar("RPROVIDES_" + fullp, pkgs)
        d.appendVar("RCONFLICTS_" + fullp, pkgs)
}

# We only support the fb-backend yet, no wayland, no x11, no directfb. So we
# copy only the -fb libraries
do_install () {
    # Directories
    install -d ${D}${libdir}
    install -d ${D}${libdir}/pkgconfig
    install -d ${D}${includedir}

    # Package libgal-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libGAL-fb.so  ${D}${libdir}/libGAL.so

    install -d ${D}${includedir}/HAL
    for name in ${S}/gpu-core/usr/include/HAL/*; do
        install -m 0644 "$name"  ${D}${includedir}/HAL/
    done


    # Package libvivante-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libVIVANTE-fb.so ${D}${libdir}/libVIVANTE.so


    # Package libegl-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libEGL-fb.so ${D}${libdir}/libEGL.so.1.0
    ln -sf libEGL.so.1.0 ${D}${libdir}/libEGL.so.1
    ln -sf libEGL.so.1.0 ${D}${libdir}/libEGL.so
    install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/egl_linuxfb.pc ${D}${libdir}/pkgconfig/egl.pc

    install -d ${D}${includedir}/EGL
    for name in ${S}/gpu-core/usr/include/EGL/*; do
        install -m 0644 "$name" ${D}${includedir}/EGL/
    done
    install -d ${D}${includedir}/KHR/
    for name in ${S}/gpu-core/usr/include/KHR/*; do
        install -m 0644 "$name" ${D}${includedir}/KHR/
    done


    # Package libgles2-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libGLESv2-fb.so ${D}${libdir}/libGLESv2.so.2.0.0
    ln -sf libGLESv2.so.2.0.0 ${D}${libdir}/libGLESv2.so.2
    ln -sf libGLESv2.so.2.0.0 ${D}${libdir}/libGLESv2.so
    install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc

    install -d ${D}${includedir}/GLES2
    for name in ${S}/gpu-core/usr/include/GLES2/*; do
        install -m 0644 "$name" ${D}${includedir}/GLES2/
    done


    # Package libgles1-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/glesv1_cm.pc ${D}${libdir}/pkgconfig/glesv1_cm.pc
    install -m 0644 ${S}/gpu-core/usr/lib/libGLESv1_CL.so.1.1.0 ${D}${libdir}/libGLESv1_CL.so.1.1.0
    ln -sf libGLESv1_CL.so.1.1.0 ${D}${libdir}/libGLESv1_CL.so.1
    ln -sf libGLESv1_CL.so.1.1.0 ${D}${libdir}/libGLESv1_CL.so
    install -m 0644 ${S}/gpu-core/usr/lib/libGLES_CL.so.1.1.0 ${D}${libdir}/libGLES_CL.so.1.1.0
    ln -sf libGLES_CL.so.1.1.0 ${D}${libdir}/libGLES_CL.so.1
    ln -sf libGLES_CL.so.1.1.0 ${D}${libdir}/libGLES_CL.so
    install -m 0644 ${S}/gpu-core/usr/lib/libGLES_CM.so.1.1.0 ${D}${libdir}/libGLES_CM.so.1.1.0
    ln -sf libGLES_CM.so.1.1.0 ${D}${libdir}/libGLES_CM.so.1
    ln -sf libGLES_CM.so.1.1.0 ${D}${libdir}/libGLES_CM.so
    install -m 0644 ${S}/gpu-core/usr/lib/libGLESv1_CM.so.1.1.0 ${D}${libdir}/libGLESv1_CM.so.1.1.0
    ln -sf libGLESv1_CM.so.1.1.0 ${D}${libdir}/libGLESv1_CM.so
    ln -sf libGLESv1_CM.so.1.1.0 ${D}${libdir}/libGLESv1_CM.so.1

    install -d ${D}${includedir}/GLES
    for name in ${S}/gpu-core/usr/include/GLES/*; do
        install -m 0644 "$name" ${D}${includedir}/GLES/
    done


    # Package libopenvg-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libOpenVG.3d.so ${D}${libdir}/libOpenVG.3d.so
    install -m 0644 ${S}/gpu-core/usr/lib/libOpenVG.so ${D}${libdir}/libOpenVG.so
    install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/vg.pc ${D}${libdir}/pkgconfig/vg.pc

    install -d ${D}${includedir}/VG
    for name in ${S}/gpu-core/usr/include/VG/*; do
        install -m 0644 "$name" ${D}${includedir}/VG/
    done


    # Package libglslc-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libGLSLC.so ${D}${libdir}/libGLSLC.so


    # Package libopencl-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libOpenCL.so ${D}${libdir}/libOpenCL.so
    install -m 0644 ${S}/gpu-core/usr/lib/libVivanteOpenCL.so ${D}${libdir}/libVivanteOpenCL.so
    install -d ${D}${sysconfdir}/OpenCL/vendors/
    install -m 0644 ${S}/gpu-core/etc/Vivante.icd ${D}${sysconfdir}/OpenCL/vendors/Vivante.icd

    install -d ${D}${includedir}/CL
    for name in ${S}/gpu-core/usr/include/CL/*; do
        install -m 0644 "$name" ${D}${includedir}/CL/
    done


    # Package libvsc-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libVSC.so ${D}${libdir}/libVSC.so


    # Package imx-gpu-viv-g2d
    install -m 0644 ${S}/g2d/usr/include/g2d.h ${D}${includedir}/g2d.h
    install -m 0644 ${S}/g2d/usr/lib/libg2d-viv.so ${D}${libdir}/libg2d-viv.so
    ln -sf libg2d-viv.so ${D}${libdir}/libg2d.so
    ln -sf libg2d-viv.so ${D}${libdir}/libg2d.so.0.8


    # Package libvdk-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libVDK.so ${D}${libdir}/libVDK.so
    install -m 0644 ${S}/gpu-core/usr/include/*vdk.h ${D}${includedir}/


    # Package libcsc-mx6
    install -m 0644 ${S}/gpu-core/usr/lib/libCLC.so ${D}${libdir}/libCLC.so


    # Package imx-gpu-viv-demos
    install -d ${D}/opt/viv_samples/
    install -d ${D}/opt/fsl-samples/
    # Permissions should be correct.
    cp -r ${S}/gpu-demos/opt/viv_samples ${D}/opt/
    cp -r ${S}/gpu-demos/opt/fsl-samples ${D}/opt/


    # Package imx-gpu-viv-tools
    install -d ${D}${bindir}
    install -m 0755 ${S}/gpu-tools/gmem-info/usr/bin/gmem_info ${D}${bindir}/gmem_info
}

# THE YOCTO PACKAGEING MECHANISM IS A CRAZY SHIT!!!! INCOMPREHENSIBLE!!!
# Some notes:
# - Package dependencies should mostly be correct.

# Arg INSANE_SKIP is not per package. It's per recipe!
# ARG ARG "already-stripped" is per recipe. Not per package!
INSANE_SKIP_${PN} = "already-stripped"

FILES_imx-gpu-viv-demos = "/opt/viv_samples/ /opt/fsl-samples/"
FILES_imx-gpu-viv-demos-dbg = " \
    /opt/viv_samples/*/*/.debug/* \
    /opt/fsl-samples/g2d/overlay_test/.debug/g2d_overlay_test \
    /opt/fsl-samples/g2d/.debug/g2d_test \
"
RDEPENDS_imx-gpu-viv-demos += " \
    libvdk-mx6 libegl-mx6 libgal-mx6 libgles2-mx6 libvsc-mx6 \
    libopencl-mx6 imx-gpu-viv-g2d \
"

FILES_libclc-mx6 = "${libdir}/libCLC.so"
FILES_libclc-mx6-dev = "${libdir}/libCLC.so"
FILES_libclc-mx6-dbg = "${libdir}/.debug/libCLC.so"
RDEPENDS_libclc-mx6 += "libvsc-mx6"

FILES_libvsc-mx6 = "${libdir}/libVSC.so"
FILES_libvsc-mx6-dev = ""
FILES_libvsc-mx6-dbg = "${libdir}/.debug/libVSC.so"

INSANE_SKIP_libgles2-mx6 += "dev-so"
FILES_libgles2-mx6 = "${libdir}/libGLESv2.so ${libdir}/libGLESv2.so.*"
FILES_libgles2-mx6-dev = "${includedir}/GLES2 ${libdir}/libGLESv2.so* ${libdir}/pkgconfig/glesv2.pc"
FILES_libgles2-mx6-dbg = "${libdir}/.debug/libGLESv2.*"
RDEPENDS_libgles2-mx6 += "libgal-mx6 libvsc-mx6 libopenvg-mx6 libglslc-mx6"
# NOTE: GLESv2 doesn't depend on libEGL
# qtbase depends on virtual/egl _and_ virtual/libgles2


INSANE_SKIP_libgles1-mx6 += "dev-so"
FILES_libgles1-mx6 = " \
    ${libdir}/libGLESv1* \
    ${libdir}/libGLES_* \
"
FILES_libgles1-mx6-dev = " \
    ${includedir}/GLES \
    ${libdir}/libGLESv1* \
    ${libdir}/libGLES_* \
    ${libdir}/pkgconfig/glesv1_cm.pc \
"
FILES_libgles1-mx6-dbg = " \
    ${libdir}/.debug/libGLESv1* \
    ${libdir}/.debug/libGLES_* \
"
RDEPENDS_libgles1-mx6 += "libgal-mx6"


FILES_libglslc-mx6 = "${libdir}/libGLSLC.so*"
FILES_libglslc-mx6-dev = "${libdir}/libGLSLC.so*"
FILES_libglslc-mx6-dbg = "${libdir}/.debug/libGLSLC.so*"
RDEPENDS_libglslc-mx6 += "libvivante-mx6 libvsc-mx6 libgal-mx6"

FILES_libopencl-mx6 = " \
	${libdir}/libOpenCL.so \
	${libdir}/libVivanteOpenCL.so \
	${sysconfdir}/OpenCL/vendors/ \
"
FILES_libopencl-mx6-dev = "${includedir}/CL ${libdir}/libOpenCL.so ${libdir}/libVivanteOpenCL.so"
FILES_libopencl-mx6-dbg = "${libdir}/.debug/libOpenCL.so ${libdir}/.debug/libVivanteOpenCL.so"
RDEPENDS_libopencl-mx6 = "libclc-mx6 libvsc-mx6"


INSANE_SKIP_libegl-mx6 += "dev-so"
FILES_libegl-mx6 = " \
    ${libdir}/libEGL.so* \
"
FILES_libegl-mx6-dev = " \
    ${includedir}/EGL \
    ${includedir}/KHR \
    ${libdir}/libEGL.so* \
    ${libdir}/pkgconfig/egl.pc \
"
FILES_libegl-mx6-dbg = " \
    ${libdir}/.debug/libEGL.so* \
"
RDEPENDS_libegl-mx6 += "libgal-mx6"


FILES_libgal-mx6 = "${libdir}/libGAL.*"
FILES_libgal-mx6-dev = "${libdir}/libGAL.* ${includedir}/HAL"
FILES_libgal-mx6-dbg = "${libdir}/.debug/libGAL.*"

FILES_libvivante-mx6 = "${libdir}/libVIVANTE.so"
FILES_libvivante-mx6-dev = "${libdir}/libVIVANTE.*"
FILES_libvivante-mx6-dbg = "${libdir}/.debug/libVIVANTE.*"

FILES_libopenvg-mx6 = "${libdir}/libOpenVG.* "
FILES_libopenvg-mx6-dev = "${includedir}/VG ${libdir}/libOpenVG.* ${libdir}/pkgconfig/vg.pc"
FILES_libopenvg-mx6-dbg = "${libdir}/.debug/libOpenVG.*"
RDEPENDS_libopenvg-mx6 += "libgal-mx6 libegl-mx6"

FILES_libvdk-mx6 = "${libdir}/libVDK.so"
FILES_libvdk-mx6-dev = "${includedir}/*vdk.h ${libdir}/libVDK.so"
FILES_libvdk-mx6-dbg = "${libdir}/.debug/libVDK.so"
RDEPENDS_libvdk-mx6 += "libegl-mx6"


# Avoid error for libg2d.so:
#   .../build/tmp-glibc/sysroots/x86_64-linux/usr/lib/rpm/bin/debugedit: \
#        canonicalization unexpectedly shrank by one character
PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

INSANE_SKIP_imx-gpu-viv-g2d += "dev-so"
FILES_imx-gpu-viv-g2d = "${libdir}/libg2d.* ${libdir}/libg2d-viv.*"
FILES_imx-gpu-viv-g2d-dev = "${includedir}/g2d.h"
FILES_imx-gpu-viv-g2d-dbg = "${libdir}/.debug/libg2d.* ${libdir}/.debug/libg2d-viv.*"
RDEPENDS_imx-gpu-viv-g2d += "libgal-mx6"

INSANE_SKIP_imx-gpu-viv-tools += "dev-so"
FILES_imx-gpu-viv-tools = "${bindir}/gmem_info"
FILES_imx-gpu-viv-tools-dbg = "${bindir}/.debug/gmem_info"


PACKAGE_ARCH = "${MACHINE_SOCARCH}"
COMPATIBLE_MACHINE = "(mx6)"
