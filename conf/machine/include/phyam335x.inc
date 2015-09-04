#Taken from TI yocto BSP Layer Version 05-2014, original name:ti33xx.inc
SOC_FAMILY = "ti33x"
require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "cortexa8thf-neon"
require conf/machine/include/tune-cortexa8.inc
# List common SoC features
MACHINE_FEATURES = "kernel26 usbgadget usbhost vfat ext2 screen alsa ethernet sgx can"

# Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
BAREBOX_DEFAULT_DEFCONFIG = "am335x_defconfig"
BAREBOX_IPL_DEFAULT_DEFCONFIG = "am335x_mlo_defconfig"
BAREBOX_IPL_IMAGE_BASE_NAME = "MLO-${MACHINE}-${PKGV}-${PKGR}-${DATETIME}"
BAREBOX_IPL_BIN_SYMLINK = "MLO"
# all am335x need an mlo, whenever we need a bootloader
# we need to have a dependency to build the mlo too
# TODO: add virtual/prebootloader and dependency
DEPENDS_pn-barebox_append = " barebox-ipl"
# image creation needs this dependency and barebox-ipl cannot take care of it
# by itself, so a
IMAGE_DEPENDS_sdcard_append = " barebox-ipl:do_deploy"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-ti"
KERNEL_IMAGETYPE ?= "zImage"
KBUILD_DEFCONFIG = "am335x_phytec_defconfig"
KCONFIG_MODE = "--alldefconfig"
# Install modules in initramfs and rootfs
PACKAGE_INSTALL_append = " kernel-modules"

SERIAL_CONSOLES ?= "115200;ttyO0"
# dont install kernelimage to rootfs
RDEPENDS_kernel-base = ""
# dont create legacy tarball of modules
MODULE_TARBALL_DEPLOY = "0"
# powermanagement firmware for the on-die Cortex M3
# the newer version makes a lot of problems dont include
# per default
# RDEPENDS_kernel-modules_append = " am33x-cm3"
# SGX GPL Kernel Modules
IMAGE_INSTALL_append = " omap3-sgx-modules"

# rootfs images
# register image class for usage
IMAGE_CLASSES += "am335x_sd_image-phy"
# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 129024 -c 7943"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 512"

# xserver
PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
# For built-in LCD, add xf86-input-tslib
XSERVER = "xserver-xorg \
           xf86-input-evdev \
           xf86-input-mouse \
           xf86-video-fbdev \
           xf86-input-keyboard"
# Default to external video, change to smallscreen for built-in LCD
GUI_MACHINE_CLASS = "smallscreen"

# OpenGLES support TI SDK 5xx, doesnt supports x11 nor wayland as of now
PREFERRED_PROVIDER_virtual/libgles1 = "libgles-omap3"
PREFERRED_PROVIDER_virtual/libgles2 = "libgles-omap3"
PREFERRED_PROVIDER_virtual/egl = "libgles-omap3"
