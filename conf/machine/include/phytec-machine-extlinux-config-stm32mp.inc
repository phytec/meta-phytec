#@DESCRIPTION: STM32MP machine extlinux file configuration

# Define extlinux console for stm32mp machine
UBOOT_EXTLINUX_CONSOLE = "console=${@d.getVar('SERIAL_CONSOLE').split()[1]},${@d.getVar('SERIAL_CONSOLE').split()[0]}"
# Define FIT option for extlinux file generation
UBOOT_EXTLINUX_FIT = "${@bb.utils.contains('MACHINE_FEATURES', 'fit', '1', '0', d)}"
# Define default FDTDIR for all configs
UBOOT_EXTLINUX_FDTDIR = "${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', '', '/', d)}"
# Define default boot config for all config
UBOOT_EXTLINUX_DEFAULT_LABEL ?= "OpenSTLinux"
# Define default INITRD for all configs
INITRD_IMAGE ??= ""
UBOOT_EXTLINUX_INITRD ?= "${@bb.utils.contains('COMBINED_FEATURES', 'initrd', '/${INITRD_IMAGE}', '', d)}"
# Define extlinux kernel arguments
UBOOT_EXTLINUX_KERNEL_ARGS ?= "rootwait rw"
# Add additional kernel arguments
UBOOT_EXTLINUX_KERNEL_ARGS += "${EXTRA_KERNEL_ARGS}"
# Define default SPLASH SCREEN
UBOOT_EXTLINUX_SPLASH ?= "${UBOOT_SPLASH_LANDSCAPE_IMAGE}"

# -----------------------------------------------------------------------------
# Configurations
# -----------------------------------------------------------------------------
# Define additional targets to use
UBOOT_EXTLINUX_TARGETS += "${@bb.utils.contains_any('BOOTDEVICE_LABELS', [ 'sdcard', 'nor-sdcard' ], 'target-sdcard', '', d)}"
UBOOT_EXTLINUX_TARGETS += "${@bb.utils.contains_any('BOOTDEVICE_LABELS', [ 'emmc', 'nor-emmc' ], 'target-emmc', '', d)}"
UBOOT_EXTLINUX_TARGETS += "${@bb.utils.contains_any('BOOTDEVICE_LABELS', [ 'nand-2-64', 'nor-nand-2-64' ], 'target-nand', '', d)}"
UBOOT_EXTLINUX_TARGETS += "${@bb.utils.contains_any('BOOTDEVICE_LABELS', [ 'nand-2-256', 'nor-nand-2-256' ], 'target-nand', '', d)}"
# Define labels for each target
# (mandatory to allow specific var override if needed)
UBOOT_EXTLINUX_LABELS:target-sdcard += "rootfs"
UBOOT_EXTLINUX_LABELS:target-emmc += "rootfs"
UBOOT_EXTLINUX_LABELS:target-nand += "rootfs"
# Define specific bootprefix for each target
UBOOT_EXTLINUX_BOOTPREFIXES:target-sdcard = "${DEVICE:SDCARD}_"
UBOOT_EXTLINUX_BOOTPREFIXES:target-emmc = "${DEVICE:EMMC}_"
UBOOT_EXTLINUX_BOOTPREFIXES:target-nand = "${DEVICE:NAND}_"
# Define ROOT overrides for each target
UBOOT_EXTLINUX_ROOT:target-nand = "ubi.mtd=UBI rootfstype=ubifs root=ubi0:rootfs"
UBOOT_EXTLINUX_ROOT:target-sdcard = "root=PARTUUID=${DEVICE_PARTUUID_ROOTFS:SDCARD}"
UBOOT_EXTLINUX_ROOT:target-emmc = "root=PARTUUID=${DEVICE_PARTUUID_ROOTFS:EMMC}"
# Define INITRD overrides for nand target
UBOOT_EXTLINUX_INITRD:target-nand = ""

# -----------------------------------------------------------------------------
# Configure default labels
# -----------------------------------------------------------------------------
# Define MENU_DESCRIPTION for rootfs label to default one to force selection
UBOOT_EXTLINUX_MENU_DESCRIPTION:rootfs = "OpenSTLinux"

# -----------------------------------------------------------------------------
# Configure additional target
# -----------------------------------------------------------------------------
# Enable UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG var that allows to configure
# additional target:
#   UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG = "extra-target"
# This will create new 'extlinux.conf' file with name 'extra-target_extlinux.conf'
# that will use UBOOT_EXTLINUX_LABELS label list defined for current target.
#
# New label(s) can be defined to append to the label list from current target:
#   UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG[extra-target] = "newlabel1 newlabel2"
# the 'newlabel1' and 'newlabel2' labels will be added in 'extra-target_extlinux.conf'
# -----------------------------------------------------------------------------
# Define additional extra targets to consider
UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG:target-sdcard += "${STM32MP_DEVICETREE}"
UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG:target-emmc += "${STM32MP_DEVICETREE}"
UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG:target-nand += "${STM32MP_DEVICETREE}"

