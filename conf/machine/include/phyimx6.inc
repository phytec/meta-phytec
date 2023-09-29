# Common settings for phytecs imx6 boards

include conf/machine/include/soc-family.inc

SOC_FAMILY = "imx-generic-bsp:mx6-generic-bsp"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"

# Kernel
PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"

# dont install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# XServer
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
# We don't support a Xserver yet.
#XSERVER_DRIVER = "xf86-video-fbdev"
#XSERVER_DRIVER:mx6 = "xf86-video-imxfb-vivante"
#XSERVER = "xserver-xorg \
#           xf86-input-evdev \
#           ${XSERVER_DRIVER}"

# Ship kernel modules
MACHINE_EXTRA_RRECOMMENDS = "kernel-modules"
MACHINE_EXTRA_RDEPENDS = "firmware-imx-sdma-imx6q"

SERIAL_CONSOLES = "115200;ttymxc0"

MACHINE_FEATURES = "apm usbgadget usbhost vfat caam"


#WIC
IMAGE_CLASSES += "wic-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES = "zImage oftree ${EXTERNAL_KERNEL_DEVICETREE}"
IMAGE_BOOT_FILES:secureboot = "fitImage"
BAREBOX_BINARY = "barebox.bin"
BAREBOX_BINARY:secureboot = "barebox-s.bin"
WKS_FILES:mx6-generic-bsp ?= "imx6-sdimage.wks"
WKS_FILES:mx6-generic-bsp:update ?= "imx6-rauc-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "20"
WKS_BOOTIMAGESIZE:secureboot ?= "90"

# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx6-generic-bsp ?= "1"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx6-generic-bsp ?= "0"

# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx6-generic-bsp ?= "3"
NAND_DEV:mx6-generic-bsp ?= "0"

# rootfs alignment required for RAUC adaptive update
IMAGE_ROOTFS_ALIGNMENT:update = "4"

# partup
IMAGE_CLASSES:append:partup-package = " image-types-partup"
include partup-layout-config.inc
PARTUP_IMAGE_FSTYPE = ""
PARTUP_IMAGE_FSTYPE:partup-package = "partup"

# default images to build
IMAGE_FSTYPES ?= "tar.gz wic ubifs ${PARTUP_IMAGE_FSTYPE}"
IMAGE_FSTYPES:append:update = " ext4"
