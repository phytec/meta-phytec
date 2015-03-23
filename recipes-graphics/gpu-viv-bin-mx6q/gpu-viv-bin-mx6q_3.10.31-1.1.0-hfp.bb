# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>
#
# Based on the recipe in the layer meta-fsl-arm.
#   Copyright (C) 2012-2013 Freescale Semiconductor
#   Copyright (C) 2012-2014 O.S. Systems Software LTDA.
#   Released under the MIT license (see COPYING.MIT for the terms)

inherit fsl-eula-unpack

DESCRIPTION = "GPU driver and apps for imx6"
SECTION = "libs"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/gc_vdk.h;endline=11;md5=c4713c78d7f52bf2f92688a6f8f0cc93"

# The recipe provides multiple packages for the Freescale demo programs,
# libgles1, libgles2 and egl support. The libgles1, libgles2 and libegl packages
# are mostly pulled in by dependences of other packages, like qtbase. The
# Freescale demo programs are not installed by default into the rootfs, because
# the package is roughly 600MB. To install it, put
#     IMAGE_INSTALL_append = " gpu-viv-bin-mx6q"
# into your local.conf.


# Require the same distro feature as the mesa recipe
REQUIRED_DISTRO_FEATURES = "opengl"

# We don't provide virtual/libgles1 and virtual/libgl yet.
PROVIDES += "virtual/libgles2 virtual/egl"

PR = "r0"

_PV_beta = "${@'${PV}'.replace('1.1.0', '1.1.0-beta')}"
SRC_URI = "http://www.freescale.com/lgfiles/NMG/MAD/YOCTO/${PN}-${_PV_beta}.bin;fsl-eula=true \
    file://egl.pc \
    file://glesv2.pc \
    file://vg.pc \
"
S = "${WORKDIR}/${PN}-${_PV_beta}"

SRC_URI[md5sum] = "8aa5c16021ce38762e7e3c07a57146eb"
SRC_URI[sha256sum] = "c132de60b28c73e8d6ea12219151ca9a0a0bb4f73d62ca1bdd0feac6db0d964e"

# The package doesn't create a 'gpu-viv-bin-mx6q' package. So overwrite
# PACKAGES with an empty string.
PACKAGES = ""
PACKAGES += " \
    libegl-mx6 libegl-mx6-dev libegl-mx6-dbg \
    libgles2-mx6 libgles2-mx6-dev libgles2-mx6-dbg \
    libgles1-mx6 libgles1-mx6-dev libgles1-mx6-dbg \
    libgal-mx6 libgal-mx6-dev libgal-mx6-dbg \
    libvivante-mx6 libvivante-mx6-dev libvivante-mx6-dbg \
    libopenvg-mx6 libopenvg-mx6-dev libopenvg-mx6-dbg \
    libglslc-mx6 libglslc-mx6-dev libglslc-mx6-dbg \
    libvsc-mx6 libvsc-mx6-dev libvsc-mx6-dbg \
"

# Skip package if it does not match the machine float-point type in use
python __anonymous () {
        is_machine_hardfp = base_contains("TUNE_FEATURES", "callconvention-hard", True, False, d)

        if not is_machine_hardfp:
                raise bb.parse.SkipPackage("Package Float-Point is not compatible with the machine")
}


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
              ("libgles3",)):
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
    install -m 0644 ${S}/usr/lib/libGAL-fb.so ${D}${libdir}/libGAL.so

    install -d ${D}${includedir}/HAL
    for name in ${S}/usr/include/HAL/*; do
        install -m 0644 "$name"  ${D}${includedir}/HAL/
    done


    # Package libvivante-mx6
    install -m 0644 ${S}/usr/lib/libVIVANTE-fb.so ${D}${libdir}/libVIVANTE.so


    # Package libegl-mx6
    install -m 0644 ${S}/usr/lib/libEGL-fb.so ${D}${libdir}/libEGL.so.1.0
    ln -sf libEGL.so.1.0 ${D}${libdir}/libEGL.so.1
    ln -sf libEGL.so.1.0 ${D}${libdir}/libEGL.so
    install -m 0644 ${WORKDIR}/egl.pc ${D}${libdir}/pkgconfig/egl.pc
    install -m 0644 ${S}/usr/lib/libGAL_egl.fb.so ${D}${libdir}/libGAL_egl.so

    install -d ${D}${includedir}/EGL
    for name in ${S}/usr/include/EGL/*; do
        install -m 0644 "$name"  ${D}${includedir}/EGL/
    done
    install -d ${D}${includedir}/KHR/
    for name in ${S}/usr/include/KHR/*; do
        install -m 0644 "$name"  ${D}${includedir}/KHR/
    done


    # Package libgles2-mx6
    install -m 0644 ${S}/usr/lib/libGLESv2-fb.so ${D}${libdir}/libGLESv2.so.2.0.0
    ln -sf libGLESv2.so.2.0.0 ${D}${libdir}/libGLESv2.so.2
    ln -sf libGLESv2.so.2.0.0 ${D}${libdir}/libGLESv2.so
    install -m 0644 ${WORKDIR}/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc

    install -d ${D}${includedir}/GLES2
    for name in ${S}/usr/include/GLES2/*; do
        install -m 0644 "$name"  ${D}${includedir}/GLES2/
    done


    # Package libgles1-mx6
    install -m 0644 ${WORKDIR}/glesv1_cm.pc ${D}${libdir}/pkgconfig/glesv1_cm.pc
    install -m 0644 ${S}/usr/lib/libGLESv1_CL.so.1.1.0 ${D}${libdir}/libGLESv1_CL.so.1.1.0
    ln -sf libGLESv1_CL.so.1.1.0 ${D}${libdir}/libGLESv1_CL.so.1
    ln -sf libGLESv1_CL.so.1.1.0 ${D}${libdir}/libGLESv1_CL.so
    install -m 0644 ${S}/usr/lib/libGLES_CL.so.1.1.0 ${D}${libdir}/libGLES_CL.so.1.1.0
    ln -sf libGLES_CL.so.1.1.0 ${D}${libdir}/libGLES_CL.so.1
    ln -sf libGLES_CL.so.1.1.0 ${D}${libdir}/libGLES_CL.so
    install -m 0644 ${S}/usr/lib/libGLES_CM.so.1.1.0 ${D}${libdir}/libGLES_CM.so.1.1.0
    ln -sf libGLES_CM.so.1.1.0 ${D}${libdir}/libGLES_CM.so.1
    ln -sf libGLES_CM.so.1.1.0 ${D}${libdir}/libGLES_CM.so
    install -m 0644 ${S}/usr/lib/libGLESv1_CM.so.1.1.0 ${D}${libdir}/libGLESv1_CM.so.1.1.0
    ln -sf libGLESv1_CM.so.1.1.0 ${D}${libdir}/libGLESv1_CM.so
    ln -sf libGLESv1_CM.so.1.1.0 ${D}${libdir}/libGLESv1_CM.so.1

    install -d ${D}${includedir}/GLES
    for name in ${S}/usr/include/GLES/*; do
        install -m 0644 "$name"  ${D}${includedir}/GLES/
    done


    # Package libopenvg-mx6
    install -m 0644 ${S}/usr/lib/libOpenVG.3d.so  ${D}${libdir}/libOpenVG.3d.so
    install -m 0644 ${S}/usr/lib/libOpenVG.so  ${D}${libdir}/libOpenVG.so
    install -m 0644 ${WORKDIR}/vg.pc ${D}${libdir}/pkgconfig/vg.pc

    install -d ${D}${includedir}/VG
    for name in ${S}/usr/include/VG/*; do
        install -m 0644 "$name"  ${D}${includedir}/VG/
    done


    # Package libglslc-mx6
    install -m 0644 ${S}/usr/lib/libGLSLC.so ${D}${libdir}/libGLSLC.so

    install -d ${D}${includedir}/CL
    for name in ${S}/usr/include/CL/*; do
        install -m 0644 "$name"  ${D}${includedir}/CL/
    done


    # Package libvsc-mx6
    install -m 0644 ${S}/usr/lib/libVSC.so ${D}${libdir}/libVSC.so


    #hacky
    find ${D}${libdir} -type f -exec chmod 644 {} \;
    find ${D}${includedir} -type f -exec chmod 644 {} \;
}

# THE YOCTO PACKAGEING MECHANISM IS A CRAZY SHIT!!!! INCOMPREHENSIBLE!!!
# Some notes:
# - Package dependencies should mostly be correct.


FILES_libvsc-mx6 = "${libdir}/libVSC.so"
FILES_libvsc-mx6-dev = ""
FILES_libvsc-mx6-dbg = "${libdir}/.debug/libVSC.so"

INSANE_SKIP_libgles2-mx6 += "dev-so"
FILES_libgles2-mx6 = "${libdir}/libGLESv2.so ${libdir}/libGLESv2.so.*"
FILES_libgles2-mx6-dev = "${includedir}/GLES2 ${libdir}/libGLESv2.so* ${libdir}/pkgconfig/glesv2.pc"
FILES_libgles2-mx6-dbg = "${libdir}/.debug/libGLESv2.*"
RDEPENDS_libgles2-mx6 += "libgal-mx6"
RDEPENDS_libgles2-mx6 += "libvsc-mx6"
RDEPENDS_libgles2-mx6 += "libopenvg-mx6"
RDEPENDS_libgles2-mx6 += "libglslc-mx6"
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
FILES_libglslc-mx6-dev = "${includedir}/CL ${libdir}/libGLSLC.so*"
FILES_libglslc-mx6-dbg = "${libdir}/.debug/libGLSLC.so*"
RDEPENDS_libglslc-mx6 += "libvivante-mx6 libvsc-mx6 libgal-mx6"


INSANE_SKIP_libegl-mx6 += "dev-so"
FILES_libegl-mx6 = " \
    ${libdir}/libEGL.so* \
    ${libdir}/libGAL_egl.so \
"
FILES_libegl-mx6-dev = " \
    ${includedir}/EGL \
    ${includedir}/KHR \
    ${libdir}/libEGL.so* \
    ${libdir}/libGAL_egl.so \
    ${libdir}/pkgconfig/egl.pc \
"
FILES_libegl-mx6-dbg = " \
    ${libdir}/.debug/libEGL.so* \
    ${libdir}/.debug/libGAL_egl.so \
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


COMPATIBLE_MACHINE = "(mx6)"
