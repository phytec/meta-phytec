# Common settings for PHYTEC's i.MX7 boards

PREFERRED_PROVIDER_virtual/kernel = "linux-imx"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot-imx"
PREFERRED_PROVIDER_u-boot = "u-boot-imx"
PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"

# cpu feature overwrite support
MACHINEOVERRIDES .= ":imxpxp"

QT_QPA_PLATFORM ?= "linuxfb"

# Disable gstreamer plugins by default
PACKAGECONFIG_pn-gstreamer1.0-plugins-imx = ""
# UBIFS
UBI_VOLNAME = "root"
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 1912"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

# WIC
IMAGE_BOOT_FILES = "zImage ${KERNEL_DEVICETREE}"
WKS_FILES_mx7 ?= "imx7-sdimage.wks"

# Default images to build
IMAGE_FSTYPES = "tar.xz wic ubifs"

RRECOMMENDS_gst-examples-bin_remove_pn-gst-examples = "gstreamer1.0-plugins-bad-meta"
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
PACKAGECONFIG_remove_pn-xserver-xorg = "xwayland"
