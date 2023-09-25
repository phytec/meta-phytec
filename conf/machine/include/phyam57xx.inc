require conf/machine/include/dra7xx.inc

SOC_FAMILY:append = ":am57xx"

# Add machine features
MACHINE_FEATURES += "can touchscreen resistivetouch wifi bluetooth"

SERIAL_CONSOLES = "115200;ttyS2"

# Install modules in initramfs and roofs
PACKAGE_INSTALL:append = " kernel-modules"

# Install device-trees in rootfs
MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-devicetree"

# Bootloader and kernel from meta-phytec
PREFERRED_PROVIDER_virtual/bootloader:${MACHINE} = "u-boot-ti"
PREFERRED_PROVIDER_u-boot = "u-boot-ti"
PREFERRED_PROVIDER_virtual/kernel:${MACHINE} = "linux-ti"

# Do not install uImage
KERNEL_IMAGETYPES:remove = "uImage"

# Do not build extlinux
UBOOT_EXTLINUX = "0"

# MLO and u-boot.img
IMAGE_BOOT_FILES = "${SPL_BINARY} u-boot.${UBOOT_SUFFIX}"

# AM57x has SGX GPU core
PREFERRED_PROVIDER_virtual/egl = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgles1 = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgles2 = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgbm = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/gpudriver = "ti-sgx-ddk-km"

PREFERRED_VERSION_ti-sgx-ddk-um = "1.17.4948957"
PREFERRED_VERSION_ti-sgx-ddk-km = "1.17.4948957"

QT_QPA_PLATFORM ?= "eglfs"

# Set the list of device targets for ti-pdk class recipes
TI_PDK_LIMIT_SOCS = "am571x am572x am574x"
TI_PDK_LIMIT_BOARDS = "evmAM571x evmAM572x idkAM571x idkAM572x idkAM574x"
