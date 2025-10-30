require phytec-machine-common.inc
require phystm32mp-openstlinux.inc

#@DESCRIPTION: Common Machine configuration for STM32MP boards

MACHINE_SOCARCH = "${TUNE_PKGARCH}"

# =========================================================================
# Machine settings
# =========================================================================
# Default machine feature
MACHINE_FEATURES = "usbhost usbgadget alsa screen ext2"
MACHINE_FEATURES += "optee"

# Enable firmware secure update feature when using RAUC distro feature
MACHINE_FEATURES:append:update = " fw-update"

# Default serial consoles (TTYs) to enable using getty
# Before kernel 4.18, serial console are ttyS3 but after is ttySTM0
SERIAL_CONSOLES = "115200;ttySTM0"

# Dont install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# Ship all kernel modules by default
MACHINE_EXTRA_RRECOMMENDS = " kernel-modules"

# Install edid firmware files for hdmi
MACHINE_EXTRA_RRECOMMENDS:append = " ${@bb.utils.contains("MACHINE_FEATURES", "hdmi", "linux-firmware-edid", "", d)}"

# =========================================================================
# PREFERRED_PROVIDER
# =========================================================================
PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec"
PREFERRED_PROVIDER_virtual/trusted-firmware-a ?= "tf-a-phytec"
PREFERRED_PROVIDER_virtual/optee-os ?= "optee-os"
VIRTUAL-RUNTIME_optee-os = "optee-os"

# Default FSTYPES requested
WKS_IMAGE_FSTYPES ?= ""
# Do note generate wic image for rauc
WKS_IMAGE_FSTYPES:update= ""
IMAGE_FSTYPES ?= "${WKS_IMAGE_FSTYPES} tar.xz ext4"

# =========================================================================
# Kernel
# =========================================================================

# Kernel image type
KERNEL_DEFAULT_NAME = "uImage"
KERNEL_DEFAULT_NAME:aarch64 = "Image.gz"

# arm32 bits
KERNEL_EFI_NAME = "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'zImage', 'uImage', d)}"
KERNEL_EFI_ALT_NAME = "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'uImage', 'zImage', d)}"
# arm64 bits
KERNEL_EFI_NAME:aarch64 = "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'Image', 'Image.gz', d)}"
KERNEL_EFI_ALT_NAME:aarch64 = "${@bb.utils.contains('MACHINE_FEATURES', 'efi', 'Image.gz', 'Image', d)}"

KERNEL_IMAGETYPE     = "${@bb.utils.contains('MACHINE_FEATURES', 'fit', 'fitImage', '${KERNEL_EFI_NAME}', d)}"
KERNEL_ALT_IMAGETYPE = " vmlinux "
KERNEL_ALT_IMAGETYPE =+ " ${@bb.utils.contains('MACHINE_FEATURES', 'fit', '${KERNEL_DEFAULT_NAME}', '${KERNEL_EFI_ALT_NAME}', d)} "

KERNEL_CLASSES = " ${@bb.utils.contains('MACHINE_FEATURES', 'fit', 'kernel-fitimage', 'kernel-uimage', d)} "

# For fit usage
UBOOT_ENTRYPOINT = "0xC0800000"

# Set LOADADDR
# Set this address to 0xC2000040, which is 0xC2000000 + 0x40.
# 0xC2000000 is the memory address where U-Boot will copy from flash the file uImage and 0x40 is uImage header size (64Bytes).
# With this value, U-Boot will be able to execute in place the zImage contained in uImage.
ST_KERNEL_LOADADDR ?= "0xC2000040"

# Extra kernel bootargs
EXTRA_KERNEL_ARGS ?= ""
# Use HDMI EDID firmware to set a specific resolution (when "hdmi_resolution" env var defined on u-boot)
# Example to set 1280x720: on u-boot, set the following var: hdmi_resolution=HDMI-A-1:edid/1280x720.bin
HDMI_EDID_KERNEL_ARGS = "drm.edid_firmware=${hdmi_resolution}"
EXTRA_KERNEL_ARGS += "${HDMI_EDID_KERNEL_ARGS}"
# bootargs to set the RAUC slot
EXTRA_KERNEL_ARGS:append:update = " rauc.slot=${bootslot}"

# List of device tree overlays to install for phyBOARD-Sargas
DT_OVERLAYS_INSTALL:phycore-stm32mp15 = "${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', '${PHY_EXPANSIONS_SARGAS}', '', d)}"
# Displays
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-peb-av02-lcd.dtbo"
PHY_EXPANSIONS_SARGAS += "${@bb.utils.contains('MACHINE_FEATURES', 'hdmi', 'phyboard-stm32mp1-peb-av01-hdmi.dtbo', '', d)}"
PHY_EXPANSIONS_SARGAS += "${@bb.utils.contains('MACHINE_FEATURES', 'mipi-dsi', 'phyboard-stm32mp1-dsi-lcd-mb1407.dtbo', '', d)}"
PHY_EXPANSIONS_SARGAS += "${@bb.utils.contains('MACHINE_FEATURES', 'mipi-dsi', 'phyboard-stm32mp1-dsi-rpi-official-display.dtbo', '', d)}"
# Configurations examples for the specific connectors
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-pi-hat-extension.dtbo"
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-uno-r3-extension.dtbo"
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-motor-control.dtbo"
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-motor-control-m4.dtbo"
# Wireless expansions
PHY_EXPANSIONS_SARGAS += "${@bb.utils.contains('MACHINE_FEATURES', 'wifi', 'phyboard-stm32mp1-peb-wlbt-05-wlan.dtbo', '', d)}"
PHY_EXPANSIONS_SARGAS += "${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'phyboard-stm32mp1-peb-wlbt-05-bluetooth-usart3.dtbo', '', d)}"
PHY_EXPANSIONS_SARGAS += "${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'phyboard-stm32mp1-peb-wlbt-05-bluetooth-usart1.dtbo', '', d)}"
PHY_EXPANSIONS_SARGAS += "${@bb.utils.contains_any('MACHINE_FEATURES', 'wifi bluetooth', 'phyboard-stm32mp1-pi-hat-redbear.dtbo', '', d)}"
# phyCAM-P
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-pcm939l-phycam-vm016-8bits.dtbo"
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-pcm939-phycam-vm016-8bits.dtbo"
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-pcm939-phycam-vm016-10bits.dtbo"
# DT overlay to enable RS485 at boot time
PHY_EXPANSIONS_SARGAS += "phyboard-stm32mp1-rs485.dtbo"

# List of device tree overlays to install for phyBOARD-Segin
DT_OVERLAYS_INSTALL:phycore-stm32mp13 = "${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', '${PHY_EXPANSIONS_SEGIN}', '', d)}"
# Displays
PHY_EXPANSIONS_SEGIN += "stm32mp135x-phyboard-segin-peb-av-02-lcd.dtbo"
PHY_EXPANSIONS_SEGIN += "stm32mp135x-phyboard-segin-peb-av-02-lcd-resistive.dtbo"
PHY_EXPANSIONS_SEGIN += "${@bb.utils.contains('MACHINE_FEATURES', 'hdmi', 'stm32mp135x-phyboard-segin-peb-av-01-hdmi.dtbo', '', d)}"
# Evaluation board
PHY_EXPANSIONS_SEGIN += "stm32mp13xx-phyboard-segin-peb-eval-01-leds-buttons.dtbo"
PHY_EXPANSIONS_SEGIN += "stm32mp13xx-phyboard-segin-peb-eval-01-jtag.dtbo"
# phyCAM-P
PHY_EXPANSIONS_SEGIN += "stm32mp135x-phyboard-segin-phycam-vm016.dtbo"

# =========================================================================
# u-boot
# =========================================================================
EXTRA_IMAGEDEPENDS += "virtual/bootloader"

# Define default U-Boot config
# STM32MP15
UBOOT_CONFIG:stm32mp15common ?= "default_stm32mp15"
# STM32MP13
UBOOT_CONFIG:stm32mp13common ?= "default_stm32mp13"

# Define u-boot defconfig and binary to use for each UBOOT_CONFIG
UBOOT_CONFIG[default_stm32mp15] = "phycore-stm32mp15_defconfig,,u-boot.dtb"
UBOOT_CONFIG[default_stm32mp13] = "phycore-stm32mp13_defconfig,,u-boot.dtb"

# List of U-Boot device tree to use
UBOOT_DEVICETREE = "${BOOTLOADER_DT}"

# =========================================================================
# u-boot extlinux configuration
# =========================================================================
# Generate extlinux config file
UBOOT_EXTLINUX ?= "1"
UBOOT_EXTLINUX_ROOT ?= "root=PARTLABEL=root"
UBOOT_EXTLINUX_KERNEL_IMAGE ?= "/${KERNEL_IMAGETYPE}"

# Define default FDTDIR for all configs
UBOOT_EXTLINUX_FDTDIR = "${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', '', '/', d)}"
# Define default boot config
UBOOT_EXTLINUX_DEFAULT_LABEL ?= "ampliPHY"
# Define default INITRD
INITRD_IMAGE ??= ""
UBOOT_EXTLINUX_INITRD ?= "${@bb.utils.contains('COMBINED_FEATURES', 'initrd', '/${INITRD_IMAGE}', '', d)}"
# Define extlinux kernel arguments
UBOOT_EXTLINUX_KERNEL_ARGS ?= "rootwait rw"
# Add additional kernel arguments
UBOOT_EXTLINUX_KERNEL_ARGS += "${EXTRA_KERNEL_ARGS}"
UBOOT_EXTLINUX_ROOT:target-emmc:update ?= "root=PARTUUID=${uuidrootfs}"

# =========================================================================
# trusted-firmware-a
# =========================================================================
EXTRA_IMAGEDEPENDS += "virtual/trusted-firmware-a"

# TF-A device tree to use
TF_A_DEVICETREE = "${BOOTLOADER_DT}"

# =========================================================================
# optee
# =========================================================================
# Map OPTEE configuration to optee device tree to use
OPTEE_CONF = "${BOOTLOADER_DT}"

#EXTRA_IMAGEDEPENDS += "virtual-optee-os"

# need to have a version of optee-client and optee-test aligned with optee-os
#PREFERRED_PROVIDER_optee-client = "optee-client"
PREFERRED_PROVIDER_optee-test = "optee-test"

ST_OPTEE_NO_SHAREDMEMORY = "1"

OPTEEMACHINE = "stm32mp1"
OPTEEOUTPUTMACHINE = "stm32mp1"

# =========================================================================
# Enable deploy of bootloader elf files
# =========================================================================
ELF_DEBUG_ENABLE = "1"

# =========================================================================
# WIC for sdcard raw image
# =========================================================================
WKS_FILE_DEPENDS ?= " \
    virtual/bootloader \
    virtual/trusted-firmware-a \
    e2fsprogs-native \
"

IMAGE_CLASSES += "wic-helper"
IMAGE_CLASSES += "wic-bootenv-helper"
EXTERNAL_KERNEL_DEVICETREE ??= ""
IMAGE_BOOT_FILES += "oftree uImage ${EXTERNAL_KERNEL_DEVICETREE} extlinux/extlinux.conf bootenv.txt dt-overlays/*.dtbo;overlays/"
WKS_BOOTIMAGESIZE ?= "20"
WKS_IMAGE_FSTYPES += "wic wic.bz2 wic.bmap"

WKS_FILES ?= "stm32mp1-sdimage.wks.in"
