require conf/machine/include/dra7xx.inc

SOC_FAMILY_append = ":am57xx"

# Sub-architecture support
MACHINE_SOCARCH = "${TUNE_PKGARCH}-am57xx"
PACKAGE_EXTRA_ARCHS_append = " ${MACHINE_SOCARCH}"

# Add machine features
MACHINE_FEATURES += "can touchscreen resistivetouch wifi bluetooth"

SERIAL_CONSOLES = "115200;ttyS2"

# Install modules in initramfs and roofs
PACKAGE_INSTALL_append = " kernel-modules"

# Install device-trees in rootfs
MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-devicetree"

# Bootloader and kernel from meta-phytec
PREFERRED_PROVIDER_virtual/bootloader_${MACHINE} = "u-boot-ti"
PREFERRED_PROVIDER_u-boot = "u-boot-ti"
PREFERRED_PROVIDER_virtual/kernel_${MACHINE} = "linux-ti"

# Do not install uImage
KERNEL_IMAGETYPES_remove = "uImage"

# Do not build extlinux
UBOOT_EXTLINUX = "0"

# MLO and u-boot.img
IMAGE_BOOT_FILES = "${SPL_BINARY} u-boot.${UBOOT_SUFFIX}"

# WIC
WKS_FILES_am57xx ?= "am57xx-sdimage.wks"

# AM57x has SGX GPU core
PREFERRED_PROVIDER_virtual/egl = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgles1 = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgles2 = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgbm = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/gpudriver = "ti-sgx-ddk-km"

PREFERRED_VERSION_ti-sgx-ddk-um = "1.17.4948957"
PREFERRED_VERSION_ti-sgx-ddk-km = "1.17.4948957"

QT_QPA_PLATFORM ?= "eglfs"

# Useful RRECOMMENDS
MACHINE_EXTRA_RRECOMMENDS_append_am57xx = " \
    omapconf \
"

# Set the list of device targets for ti-pdk class recipes
TI_PDK_LIMIT_SOCS = "am571x am572x am574x"
TI_PDK_LIMIT_BOARDS = "evmAM571x evmAM572x idkAM571x idkAM572x idkAM574x"
