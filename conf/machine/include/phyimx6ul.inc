# Common settings for phytecs imx6ul boards
include conf/machine/include/soc-family.inc

SOC_FAMILY .= "imx:mx6ul"

DEFAULTTUNE ?= "cortexa7thf-neon-vfpv4"
include conf/machine/include/tune-cortexa7.inc

SERIAL_CONSOLES ?= "115200;ttymxc0"

# Sub-architecture support
MACHINE_SOCARCH = "${TUNE_PKGARCH}-mx6ul"
PACKAGE_EXTRA_ARCHS_append = " ${MACHINE_SOCARCH}"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
# Kernel
PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"
# Install modules in initramfs and roofs
PACKAGE_INSTALL_append = " kernel-modules"
# Ship kernel modules
MACHINE_EXTRA_RRECOMMENDS = "kernel-modules"
# dont install kernelimage to rootfs
RDEPENDS_${KERNEL_PACKAGE_NAME}-base = ""

# cpu feature overwrite support
MACHINEOVERRIDES .= ":imxpxp"

MACHINE_FEATURES = "apm usbgadget usbhost vfat"

QT_QPA_PLATFORM ?= "linuxfb"

# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 7943"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

#WIC
IMAGE_CLASSES += "wic-helper"
IMAGE_BOOT_FILES = "zImage ${@parse_dtbs(d)}"
WKS_FILES_mx6ul ?= "imx6-sdimage.wks"

# default images to build
IMAGE_FSTYPES ?= "tar.gz wic ubifs"

# Avoid pulling package 'gstreamer1.0-plugins-bad-meta' into the image
# 'core-image-sato', because the recipe gstreamer1.0-plugins-bad depends on
# virtual/libgles2.
RRECOMMENDS_gst-player-bin_remove_pn-gst-player = "gstreamer1.0-plugins-bad-meta"

# XServer
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
# We don't support a Xserver yet.
# Don't build the X server with xwayland support. It depends on libepoxy
# (OpenGL function pointer management library) that depends on EGL.
PACKAGECONFIG_remove_pn-xserver-xorg = "xwayland"

# Disable gstreamer plugins by default
PACKAGECONFIG_pn-gstreamer1.0-plugins-imx = ""
