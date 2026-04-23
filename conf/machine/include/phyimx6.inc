require phytec-machine-common.inc

# Common settings for phytecs imx6 boards

include conf/machine/include/soc-family.inc

SOC_FAMILY = "imx-generic-bsp:mx6-generic-bsp"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"

# Kernel
PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec"
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

MACHINE_FEATURES = "apm usbgadget usbhost vfat caam gpu screen"


#WIC
IMAGE_CLASSES += "wic-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES = "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', \
    'fitImage', \
    'zImage oftree ${EXTERNAL_KERNEL_DEVICETREE}', d)}"
BAREBOX_BINARY = "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', \
    'barebox-s.bin', 'barebox.bin', d)}"
WKS_FILES:mx6-generic-bsp ?= "imx6-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', '90', '20', d)}"

# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx6-generic-bsp ?= "1"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx6-generic-bsp ?= "0"

# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx6-generic-bsp ?= "3"
NAND_DEV:mx6-generic-bsp ?= "0"

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
