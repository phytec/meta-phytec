# Common settings for PHYTEC's i.MX7 boards

PREFERRED_PROVIDER_virtual/kernel = "linux-imx"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot-imx"
PREFERRED_PROVIDER_u-boot = "u-boot-imx"
PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"

# UBIFS
UBI_VOLNAME = "root"
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 1912"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

# WIC
IMAGE_BOOT_FILES = "zImage ${KERNEL_DEVICETREE}"
WKS_FILES_mx7 ?= "imx7-sdimage.wks"

# MMC user area bootloader image offset
BOOTLOADER_SEEK_mx7 ?= "1"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC_mx7 ?= "1"

# Default images to build
IMAGE_FSTYPES = "tar.xz wic ubifs"
