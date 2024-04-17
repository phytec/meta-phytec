require phytec-machine-common.inc

# Common settings for PHYTEC's i.MX8 boards

PREFERRED_PROVIDER_virtual/bootloader:mx8-nxp-bsp ?= "u-boot-imx"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec"
PREFERRED_PROVIDER_virtual/kernel:mx8-nxp-bsp ?= "${@bb.utils.contains('DISTRO_FEATURES', 'preempt-rt', 'linux-imx-rt', 'linux-imx',d)}"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec"
PREFERRED_PROVIDER_imx-boot="imx-boot-phytec"
PREFERRED_PROVIDER_opencl-headers:imxgpu = "imx-gpu-viv"
PREFERRED_PROVIDER_linux-imx-mfgtool:mx8-nxp-bsp = "linux-imx-mfgtool"
PREFERRED_PROVIDER_virtual/libgles3:mx8-nxp-bsp = "imx-gpu-viv"
PREFERRED_PROVIDER_virtual/libgles3:mx8mm-nxp-bsp = "mesa"

# dont install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:append:mx8-nxp-bsp = " wic-imx8-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE}"
IMAGE_BOOT_FILES:mx8m-nxp-bsp += "bootenv.txt"
IMAGE_BOOT_FILES:secureboot:mx8m-nxp-bsp = "fitImage bootenv.txt"
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

# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx8mq-generic-bsp ?= "0"
EMMC_DEV:mx8mm-generic-bsp ?= "2"
EMMC_DEV:mx8mn-generic-bsp ?= "2"
EMMC_DEV:mx8mp-generic-bsp ?= "2"

# rootfs alignment required for RAUC adaptive update
IMAGE_ROOTFS_ALIGNMENT:update = "4"

# partup
IMAGE_CLASSES += "image-types-partup"
include partup-layout-config.inc

# Default images to build
IMAGE_FSTYPES = "tar.gz wic wic.bmap partup"
IMAGE_FSTYPES:remove:update = "wic wic.bmap"
IMAGE_FSTYPES:append:mx8x-nxp-bsp = " wic.xz"
IMAGE_FSTYPES:remove:mx8x-nxp-bsp = " wic"

MACHINE_FEATURES += "caam optee"
MACHINE_FEATURES:remove:mx8x-nxp-bsp = " optee"
MACHINE_FEATURES:remove:mx8m-mainline-bsp = "optee"

OPTEEMACHINE:mx8mm-generic-bsp ?= "imx-mx8mmevk"
OPTEEMACHINE:mx8mn-generic-bsp ?= "imx-mx8mnevk"
OPTEEMACHINE:mx8mp-generic-bsp ?= "imx-mx8mpevk"

PREFERRED_VERSION_optee-os:mx8-nxp-bsp ?= "4.1.0"
PREFERRED_VERSION_optee-client:mx8-nxp-bsp ?= "4.1.0"
PREFERRED_VERSION_optee-test:mx8-nxp-bsp ?= "4.1.0"
