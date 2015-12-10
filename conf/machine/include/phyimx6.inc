# Common settings for phytecs imx6 boards

include conf/machine/include/imx-base.inc
include conf/machine/include/tune-cortexa9.inc

SOC_FAMILY = "mx6"

#Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
INTREE_DEFCONFIG_pn-barebox = "imx_v7_defconfig"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"
INTREE_DEFCONFIG_pn-linux-mainline = "imx_v6_v7_defconfig"
# Install modules in initramfs and roofs
PACKAGE_INSTALL_append = " kernel-modules"

SERIAL_CONSOLES ?= "115200;ttymxc3"
# dont install kernelimage to rootfs
RDEPENDS_kernel-base = ""
# dont create legacy tarball of modules
MODULE_TARBALL_DEPLOY = "0"

# Avoid pulling package 'gstreamer1.0-plugins-bad-meta' into the image
# 'core-image-sato', because the recipe gstreamer1.0-plugins-bad depends on
# virtual/libgles2 which pulls in gpu-viv-bin-mx6q which conflicts with mesa
# which is pulled in by xorg-xserver. Puh!
RRECOMMENDS_gst-player-bin_remove_pn-gst-player = "gstreamer1.0-plugins-bad-meta"

# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 126976 -c 8083"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 2048 -O 2048"

# Firmware files
MACHINE_EXTRA_RRECOMMENDS_append_mx6q = " firmware-imx-vpu-mx6q"
MACHINE_EXTRA_RRECOMMENDS_append_mx6dl = " firmware-imx-vpu-mx6dl"
