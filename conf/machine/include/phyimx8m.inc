# Common settings for PHYTEC's i.MX8M boards
include conf/machine/include/phyimx8.inc
require conf/machine/include/arm/armv8a/tune-cortexa53.inc

MACHINE_FEATURES += "emmc pci wifi bluetooth"
MACHINE_FEATURES:remove = "nxp8987"

DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

IMAGE_BOOT_FILES += "fitImage bootenv.txt boot.scr.uimg"
IMAGE_BOOT_FILES:secureboot = "fitImage bootenv.txt boot.scr.uimg"

UBOOT_DTB_LOADADDRESS = "0x48000000"
UBOOT_DTBO_LOADADDRESS = "0x49000000"

KERNEL_CLASSES += "kernel-fitimage"
KERNEL_IMAGETYPES += "fitImage"

ATF_BOOT_UART_BASE = "auto"
