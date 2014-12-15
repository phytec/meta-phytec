#Taken from TI yocto BSP Layer Version 05-2014, original name:ti33xx.inc
SOC_FAMILY = "ti33x"
require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "cortexa8thf-neon"
require conf/machine/include/tune-cortexa8.inc
# List common SoC features
MACHINE_FEATURES = "kernel26 usbgadget usbhost vfat ext2 screen alsa ethernet sgx"

# Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
BAREBOX_DEFAULT_DEFCONFIG = "am335x_defconfig"
BAREBOX_IPL_DEFAULT_DEFCONFIG = "am335x_mlo_defconfig"
BAREBOX_IPL_BIN_DEPLOY = "MLO-${MACHINE}-${PV}-${PR}.img"
BAREBOX_IPL_BIN_SYMLINK = "MLO"
# all am335x need an mlo, whenever we need a bootloader
# we need to have a dependency to build the mlo too
# TODO: add virtual/prebootloader and dependency
DEPENDS_pn-barebox_append = " barebox-ipl"
BAREBOX_BOOTARGS_BASE_EXTRA = "${@bb.utils.contains("MACHINE_FEATURES", "screen", "consoleblank=0", "",d)}"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-ti"
KERNEL_IMAGETYPE ?= "zImage"
KERNEL_DEFAULT_DEFCONFIG = "am335x_phytec_defconfig"
# install modules in initramfs
PACKAGE_INSTALL_append = " kernel-modules"
# install modules into rootfs
IMAGE_INSTALL_append = " kernel-modules"
SERIAL_CONSOLES ?= "115200;ttyO0"
# dont install kernelimage to rootfs
RDEPENDS_kernel-base = ""
# dont create legacy tarball of modules
MODULE_TARBALL_DEPLOY = "0"
# powermanagement firmware for the on-die Cortex M3
# the newer version makes a lot of problems dont include
# per default
# RDEPENDS_kernel-modules_append = " am33x-cm3"
# Support for the crypto hardware acceleration. In some cases it
# could slow down things, e.g. comparing raw scp speeds to the openssl neon
# implementation, but it will reduce cpu load and power consumption.
KERNEL_MODULE_AUTOLOAD += "cryptodev"
IMAGE_INSTALL_append = " cryptodev-module"

# rootfs images
# register image class for usage
IMAGE_CLASSES += "sdcard_image-phy"
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
