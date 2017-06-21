SUMMARY = "QT 5.7 QML 2D Dummy OpenGL libraries"
DESCRIPTION = "QML scenegraph renderer uses OpenGL to render. This dummy \
implementation is required by the 2D renderer."
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.GPL3;md5=d32239bcb673463ab874e80d47fae504"

PROVIDES += "virtual/egl virtual/libgles2"

SRC_URI = "git://github.com/qt/qtdeclarative-render2d.git;protocol=https;branch=5.7"

PV = "5.7+git${SRCPV}"
SRCREV = "fe2807312ff3d2285b51a4de363b1c1fb8d85f82"

S = "${WORKDIR}/git"

do_compile() {
   cd ${S}/tools/opengldummy/src/
   ${CC} ${CFLAGS} ${LDFLAGS} -DQGS_BUILD_CLIENT_DLL -fPIC -shared -I../3rdparty/include -o libEGL.so egl.cpp
   ${CC} ${CFLAGS} ${LDFLAGS} -DQGS_BUILD_CLIENT_DLL -fPIC -shared -I../3rdparty/include -o libGLESv2.so gles2.cpp
}

do_install() {
    install -d ${D}${includedir}
    cp -rd ${S}/tools/opengldummy/3rdparty/include/* ${D}${includedir}
    install -d ${D}${libdir}
    cp -rd ${S}/tools/opengldummy/src/lib* ${D}${libdir}/
}

FILES_${PN} += "${libdir}"
FILES_${PN}-dev = "${includedir}"
