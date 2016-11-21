# Common settings for phytecs imx6 boards

include conf/machine/include/soc-family.inc

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

# dont install kernelimage to rootfs
RDEPENDS_kernel-base = ""

# XServer
PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
# We don't support a Xserver yet.
#XSERVER_DRIVER = "xf86-video-fbdev"
#XSERVER_DRIVER_mx6 = "xf86-video-imxfb-vivante"
#XSERVER = "xserver-xorg \
#           xf86-input-evdev \
#           ${XSERVER_DRIVER}"

# Ship kernel modules
MACHINE_EXTRA_RRECOMMENDS = "kernel-modules"

# sd-card image
IMAGE_CLASSES += "image_types_fsl"

SERIAL_CONSOLE = "115200 ttymxc0"

KERNEL_IMAGETYPE = "zImage"

MACHINE_FEATURES = "apm usbgadget usbhost vfat"
