
# The string 'gl' is in PACKAGECONFIG because "opengl" is in yogurt's
# DISTRO_FEATURES. Since our boards only support egl/gles2 and not the full
# opengl, we have to disable gl and enable gles2 by hand here.
PACKAGECONFIG_GL_mx6ul = "linuxfb"
PACKAGECONFIG_GL_imxgpu3d = "gles2 eglfs"
PACKAGECONFIG_GL_ti33x = "gles2 eglfs"
PACKAGECONFIG_GL_rk3288 = "gles2 eglfs"

# From the layer meta-fsl-arm. Fix qtbase build.
do_configure_prepend_imxgpu3d() {
# adapt qmake.conf to our needs
sed -i 's!load(qt_config)!!' ${S}/mkspecs/linux-oe-g++/qmake.conf

cat >> ${S}/mkspecs/linux-oe-g++/qmake.conf <<EOF
IMX6_CFLAGS             = -DLINUX=1 -DEGL_API_FB=1
EGLFS_DEVICE_INTEGRATION = eglfs_viv
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

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += " file://0001-eglfs_kms_egldevice-fix-include-path-mismatch-for-dr.patch"
