require phytec-machine-common.inc

# Common settings for PHYTEC's i.MX7 boards

PREFERRED_PROVIDER_virtual/kernel = "linux-imx"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot-phytec-imx"
PREFERRED_PROVIDER_u-boot = "u-boot-phytec-imx"

# UBIFS
UBI_VOLNAME = "root"
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 1912"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

# WIC
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:append:mx7-nxp-bsp = " wic-bootenv-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE} bootenv.txt"
WKS_FILES:mx7-nxp-bsp ?= "imx7-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "20"

# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx7-generic-bsp ?= "1"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx7-generic-bsp ?= "1"

# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx7-generic-bsp ?= "2"

# partup
IMAGE_CLASSES += "image-types-partup"
include partup-layout-config.inc

# Default images to build
IMAGE_FSTYPES = " \
    tar.xz \
    wic \
    ${@bb.utils.contains("MACHINE_FEATURES", "nand", "ubifs", "", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "emmc", "partup", "", d)} \
"
IMAGE_FSTYPES:update = "partup"
