require phytec-machine-common.inc

# Common settings for PHYTEC's i.MX93 boards
require conf/machine/include/arm/armv8-2a/tune-cortexa55.inc

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-imx"

# Don't install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:append:mx93-nxp-bsp = " wic-bootenv-helper"
WKS_FILES:mx93-generic-bsp ?= "imx93-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "64"
WIC_CREATE_EXTRA_ARGS:remove = "--no-fstab-update"

# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx93-generic-bsp ?= "32"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx93-generic-bsp ?= "0"
# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx93-generic-bsp ?= "0"

EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE} bootenv.txt"

# partup
IMAGE_CLASSES += "image-types-partup"
include partup-layout-config.inc

IMAGE_FSTYPES = "tar.gz wic.bmap wic.xz partup"
IMAGE_FSTYPES:append:update = " ext4"

# rootfs alignment required for RAUC adaptive update
IMAGE_ROOTFS_ALIGNMENT:update = "4"

MACHINE_FEATURES += "caam optee wifi"

# Remove unused NXP WiFi features
MACHINE_FEATURES:remove:use-nxp-bsp = " \
    nxp8801-sdio \
    nxp8987-sdio \
    nxp8997-pcie \
    nxp8997-sdio \
    nxp9098-pcie \
    nxp9098-sdio \
    nxpiw416-sdio \
    nxpiw612-sdio \
"
