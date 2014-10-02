#Taken from TI yocto BSP Layer Version 05-2014, original name:ti33xx.inc
SOC_FAMILY = "ti33x"
require conf/machine/include/soc-family.inc
require conf/machine/include/tune-cortexa8.inc
# List common SoC features
MACHINE_FEATURES = "kernel26 apm usbgadget usbhost vfat ext2 screen alsa ethernet"

# Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
BAREBOX_DEFAULT_DEFCONFIG = "am335x_defconfig"
BAREBOX_IPL_DEFAULT_DEFCONFIG = "am335x_mlo_defconfig"
BAREBOX_IPL_BIN_DEPLOY = "MLO-${MACHINE}-${PV}-${PR}.img"
BAREBOX_IPL_BIN_SYMLINK = "MLO"
# all am335x need an mlo, whenver we need a bootloader
# we need to have a dependency to build the mlo too
DEPENDS_virtual/bootloader_append = " barebox-ipl"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-ti"
KERNEL_IMAGETYPE ?= "zImage"
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

# rootfs images
# register image class for usage
IMAGE_CLASSES += "sdcard_image-phy"
# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "rootfs"

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
#directfb is activated per default
DISTRO_FEATURES_append = " directfb"
