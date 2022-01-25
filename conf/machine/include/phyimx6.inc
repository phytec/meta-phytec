# Common settings for phytecs imx6 boards

include conf/machine/include/soc-family.inc

SOC_FAMILY = "imx:mx6"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"

# Kernel
PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"

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
MACHINE_EXTRA_RDEPENDS = "firmware-imx-sdma-imx6q"

SERIAL_CONSOLES = "115200;ttymxc0"

MACHINE_FEATURES = "apm usbgadget usbhost vfat caam"


#WIC
IMAGE_CLASSES += "wic-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES = "zImage oftree ${KERNEL_DEVICETREE} ${EXTERNAL_KERNEL_DEVICETREE}"
IMAGE_BOOT_FILES_secureboot = "fitImage"
BAREBOX_BINARY = "barebox.bin"
BAREBOX_BINARY_secureboot = "barebox-s.bin"
WKS_FILES_mx6 ?= "imx6-sdimage.wks"
WKS_FILES_mx6_update ?= "imx6-rauc-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "20"
WKS_BOOTIMAGESIZE_secureboot ?= "90"

# MMC user area bootloader image offset
BOOTLOADER_SEEK_mx6 ?= "1"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC_mx6 ?= "0"

# Device information used by RAUC and some of its dependencies
EMMC_DEV_mx6 ?= "3"
NAND_DEV_mx6 ?= "0"

# default images to build
IMAGE_FSTYPES ?= "tar.gz wic ubifs"
