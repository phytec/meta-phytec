# Avoid dependency virtual/gl when buliding core-image-sato
PACKAGECONFIG_remove_imx = "dri"
PACKAGECONFIG_remove_imx = "glx"
PACKAGECONFIG_remove_ti33x = "dri"
PACKAGECONFIG_remove_ti33x = "glx"
PACKAGECONFIG_remove_rk3288 = "dri"
PACKAGECONFIG_remove_rk3288 = "glx"

FILESEXTRAPATHS_prepend := "${THISDIR}/xserver-xorg:"
SRC_URI += "file://0001-hw-xwayland-Makefile.am-fix-build-without-glx.patch "
