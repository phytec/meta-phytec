FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

# Avoid dependency virtual/gl when buliding core-image-sato
PACKAGECONFIG_remove_imx = "dri"
PACKAGECONFIG_remove_imx = "glx"
PACKAGECONFIG_remove_ti33x = "dri"
PACKAGECONFIG_remove_ti33x = "glx"
PACKAGECONFIG_remove_rk3288 = "dri"
PACKAGECONFIG_remove_rk3288 = "glx"

SRC_URI += "file://0005-hw-xwayland-Makefile.am-fix-build-without-glx.patch \
"
