# Common settings for PHYTEC's i.MX93 boards
require conf/machine/include/arm/armv8-2a/tune-cortexa55.inc

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-imx"

KERNEL_IMAGETYPE = "Image"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE} ${KERNEL_IMAGETYPE}"

IMAGE_FSTYPES = "tar.gz wic.bmap wic.xz"

MACHINE_FEATURES += "caam optee"
