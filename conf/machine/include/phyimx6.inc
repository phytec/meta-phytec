# Common settings for phytecs imx6 boards

include conf/machine/include/soc-family.inc

SOC_FAMILY = "imx:mx6"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"
# Install modules in initramfs and roofs
PACKAGE_INSTALL_append = " kernel-modules"

# dont install kernelimage to rootfs
RDEPENDS_${KERNEL_PACKAGE_NAME}-base = ""

# XServer
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
# We don't support a Xserver yet.
#XSERVER_DRIVER = "xf86-video-fbdev"
#XSERVER_DRIVER_mx6 = "xf86-video-imxfb-vivante"
#XSERVER = "xserver-xorg \
#           xf86-input-evdev \
#           ${XSERVER_DRIVER}"

# Ship kernel modules
MACHINE_EXTRA_RRECOMMENDS = "kernel-modules"

SERIAL_CONSOLES = "115200;ttymxc0"

MACHINE_FEATURES = "apm usbgadget usbhost vfat"


#WIC
IMAGE_CLASSES += "wic-helper"
IMAGE_BOOT_FILES = "zImage oftree ${KERNEL_DEVICETREE}"
IMAGE_BOOT_FILES_secureboot = "fitImage.fitimg"
WKS_FILES_mx6 = "imx6-sdimage.wks"
WKS_FILES_secureboot_mx6 = "imx6-secureboot-sdimage.wks"

# default images to build
IMAGE_FSTYPES ?= "tar.gz wic ubifs"
