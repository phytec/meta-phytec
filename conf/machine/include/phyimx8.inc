require phytec-machine-common.inc

# Common settings for PHYTEC's i.MX8 boards

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-master"
PREFERRED_PROVIDER_virtual/bootloader:mx8-nxp-bsp ?= "u-boot-phytec-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto-dev"
PREFERRED_PROVIDER_virtual/kernel:mx8-nxp-bsp ?= "linux-phytec-imx"
PREFERRED_PROVIDER_imx-boot = "imx-boot-phytec"
PREFERRED_PROVIDER_opencl-headers:imxgpu = "imx-gpu-viv"
PREFERRED_PROVIDER_linux-imx-mfgtool:mx8-nxp-bsp = "linux-imx-mfgtool"
PREFERRED_PROVIDER_virtual/libgles3:mx8-nxp-bsp = "imx-gpu-viv"

# dont install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# remove ath10k firmware to save space
MACHINE_FIRMWARE:remove:mx8-nxp-bsp = "linux-firmware-ath10k"

KERNEL_MODULE_AUTOLOAD += " cryptodev"

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:append:mx8-nxp-bsp = " wic-imx8-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE}"
WKS_FILES:mx8m-generic-bsp ?= "imx8m-sdimage.wks"
WKS_FILES:mx8x-nxp-bsp ?= "imx8x-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "50"
WIC_CREATE_EXTRA_ARGS:remove = "--no-fstab-update"

IMX_DEFAULT_ATF_PROVIDER:use-mainline-bsp ?= "trusted-firmware-a"
# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx8m-generic-bsp ?= "33"
BOOTLOADER_SEEK:mx8mn-generic-bsp ?= "32"
BOOTLOADER_SEEK:mx8mp-generic-bsp ?= "32"
BOOTLOADER_SEEK:mx8x-generic-bsp ?= "32"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx8m-generic-bsp ?= "33"
BOOTLOADER_SEEK_EMMC:mx8mn-generic-bsp ?= "0"
BOOTLOADER_SEEK_EMMC:mx8mp-generic-bsp ?= "0"

UBOOT_ARCH = "arm64"
UBOOT_BINARY = "flash.bin"

# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx8mq-generic-bsp ?= "0"
EMMC_DEV:mx8mm-generic-bsp ?= "2"
EMMC_DEV:mx8mn-generic-bsp ?= "2"
EMMC_DEV:mx8mp-generic-bsp ?= "2"

# partup
IMAGE_CLASSES += "image-types-partup"
include partup-layout-config.inc

# Default images to build
IMAGE_FSTYPES = "tar.gz wic.xz wic.bmap partup"
IMAGE_FSTYPES:update = "partup"

MACHINE_FEATURES:use-nxp-bsp += "caam optee"
MACHINE_FEATURES:remove:mx8x-nxp-bsp = " optee"

QEMUVERSION = "8.2%"
