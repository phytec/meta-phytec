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
BAREBOX_IPL_IMAGE_NAME = "MLO-${MACHINE}-${PKGV}-${PKGR}${IMAGE_VERSION_SUFFIX}"
BAREBOX_IPL_BIN_LINK_NAME = "MLO"

# Kernel 
PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
PREFERRED_PROVIDER_amx3-cm3 ?= "amx3-cm3-phy"
KERNEL_IMAGETYPE ?= "zImage"
# Install modules in initramfs and rootfs
PACKAGE_INSTALL_append = " kernel-modules"
KERNEL_MODULE_AUTOLOAD += " cryptodev"

SERIAL_CONSOLES ?= "115200;ttyO0"
# dont install kernelimage to rootfs
RDEPENDS_${KERNEL_PACKAGE_NAME}-base = ""

# rootfs images
# Use the expected value of the ubifs filesystem's volume name in the kernel
UBI_VOLNAME = "root"
# The biggest NANDs on current modules are 1GB. With current partition layout,
# this sums up to 7943 LEBs available for the ubifs
MKUBIFS_ARGS = "-F -m 2048 -e 129024 -c 7943"
UBINIZE_ARGS = "-m 2048 -p 128KiB -s 512"

# xserver
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
# For built-in LCD, add xf86-input-tslib
XSERVER = "xserver-xorg \
           xf86-input-evdev \
           xf86-input-mouse \
           xf86-video-fbdev \
           xf86-input-keyboard"
# Default to external video, change to smallscreen for built-in LCD
GUI_MACHINE_CLASS = "smallscreen"

# OpenGLES support for AM335x SGX Core
PREFERRED_PROVIDER_virtual/libgles1 ?= "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgles2 ?= "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/egl ?= "ti-sgx-ddk-um"

QT_QPA_DEFAULT_PLATFORM ?= "${@bb.utils.contains('MACHINE_FEATURES', 'sgx', 'eglfs', 'linuxfb', d)}"

#WIC
IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES += "wic-am335x-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES = "barebox.bin MLO zImage oftree ${KERNEL_DEVICETREE} ${EXTERNAL_KERNEL_DEVICETREE}"
#copy ubifs image to sdcard
IMAGE_BOOT_FILES += "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.ubifs;${IMAGE_LINK_NAME}.ubifs"
IMAGE_TYPEDEP_wic ??= "ubifs"
do_image_wic[depends] += "\
    virtual/prebootloader:do_deploy \
"
WKS_FILES_ti33x ?= "am335x-sdimage.wks"
WKS_FILES_ti33x_update ?= "am335x-rauc-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "20"

# Device information used by RAUC and some of its dependencies
EMMC_DEV_ti33x ?= "1"
NAND_DEV_ti33x ?= "0"

# default images to build
IMAGE_FSTYPES ?= "tar.gz wic ubifs ext4"
