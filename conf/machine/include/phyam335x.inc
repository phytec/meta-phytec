#Taken from TI yocto BSP Layer Version 05-2014, original name:ti33xx.inc
SOC_FAMILY = "ti33x"
require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "cortexa8thf-neon"
require conf/machine/include/tune-cortexa8.inc
# List common SoC features
MACHINE_FEATURES = "kernel26 usbgadget usbhost vfat ext2 screen alsa ethernet can"

# Sub-architecture support adapted fom fsl-arm
MACHINE_SOCARCH = "${TUNE_PKGARCH}-ti33x"
PACKAGE_EXTRA_ARCHS_append = " ${MACHINE_SOCARCH}"

# Bootloader
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"
PREFERRED_PROVIDER_virtual/prebootloader ?= "barebox-ipl"
INTREE_DEFCONFIG_pn-barebox = "am335x_defconfig"
INTREE_DEFCONFIG_pn-barebox-ipl = "am335x_mlo_defconfig"
BAREBOX_IPL_IMAGE_BASE_NAME = "MLO-${MACHINE}-${PKGV}-${PKGR}-${DATETIME}"
BAREBOX_IPL_BIN_SYMLINK = "MLO"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-ti"
PREFERRED_PROVIDER_amx3-cm3 ?= "amx3-cm3-phy"
KERNEL_IMAGETYPE ?= "zImage"
INTREE_DEFCONFIG_pn-linux-ti = "am335x_phytec_defconfig"
# Install modules in initramfs and rootfs
PACKAGE_INSTALL_append = " kernel-modules"

SERIAL_CONSOLES ?= "115200;ttyO0"
# dont install kernelimage to rootfs
RDEPENDS_kernel-base = ""

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

# OpenGLES support for AM335x SGX Core
PREFERRED_PROVIDER_virtual/libgles1 = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgles2 = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/egl = "ti-sgx-ddk-um"

QT_QPA_DEFAULT_PLATFORM ?= "${@bb.utils.contains('MACHINE_FEATURES', 'sgx', 'eglfs', 'linuxfb', d)}"
