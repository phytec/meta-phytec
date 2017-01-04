# Common settings for phytecs imx6ul boards

include conf/machine/include/phyimx6.inc
include conf/machine/include/tune-cortexa7.inc

DEFAULTTUNE = "cortexa7hf-neon"

SERIAL_CONSOLES ?= "115200;ttymxc0"

# Sub-architecture support
MACHINE_SOCARCH = "${TUNE_PKGARCH}-mx6ul"
PACKAGE_EXTRA_ARCHS_append = " ${MACHINE_SOCARCH}"

# cpu feature overwrite support
MACHINEOVERRIDES .= ":imxpxp"

QT_QPA_DEFAULT_PLATFORM ?= "linuxfb"

# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 7943"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

# Avoid pulling package 'gstreamer1.0-plugins-bad-meta' into the image
# 'core-image-sato', because the recipe gstreamer1.0-plugins-bad depends on
# virtual/libgles2.
RRECOMMENDS_gst-player-bin_remove_pn-gst-player = "gstreamer1.0-plugins-bad-meta"

# Don't build the X server with xwayland support. It depends on libepoxy
# (OpenGL function pointer management library) that depends on EGL.
PACKAGECONFIG_remove_pn-xserver-xorg = "xwayland"
