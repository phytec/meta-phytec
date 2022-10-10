# Common settings for PHYTEC's i.MX8 boards

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'linux-imx-rt', 'linux-imx',d)}"
PREFERRED_PROVIDER_imx-boot="imx-boot-phytec"
PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_PROVIDER_opencl-headers:imxgpu = "imx-gpu-viv"
PREFERRED_RPROVIDER_mfgtool-base = "linux-imx-mfgtool"
PREFERRED_RPROVIDER_mfgtool-image = "linux-imx-mfgtool"
PREFERRED_RPROVIDER_mfgtool-image-image = "linux-imx-mfgtool"

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:append:mx8m-nxp-bsp = " wic-imx8-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE}"
IMAGE_BOOT_FILES:mx8m-nxp-bsp += "bootenv.txt"
IMAGE_BOOT_FILES:secureboot:mx8m-nxp-bsp = "fitImage"
WKS_FILES:mx8m-nxp-bsp ?= "imx8m-sdimage.wks"
WKS_FILES:mx8m-mainline-bsp ?= "imx8m-binman-sdimage.wks"
WKS_FILES:mx8m-nxp-bsp:update ?= "imx8m-rauc-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "50"
WIC_CREATE_EXTRA_ARGS:remove = "--no-fstab-update"

IMX_DEFAULT_ATF_PROVIDER:use-mainline-bsp ?= "trusted-firmware-a"
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
