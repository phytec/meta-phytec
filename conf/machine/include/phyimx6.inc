# Common settings for phytecs imx6 boards

include conf/machine/include/imx-base.inc
include conf/machine/include/tune-cortexa9.inc

SOC_FAMILY = "mx6"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
IMAGE_BOOTLOADER ?= "barebox"
BAREBOX_DEFAULT_DEFCONFIG = "imx_v7_defconfig"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"
# Install modules in initramfs and roofs
PACKAGE_INSTALL_append = " kernel-modules"

SERIAL_CONSOLES ?= "115200;ttymxc3"
# dont install kernelimage to rootfs
RDEPENDS_kernel-base = ""
# dont create legacy tarball of modules
MODULE_TARBALL_DEPLOY = "0"

# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 8083"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"
