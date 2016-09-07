# Copyright (C) 2016 Wadim Egorov <w.egorov@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Userspace GPU driver for Mali-T760"
DESCRIPTION = "Userspace GPU driver for Mali-T760"
SECTION = "libs"
LICENSE = "proprietary-binary"
LIC_FILES_CHKSUM = "file://END_USER_LICENCE_AGREEMENT.txt;md5=6c3209233a1523d6c38e3c188ec54e70"

PROVIDES += "virtual/libgles2 virtual/libgles1 virtual/egl virtual/opencl"

PR = "r0"
S = "${WORKDIR}/mali-t760-libs_${PV}"

# checksums for mali-t760-libs_r9p0-05rel0.tar.gz
SRC_URI[md5sum] = "59b392b5b976214d6321156fa4170517"
SRC_URI[sha256sum] = "9740b604e408c94b5b8c152110ef8aed3920b876bcd9ef84193beb9f79f8f8ba"

SRC_URI = "ftp://ftp.phytec.de/pub/Software/Linux/Driver/mali-t760/mali-t760-libs_${PV}.tar.gz"

PACKAGES = "${PN} ${PN}-dev ${PN}-dbg"

do_install() {
	install -d ${D}${includedir}/EGL
	install -m 0644 v2.4.4-gles/inc/EGL/* ${D}${includedir}/EGL
	install -d ${D}${includedir}/GLES
	install -m 0644 v2.4.4-gles/inc/GLES/* ${D}${includedir}/GLES
	install -d ${D}${includedir}/GLES2
	install -m 0644 v2.4.4-gles/inc/GLES2/* ${D}${includedir}/GLES2
	install -d ${D}${includedir}/GLES3
	install -m 0644 v2.4.4-gles/inc/GLES3/* ${D}${includedir}/GLES3
	install -d ${D}${includedir}/KHR
	install -m 0644 v2.4.4-gles/inc/KHR/* ${D}${includedir}/KHR

	install -d ${D}${includedir}/CL
	install -m 0644 v1.1.0-cl/inc/CL/* ${D}${includedir}/CL

	install -d ${D}${libdir}/pkgconfig
	install -m 0644 pkgconfig/* ${D}${libdir}/pkgconfig
	cp -av lib* ${D}${libdir}/
	install -m 0644 libmali.so ${D}${libdir}/libmali.so
}

FILES_${PN} += "${libdir}"

FILES_${PN}-dev += " \
	${includedir} \
	${libdir} \
	${libdir}/pkgconfig \
"

INSANE_SKIP_${PN} = "dev-so"

# Fix warnings:
#   WARNING: do_package_qa: QA Issue: libqminimalegl.so contained in package qtbase-plugins
#        requires libEGL.so, but no providers found in RDEPENDS_qtbase-plugins? [file-rdeps]
#   WARNING: do_package_qa: QA Issue: libQt5OpenGL.so.5.6.1 contained in package qtbase
#        requires libGLESv2.so, but no providers found in RDEPENDS_qtbase? [file-rdeps]
RPROVIDES_${PN} = "libGLESv1_CM.so libGLESv2.so libEGL.so libOpenCL.so"

COMPATIBLE_MACHINE = "rk3288"
