# Common settings for phytecs imx6 boards

include conf/machine/include/imx-base.inc
include conf/machine/include/tune-cortexa9.inc

SOC_FAMILY = "mx6:mx6q"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
IMAGE_BOOTLOADER ?= "barebox"
BAREBOX_DEFAULT_DEFCONFIG = "freescale-mx6-arm2_defconfig"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"
# install modules in initramfs
PACKAGE_INSTALL_append = " kernel-modules"
# install modules into rootfs
IMAGE_INSTALL_append = " kernel-modules"
SERIAL_CONSOLES ?= "115200;ttymxc3"
# dont install kernelimage to rootfs
RDEPENDS_kernel-base = ""
# dont create legacy tarball of modules
MODULE_TARBALL_DEPLOY = "0"

MACHINE_FEATURES += "pci"
