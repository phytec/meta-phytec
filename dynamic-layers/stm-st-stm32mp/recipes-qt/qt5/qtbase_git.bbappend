FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG_GL_append = " linuxfb"

#for qt widget applications add this in your layer
#PACKAGECONFIG_append = " widgets"

#QT_QPA_PLATFORM ??= "eglfs"
#QT_CONFIG_FLAGS += "-qpa ${QT_QPA_PLATFORM}"


