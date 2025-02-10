# TI K3 Armv7 profile for Cortex-R5F cores

SOC_FAMILY = "ti-soc"
require conf/machine/include/soc-family.inc
SOC_FAMILY:append = ":k3r5"

# The closest of existing tunes for Cortex-R5F
DEFAULTTUNE = "armv7athf"
require conf/machine/include/arm/armv7a/tune-cortexa8.inc

require phytec-machine-common.inc

# R5 u-boot
SPL_SUFFIX = "bin"
SPL_SYMLINK = "tiboot3.${SPL_SUFFIX}"
UBOOT_SUFFIX = "bin"
UBOOT_BINARY = "u-boot-r5spl.${UBOOT_SUFFIX}"
UBOOT_IMAGE = "u-boot-r5spl-${MAINMACHINE}-${PV}-${PR}.${UBOOT_SUFFIX}"
UBOOT_SYMLINK = "u-boot-r5spl-${MAINMACHINE}.${UBOOT_SUFFIX}"

MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-devicetree"

PREFERRED_PROVIDER_virtual/kernel ??= "linux-dummy"
PREFERRED_PROVIDER_virtual/bootloader ??= "u-boot-phytec-ti"
PREFERRED_PROVIDER_u-boot ??= "u-boot-phytec-ti"
