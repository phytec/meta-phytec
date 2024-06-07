require phytec-machine-common.inc

require conf/machine/include/am57xx.inc

# Sub-architecture support
MACHINE_SOCARCH = "${TUNE_PKGARCH}-am57xx"
PACKAGE_EXTRA_ARCHS:append = " ${MACHINE_SOCARCH}"

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
# Use standard binutils and gcc and not TI's packages
PREFERRED_RPROVIDER_binutils-cross-canadian-arm ?= "binutils-cross-canadian-arm"
PREFERRED_RPROVIDER_gcc-cross-canadian-arm ?= "gcc-cross-canadian-arm"
# Use standard wpa-supplicant and not the tools provide by wl18xx
PREFERRED_RPROVIDER_wpa-supplicant ?= "wpa-supplicant"
PREFERRED_RPROVIDER_wpa-supplicant-cli ?= "wpa-supplicant"
PREFERRED_RPROVIDER_wpa-supplicant-passphrase ?= "wpa-supplicant"

# Do not install uImage
KERNEL_IMAGETYPES:remove = "uImage"

# Do not build extlinux
UBOOT_EXTLINUX = "0"

# MLO and u-boot.img
IMAGE_BOOT_FILES = "${SPL_BINARY} u-boot.${UBOOT_SUFFIX}"

# WIC
WKS_FILES:am57xx ?= "am57xx-sdimage.wks"

# AM57x has SGX GPU core
PREFERRED_PROVIDER_virtual/gpudriver ?= "ti-sgx-ddk-km"

# Hardware cryptography
KERNEL_MODULE_AUTOLOAD += "cryptodev"

# Vivante GC320 2D accelerator
MACHINE_EXTRA_RRECOMMENDS:append:am57xx = " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'gc320', \
    'ti-gc320-driver ti-gc320-libs-dev', '', d)} \
"

# Set the list of device targets for ti-pdk class recipes
TI_PDK_LIMIT_SOCS = "am571x am572x am574x"
TI_PDK_LIMIT_BOARDS = "evmAM571x evmAM572x idkAM571x idkAM572x idkAM574x"

# Set phytec-eeprom-flashtool to 2.00.01 (APIv1)
PREFERRED_VERSION_phytec-eeprom-flashtool = "2.00.01"
