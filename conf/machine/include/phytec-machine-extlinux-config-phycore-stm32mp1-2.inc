#@DESCRIPTION: STM32MP machine extlinux file configuration

# Set configuration file to monitor
UBOOT_EXTLINUX_CONFIGURE_FILES_append = " ${PHYCORE_STM32MP_BASE}/conf/machine/include/phytec-machine-extlinux-config-phycore-stm32mp1-2.inc:True "

include conf/machine/include/phytec-machine-extlinux-config-stm32mp.inc

# -----------------------------------------------------------------------------
# PHYCORE STM32mp1-2 configuration
# -----------------------------------------------------------------------------
# Define available targets to use
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-2_sdcard"
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-2_nand"
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-2_nor-nand"
# Define bootprefix for each target
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-2_sdcard       = "${EXTLINUX_BOOTDEVICE_SDCARD}_phycore-stm32mp1-2_"
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-2_nand = "${EXTLINUX_BOOTDEVICE_NAND}_phycore-stm32mp1-2_"
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-2_nor-nand = "${EXTLINUX_BOOTDEVICE_NAND}_phycore-stm32mp1-2_"
# Define labels for each target
UBOOT_EXTLINUX_LABELS_mp1-2_sdcard       = "phycore-stm32mp1-2-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-2_nand = "phycore-stm32mp1-2-nand"
UBOOT_EXTLINUX_LABELS_mp1-2_nor-nand = "phycore-stm32mp1-2-nor-nand"
# Define default boot config for each target
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-2_sdcard       ?= "phycore-stm32mp1-2-sdcard"
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-2_nand ?= "phycore-stm32mp1-2-nand"
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-2_nor-nand ?= "phycore-stm32mp1-2-nor-nand"
# Define FDT overrides for all labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-sdcard       = "/phycore-stm32mp1-2.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-nand = "/phycore-stm32mp1-2.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-nor-nand = "/phycore-stm32mp1-2.dtb"
# Define ROOT overrides for all labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-sdcard = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-nand = "${EXTLINUX_ROOT_NAND}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-nor-nand = "${EXTLINUX_ROOT_NAND}"
# -----------------------------------------------------------------------------
# Append A7 examples labels for each target
UBOOT_EXTLINUX_LABELS_mp1-2_sdcard       += "phycore-stm32mp1-2-a7-examples-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-2_nand += "phycore-stm32mp1-2-a7-examples-nand"
UBOOT_EXTLINUX_LABELS_mp1-2_nor-nand += "phycore-stm32mp1-2-a7-examples-nor-nand"
# Define FDT overrides for A7 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-a7-examples-sdcard       = "/phycore-stm32mp1-2-a7-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-a7-examples-nand = "/phycore-stm32mp1-2-a7-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-a7-examples-nor-nand = "/phycore-stm32mp1-2-a7-examples.dtb"
# Define ROOT overrides for A7 labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-a7-examples-sdcard       = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-a7-examples-nand = "${EXTLINUX_ROOT_NAND}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-a7-examples-nor-nand = "${EXTLINUX_ROOT_NAND}"
# -----------------------------------------------------------------------------
# Append M4 examples labels for each target
UBOOT_EXTLINUX_LABELS_mp1-2_sdcard       += "phycore-stm32mp1-2-m4-examples-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-2_nand += "phycore-stm32mp1-2-m4-examples-nand"
UBOOT_EXTLINUX_LABELS_mp1-2_nor-nand += "phycore-stm32mp1-2-m4-examples-nor-nand"
# Define FDT overrides for M4 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-m4-examples-sdcard       = "/phycore-stm32mp1-2-m4-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-m4-examples-nand = "/phycore-stm32mp1-2-m4-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-m4-examples-nor-nand = "/phycore-stm32mp1-2-m4-examples.dtb"
# Define ROOT overrides for M4 labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-m4-examples-sdcard       = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-m4-examples-nand = "${EXTLINUX_ROOT_NAND}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-2-m4-examples-nor-nand = "${EXTLINUX_ROOT_NAND}"
