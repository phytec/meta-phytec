require phytec-machine-common.inc

# Common settings for phytecs imx6ul boards
include conf/machine/include/soc-family.inc

SOC_FAMILY .= "imx-generic-bsp:mx6ul-generic-bsp"

DEFAULTTUNE ?= "cortexa7thf-neon-vfpv4"
include conf/machine/include/arm/armv7a/tune-cortexa7.inc

SERIAL_CONSOLES ?= "115200;ttymxc0"

# Sub-architecture support
MACHINE_SOCARCH = "${TUNE_PKGARCH}-mx6ul"
PACKAGE_EXTRA_ARCHS:append = " ${MACHINE_SOCARCH}"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"

# Kernel
PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec"
KERNEL_IMAGETYPE ?= "zImage"

# Ship kernel modules
MACHINE_EXTRA_RRECOMMENDS = "kernel-modules"
MACHINE_EXTRA_RDEPENDS = "firmware-imx-sdma-imx6q"

# dont install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# cpu feature overwrite support
MACHINEOVERRIDES .= ":imxpxp"

MACHINE_FEATURES = "apm camera usbgadget usbhost vfat"

QT_QPA_PLATFORM ?= "linuxfb"

# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 7943"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

#WIC
IMAGE_CLASSES += "wic-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES = "zImage oftree ${EXTERNAL_KERNEL_DEVICETREE}"
IMAGE_BOOT_FILES:secureboot = "fitImage"
BAREBOX_BINARY = "barebox.bin"
BAREBOX_BINARY:secureboot = "barebox-s.bin"
WKS_FILES:mx6ul-generic-bsp ?= "imx6-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "20"
WKS_BOOTIMAGESIZE:secureboot ?= "60"

# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx6ul-generic-bsp ?= "1"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx6ul-generic-bsp ?= "0"

# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx6ul-generic-bsp ?= "1"
NAND_DEV:mx6ul-generic-bsp ?= "0"

# partup
IMAGE_CLASSES += "image-types-partup"
require partup-layout-config.inc

# default images to build
IMAGE_FSTYPES ?= " \
    tar.gz wic wic.bmap partup \
    ${@bb.utils.contains('MACHINE_FEATURES', 'emmc', '', 'ubifs', d)} \
"
IMAGE_FSTYPES:update = " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'emmc', 'partup', 'ubifs', d)} \
"

# Avoid pulling package 'gstreamer1.0-plugins-bad-meta' into the image
# 'core-image-sato', because the recipe gstreamer1.0-plugins-bad depends on
# virtual/libgles2.
RRECOMMENDS:gst-examples-bin:remove:pn-gst-examples = "gstreamer1.0-plugins-bad-meta"

# XServer
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
# We don't support a Xserver yet.
# Don't build the X server with xwayland support. It depends on libepoxy
# (OpenGL function pointer management library) that depends on EGL.
PACKAGECONFIG:remove:pn-xserver-xorg = "xwayland"

# Disable gstreamer plugins by default
PACKAGECONFIG:pn-gstreamer1.0-plugins-imx = ""

# Install devicetree overlays
DT_OVERLAYS_INSTALL = " \
    imx6ul-vm009.dtbo \
    imx6ul-vm010-bw.dtbo \
    imx6ul-vm010-col.dtbo \
    imx6ul-vm011-bw.dtbo \
    imx6ul-vm011-col.dtbo \
    imx6ul-vm016.dtbo \
"
