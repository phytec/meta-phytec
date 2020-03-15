#@DESCRIPTION: STM32MP machine extlinux file configuration

# Set configuration file to monitor
UBOOT_EXTLINUX_CONFIGURE_FILES_append = " ${PHYCORE_STM32MP_BASE}/conf/machine/include/phytec-machine-extlinux-config-phycore-stm32mp1-7.inc:True "

include conf/machine/include/phytec-machine-extlinux-config-stm32mp.inc

# -----------------------------------------------------------------------------
# PHYCORE STM32mp1-7 configuration
# -----------------------------------------------------------------------------
# Define available targets to use
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-7_sdcard"
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-7_nand"
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-7_nor-nand"
# Define bootprefix for each target
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-7_sdcard       = "${EXTLINUX_BOOTDEVICE_SDCARD}_phycore-stm32mp1-7_"
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-7_nand = "${EXTLINUX_BOOTDEVICE_NAND}_phycore-stm32mp1-7_"
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-7_nor-nand = "${EXTLINUX_BOOTDEVICE_NAND}_phycore-stm32mp1-7_"
# Define labels for each target
UBOOT_EXTLINUX_LABELS_mp1-7_sdcard       = "phycore-stm32mp1-7-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-7_nand = "phycore-stm32mp1-7-nand"
UBOOT_EXTLINUX_LABELS_mp1-7_nor-nand = "phycore-stm32mp1-7-nor-nand"
# Define default boot config for each target
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-7_sdcard       ?= "phycore-stm32mp1-7-sdcard"
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-7_nand ?= "phycore-stm32mp1-7-nand"
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-7_nor-nand ?= "phycore-stm32mp1-7-nor-nand"
# Define FDT overrides for all labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-sdcard       = "/phycore-stm32mp1-7.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-nand = "/phycore-stm32mp1-7.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-nor-nand = "/phycore-stm32mp1-7.dtb"
# Define ROOT overrides for all labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-sdcard = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-nand = "${EXTLINUX_ROOT_NAND}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-nor-nand = "${EXTLINUX_ROOT_NAND}"
# -----------------------------------------------------------------------------
# Append A7 examples labels for each target
UBOOT_EXTLINUX_LABELS_mp1-7_sdcard       += "phycore-stm32mp1-7-a7-examples-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-7_nand += "phycore-stm32mp1-7-a7-examples-nand"
UBOOT_EXTLINUX_LABELS_mp1-7_nor-nand += "phycore-stm32mp1-7-a7-examples-nor-nand"
# Define FDT overrides for A7 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-a7-examples-sdcard       = "/phycore-stm32mp1-7-a7-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-a7-examples-nand = "/phycore-stm32mp1-7-a7-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-a7-examples-nor-nand = "/phycore-stm32mp1-7-a7-examples.dtb"
# Define ROOT overrides for A7 labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-a7-examples-sdcard       = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-a7-examples-nand = "${EXTLINUX_ROOT_NAND}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-a7-examples-nor-nand = "${EXTLINUX_ROOT_NAND}"
# -----------------------------------------------------------------------------
# Append M4 examples labels for each target
UBOOT_EXTLINUX_LABELS_mp1-7_sdcard       += "phycore-stm32mp1-7-m4-examples-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-7_nand += "phycore-stm32mp1-7-m4-examples-nand"
UBOOT_EXTLINUX_LABELS_mp1-7_nor-nand += "phycore-stm32mp1-7-m4-examples-nor-nand"
# Define FDT overrides for M4 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-m4-examples-sdcard       = "/phycore-stm32mp1-7-m4-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-m4-examples-nand = "/phycore-stm32mp1-7-m4-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-7-m4-examples-nor-nand = "/phycore-stm32mp1-7-m4-examples.dtb"
# Define ROOT overrides for M4 labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-m4-examples-sdcard       = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-m4-examples-nand = "${EXTLINUX_ROOT_NAND}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-7-m4-examples-nor-nand = "${EXTLINUX_ROOT_NAND}"
