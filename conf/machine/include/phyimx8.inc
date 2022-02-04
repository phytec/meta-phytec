# Common settings for PHYTEC's i.MX8 boards

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-imx"
PREFERRED_PROVIDER_imx-boot="imx-boot-phytec"
PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_PROVIDER_opencl-headers_imxgpu = "imx-gpu-viv"

MACHINE_FEATURES_remove = "nxp8987"

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES += "wic-imx8-helper"
IMAGE_BOOTFILES_DEPENDS ?= "phytec-bootenv:do_deploy"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE} bootenv.txt"
IMAGE_BOOT_FILES_secureboot_mx8m = "fitImage bootenv.txt"
WKS_FILES_mx8m ?= "imx8m-sdimage.wks"
WKS_FILES_mx8m_update ?= "imx8m-rauc-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "50"
WIC_CREATE_EXTRA_ARGS_remove = "--no-fstab-update"

# MMC user area bootloader image offset
BOOTLOADER_SEEK_mx8m ?= "33"
BOOTLOADER_SEEK_mx8mn ?= "32"
BOOTLOADER_SEEK_mx8mp ?= "32"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC_mx8m ?= "33"
BOOTLOADER_SEEK_EMMC_mx8mn ?= "0"
BOOTLOADER_SEEK_EMMC_mx8mp ?= "0"

# Device information used by RAUC and some of its dependencies
EMMC_DEV_mx8mq ?= "0"
EMMC_DEV_mx8mm ?= "2"
EMMC_DEV_mx8mn ?= "2"
EMMC_DEV_mx8mp ?= "2"

# Default images to build
IMAGE_FSTYPES = "tar.gz wic"

MACHINE_FEATURES += " caam"
