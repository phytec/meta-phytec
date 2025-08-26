require phytec-machine-common.inc

#Taken from meta-rockchip Layer, original name:rk3288.inc
DEFAULTTUNE ?= "cortexa17thf-neon-vfpv4"

require conf/machine/include/arm/armv7a/tune-cortexa17.inc
require conf/machine/include/soc-family.inc

SOC_FAMILY = "rk3288"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-mainline"
KERNEL_IMAGETYPE ?= "zImage"

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot"
SPL_BINARY = "spl/u-boot-spl-dtb.bin"

# Install modules in initramfs and rootfs
MACHINE_EXTRA_RRECOMMENDS += "kernel-modules"

SERIAL_CONSOLES = "115200;ttyS2"

#WIC
IMAGE_CLASSES += "wic-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES = "zImage;linuximage oftree ${KERNEL_DEVICETREE} ${EXTERNAL_KERNEL_DEVICETREE}"
WKS_FILES:rk3288 ?= "rk3288-sdimage.wks"
WKS_BOOTIMAGESIZE ?= "20"

# Device information used by RAUC and some of its dependencies
EMMC_DEV:rk3288 ?= "1"

# default images to build
IMAGE_FSTYPES ?= "tar.gz wic"
IMAGE_FSTYPES:append:update = " ext4"
