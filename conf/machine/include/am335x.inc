#Taken from TI yocto BSP Layer Version 05-2014, original name:ti33xx.inc
SOC_FAMILY = "am335x"

require conf/machine/include/soc-family.inc
require conf/machine/include/tune-cortexa8.inc

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"

# For built-in LCD, add xf86-input-tslib
XSERVER = "xserver-xorg \
           xf86-input-evdev \
           xf86-input-mouse \
           xf86-video-fbdev \
           xf86-input-keyboard"

# Default to external video, change to smallscreen for built-in LCD
GUI_MACHINE_CLASS = "bigscreen"

# Default providers, may need to override for specific machines
PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec"
PREFERRED_PROVIDER_virtual/bootloader ?= "barebox"

KERNEL_IMAGETYPE = "zImage"

#UBOOT_ARCH = "arm"
#UBOOT_MACHINE = "am335x_evm_config"

#UBOOT_ENTRYPOINT = "0x80008000"
#UBOOT_LOADADDRESS = "0x80008000"

# Use the expected value of the ubifs filesystem's volume name in the kernel
# and u-boot.
#UBI_VOLNAME = "rootfs"

EXTRA_IMAGEDEPENDS += "barebox"

# List common SoC features, may need to add touchscreen for specific machines
# MACHINE_FEATURES = "kernel26 apm usbgadget usbhost vfat ext2 screen alsa ethernet sgx"
