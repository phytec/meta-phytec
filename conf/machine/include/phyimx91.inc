require phytec-machine-common.inc

# Common settings for PHYTEC's i.MX91 boards
require conf/machine/include/arm/armv8-2a/tune-cortexa55.inc

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec-imx"

# Don't install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:append:mx91-nxp-bsp = " wic-bootenv-helper"
WKS_FILES:mx91-generic-bsp ?= "imx9-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "64"
WIC_CREATE_EXTRA_ARGS:remove = "--no-fstab-update"

# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx91-generic-bsp ?= "32"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx91-generic-bsp ?= "0"
# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx91-generic-bsp ?= "0"

EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE} bootenv.txt"
IMAGE_BOOT_FILES:secureboot:mx91-nxp-bsp = "fitImage bootenv.txt"

UBOOT_LOADADDRESS = "0x88000000"
UBOOT_ENTRYPOINT = "0x88000000"
UBOOT_DTB_LOADADDRESS = "0x8A380000"
UBOOT_DTBO_LOADADDRESS = "0x8A440000"

INITRAMFS_MAXSIZE = "262144"

# partup
IMAGE_CLASSES += "image-types-partup"
include partup-layout-config.inc

IMAGE_FSTYPES = "tar.gz wic.bmap wic.xz partup"
IMAGE_FSTYPES:update = "partup"

# rootfs alignment required for RAUC adaptive update
IMAGE_ROOTFS_ALIGNMENT:update = "4"

MACHINE_FEATURES += "optee wifi bluetooth"

QEMUVERSION = "8.2%"
