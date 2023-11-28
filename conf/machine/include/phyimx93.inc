# Common settings for PHYTEC's i.MX93 boards
require conf/machine/include/arm/armv8-2a/tune-cortexa55.inc

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-imx"

# Don't install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

KERNEL_IMAGETYPE = "Image"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree ${EXTERNAL_KERNEL_DEVICETREE} ${KERNEL_IMAGETYPE}"

IMAGE_FSTYPES = "tar.gz wic.bmap wic.xz"

MACHINE_FEATURES += "caam optee"

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
