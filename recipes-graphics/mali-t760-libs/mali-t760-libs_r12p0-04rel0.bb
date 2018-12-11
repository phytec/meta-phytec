SUMMARY = "Userspace GPU driver for Mali-T760"
DESCRIPTION = "Userspace GPU driver for Mali-T760"
SECTION = "libs"
LICENSE = "ARM-MALI-EULA"
LIC_FILES_CHKSUM = "file://END_USER_LICENCE_AGREEMENT.txt;md5=3918cc9836ad038c5a090a0280233eea"

PROVIDES += "libgbm virtual/libgles2 virtual/libgles1 virtual/egl virtual/opencl virtual/wayland-egl"

DEPENDS += "wayland libdrm"
RDEPENDS_${PN} += "wayland libdrm"

PR = "r0"
S = "${WORKDIR}/mali-t760-libs_${PV}"

# checksums for mali-t760-libs_r12p0-04rel0.tar.gz
SRC_URI[md5sum] = "71790b9a635263188bd88e539d24d89c"
SRC_URI[sha256sum] = "f1ac336dd72401ec2656a9ef71fcee11d50a2f38d48fbd25a07f7750d1543a60"

SRC_URI = "ftp://ftp.phytec.de/pub/Software/Linux/Driver/mali-t760/mali-t760-libs_${PV}.tar.gz \
	file://0001-gbm.h-Add-a-missing-stddef.h-include-for-size_t.patch \
"

PACKAGES = "${PN} ${PN}-dev ${PN}-dbg"

do_install() {
	install -d ${D}${includedir}
	install -m 0644 gbm/inc/gbm.h ${D}${includedir}

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
	cp -rd lib* ${D}${libdir}/
}

FILES_${PN} += "${libdir}"

FILES_${PN}-dev += " \
	${includedir} \
	${libdir} \
	${libdir}/pkgconfig \
"

INSANE_SKIP_${PN} = "dev-so already-stripped ldflags"

# Fix warnings:
#   WARNING: do_package_qa: QA Issue: libqminimalegl.so contained in package qtbase-plugins
#        requires libEGL.so, but no providers found in RDEPENDS_qtbase-plugins? [file-rdeps]
#   WARNING: do_package_qa: QA Issue: libQt5OpenGL.so.5.6.1 contained in package qtbase
#        requires libGLESv2.so, but no providers found in RDEPENDS_qtbase? [file-rdeps]
RPROVIDES_${PN} = "libwayland-egl.so libgbm.so libGLESv1_CM.so libGLESv2.so libEGL.so libOpenCL.so"

COMPATIBLE_MACHINE = "rk3288"
