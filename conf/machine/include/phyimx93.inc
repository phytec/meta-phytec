require phytec-machine-common.inc

# Common settings for PHYTEC's i.MX93 boards
require conf/machine/include/arm/armv8-2a/tune-cortexa55.inc

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec-imx"
PREFERRED_PROVIDER_virtual/bootloader:use-mainline-bsp ?= "u-boot-master"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec-imx"
PREFERRED_PROVIDER_virtual/kernel:use-mainline-bsp ?= "linux-yocto-dev"

# Don't install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# Wic
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES:append:mx93-nxp-bsp = " wic-bootenv-helper"
WKS_FILES:mx93-generic-bsp ?= "imx9-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "64"

# MMC user area bootloader image offset
BOOTLOADER_SEEK:mx93-generic-bsp ?= "32"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC:mx93-generic-bsp ?= "0"
# Device information used by RAUC and some of its dependencies
EMMC_DEV:mx93-generic-bsp ?= "0"

IMAGE_BOOT_FILES = "fitImage bootenv.txt boot.scr.uimg"
IMAGE_BOOT_FILES:secureboot:mx93-nxp-bsp = "fitImage bootenv.txt"
IMAGE_BOOT_FILES:remove:use-mainline-bsp = "bootenv.txt"

UBOOT_ARCH = "arm64"
UBOOT_BINARY:use-mainline-bsp = "flash.bin"
UBOOT_LOADADDRESS = "0x88000000"
UBOOT_ENTRYPOINT = "0x88000000"
UBOOT_DTB_LOADADDRESS = "0x8A380000"
UBOOT_DTBO_LOADADDRESS = "0x8A440000"
UBOOT_PROVIDES_BOOT_CONTAINER:use-mainline-bsp = "1"

# TFA
IMX_DEFAULT_ATF_PROVIDER:use-mainline-bsp ?= "trusted-firmware-a"
TFA_BUILD_TARGET = "bl31"
TFA_PLATFORM = "${ATF_PLATFORM}"

INITRAMFS_MAXSIZE = "262144"

# M33 core demos (bootaux)
WKS_FILE_DEPENDS:append:use-nxp-bsp = " imx-m33-demos"
IMX_M33_DEMO_INSTALL ??= " \
    imx93-11x11-evk_m33_TCM_rpmsg_lite_str_echo_rtos.bin \
    imx93-11x11-evk_m33_TCM_rpmsg_lite_pingpong_rtos_linux_remote.bin \
    imx93-11x11-evk_m33_TCM_power_mode_switch.bin \
"
IMAGE_BOOT_FILES:append:use-nxp-bsp = "${IMX_M33_DEMO_INSTALL}"

# partup
IMAGE_CLASSES += "image-types-partup"
include partup-layout-config.inc

IMAGE_FSTYPES = "tar.gz wic.bmap wic.xz partup"
IMAGE_FSTYPES:update = "partup"

MACHINE_FEATURES += "optee wifi bluetooth"
MACHINE_FEATURES:remove:use-mainline-bsp = " optee"

QEMUVERSION = "8.2%"
