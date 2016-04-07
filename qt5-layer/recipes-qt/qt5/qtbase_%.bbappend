FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# The string 'gl' is in PACKAGECONFIG because "opengl" is in yogurt's
# DISTRO_FEATURES. Since our boards only support egl/gles2 and not the full
# opengl, we have to disable gl and enable gles2 by hand here.
PACKAGECONFIG_remove = "gl"
PACKAGECONFIG_append_ti33x = " gles2"
PACKAGECONFIG_append_mx6 = " gles2"

# From the layer meta-fsl-arm. Fix qtbase build.
SRC_URI_append_mx6 = " file://Force_egl_visual_ID_33.patch"
do_configure_prepend_mx6() {
# adapt qmake.conf to our needs
sed -i 's!load(qt_config)!!' ${S}/mkspecs/linux-oe-g++/qmake.conf

# copy the hook in the mkspecs directory OE is using
cp ${S}/mkspecs/devices/linux-imx6-g++/qeglfshooks_imx6.cpp ${S}/mkspecs/linux-oe-g++/

cat >> ${S}/mkspecs/linux-oe-g++/qmake.conf <<EOF
EGLFS_PLATFORM_HOOKS_SOURCES = \$\$PWD/qeglfshooks_imx6.cpp
IMX6_CFLAGS             = -DLINUX=1 -DEGL_API_FB=1
QMAKE_LIBS_EGL         += -lEGL
QMAKE_LIBS_OPENGL_ES2  += -lGLESv2 -lEGL -lGAL
QMAKE_LIBS_OPENVG      += -lOpenVG -lEGL -lGAL
QMAKE_CFLAGS_RELEASE   += \$\$IMX6_CFLAGS
QMAKE_CXXFLAGS_RELEASE += \$\$IMX6_CFLAGS
QMAKE_CFLAGS_DEBUG     += \$\$IMX6_CFLAGS
QMAKE_CXXFLAGS_DEBUG   += \$\$IMX6_CFLAGS

load(qt_config)

EOF
}
