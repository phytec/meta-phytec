# Common settings for PHYTEC's i.MX8M boards
include conf/machine/include/phyimx8.inc
require conf/machine/include/arm/armv8a/tune-cortexa53.inc

MACHINE_FEATURES += "emmc pci wifi bluetooth"
MACHINE_FEATURES:remove = "nxp8987"

DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

IMAGE_BOOT_FILES = "fitImage boot.scr.uimg"
IMAGE_BOOT_FILES:mx8m-nxp-bsp = "fitImage bootenv.txt boot.scr.uimg"
IMAGE_BOOT_FILES:mx8m-nxp-bsp:secureboot = "fitImage bootenv.txt"
KERNEL_CLASSES += "kernel-fit-extra-artifacts"
IMAGE_INSTALL:append = "linux-yocto-fitimage"

# fdt_addr_r
UBOOT_DTB_LOADADDRESS ?= "0x40480000"
# fdtoverlay_addr_r
UBOOT_DTBO_LOADADDRESS ?= "0x404a0000"
# kernel_addr_r
UBOOT_LOADADDRESS ?= "0x40a00000"

ATF_BOOT_UART_BASE = "auto"
TFA_BUILD_TARGET = "bl31"
TFA_PLATFORM = "${ATF_PLATFORM}"

INITRAMFS_MAXSIZE = "262144"
