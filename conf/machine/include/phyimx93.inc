# Common settings for PHYTEC's i.MX93 boards
require conf/machine/include/arm/armv8-2a/tune-cortexa55.inc

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-imx"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-imx"

# Don't install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:append:mx93-nxp-bsp = " wic-imx93-helper"
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

# M33 core demos (bootaux)
WKS_FILES:append:mx93-nxp-bsp = " imx-m33-demos"
IMX_M33_DEMO_EXT ??= "bin"
IMX_M33_DEMO_INSTALL:mx93-nxp-bsp ??= " \
    imx93-11x11-evk_m33_TCM_rpmsg_lite_str_echo_rtos.${IMX_M33_DEMO_EXT} \
    imx93-11x11-evk_m33_TCM_rpmsg_lite_pingpong_rtos_linux_remote.${IMX_M33_DEMO_EXT} \
    imx93-11x11-evk_m33_TCM_power_mode_switch.${IMX_M33_DEMO_EXT} \
"
IMAGE_BOOT_FILES:append = "${IMX_M33_DEMO_INSTALL}"

# partup
IMAGE_CLASSES:append:partup-package = " image-types-partup"
include partup-layout-config.inc
PARTUP_IMAGE_FSTYPE = ""
PARTUP_IMAGE_FSTYPE:partup-package = "partup"

IMAGE_FSTYPES = "tar.gz wic.bmap wic.xz ${PARTUP_IMAGE_FSTYPE}"
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
