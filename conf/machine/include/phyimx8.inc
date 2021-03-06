# Common settings for PHYTEC's i.MX8 boards

PREFERRED_PROVIDER_virtual/bootloader= "u-boot-imx"
PREFERRED_PROVIDER_imx-boot="imx-boot-phytec"
PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_PROVIDER_opencl-headers_imx = "imx-gpu-viv"

MACHINE_FEATURES_remove = "nxp8987"

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES += "wic-imx8-helper"
IMAGE_BOOTFILES_DEPENDS ?= ""
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE}"
WKS_FILES_mx8 ?= "imx8-sdimage.wks"
WKS_FILES_mx8m ?= "imx8m-sdimage.wks"
WKS_FILES_mx8mn ?= "imx8mn-sdimage.wks"
WKS_FILES_mx8mp ?= "imx8mn-sdimage.wks"

# eMMC boot partition bootloader image offset
IMX_BOOT_SEEK_EMMC_mx8m ?= "33"
IMX_BOOT_SEEK_EMMC_mx8mn ?= "0"
IMX_BOOT_SEEK_EMMC_mx8mp ?= "0"

# Default images to build
IMAGE_FSTYPES = "tar.gz wic"
