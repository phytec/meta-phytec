# Common settings for PHYTEC's i.MX8 boards

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-imx"
PREFERRED_PROVIDER_imx-boot="imx-boot-phytec"
PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_PROVIDER_opencl-headers:imxgpu = "imx-gpu-viv"

MACHINE_FEATURES:remove = "nxp8987"

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:mx8m-nxp-bsp += "wic-imx8-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE}"
IMAGE_BOOT_FILES:secureboot:mx8m-nxp-bsp = "fitImage bootenv.txt"
WKS_FILES:mx8m-nxp-bsp ?= "imx8m-sdimage.wks"
WKS_FILES:mx8m-mainline-bsp ?= "imx8m-binman-sdimage.wks"
WKS_FILES:mx8m-nxp-bsp_update ?= "imx8m-rauc-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "50"

# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx8m-generic-bsp ?= "33"
BOOTLOADER_SEEK:mx8mn-generic-bsp ?= "32"
BOOTLOADER_SEEK:mx8mp-generic-bsp ?= "32"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx8m-generic-bsp ?= "33"
BOOTLOADER_SEEK_EMMC:mx8mn-generic-bsp ?= "0"
BOOTLOADER_SEEK_EMMC:mx8mp-generic-bsp ?= "0"

# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx8mq-generic-bsp ?= "0"
EMMC_DEV:mx8mm-generic-bsp ?= "2"
EMMC_DEV:mx8mn-generic-bsp ?= "2"
EMMC_DEV:mx8mp-generic-bsp ?= "2"

# Default images to build
IMAGE_FSTYPES = "tar.gz wic"

MACHINE_FEATURES += " caam"
