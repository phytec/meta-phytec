#@DESCRIPTION: PHYCORE-STM32mp1-4 machine extlinux file configuration

# Set configuration file to monitor
UBOOT_EXTLINUX_CONFIGURE_FILES_append = " ${PHYCORE_STM32MP_BASE}/conf/machine/include/phytec-machine-extlinux-config-phycore-stm32mp1-4.inc:True "

include conf/machine/include/phytec-machine-extlinux-config-stm32mp.inc

# -----------------------------------------------------------------------------
# PHYCORE STM32mp1-4 configuration
# -----------------------------------------------------------------------------
# Define available targets to use
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-4_sdcard"
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-4_emmc"
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-4_nor-emmc"
# Define bootprefix for each target
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-4_sdcard       = "${EXTLINUX_BOOTDEVICE_SDCARD}_phycore-stm32mp1-4_"
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-4_emmc = "${EXTLINUX_BOOTDEVICE_EMMC}_phycore-stm32mp1-4_"
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-4_nor-emmc = "${EXTLINUX_BOOTDEVICE_NOREMMC}_phycore-stm32mp1-4_"
# Define labels for each target
UBOOT_EXTLINUX_LABELS_mp1-4_sdcard       = "phycore-stm32mp1-4-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-4_emmc = "phycore-stm32mp1-4-emmc"
UBOOT_EXTLINUX_LABELS_mp1-4_nor-emmc = "phycore-stm32mp1-4-nor-emmc"
# Define default boot config for each target
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-4_sdcard       ?= "phycore-stm32mp1-4-sdcard"
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-4_emmc ?= "phycore-stm32mp1-4-emmc"
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-4_nor-emmc ?= "phycore-stm32mp1-4-nor-emmc"
# Define FDT overrides for all labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-sdcard       = "/phycore-stm32mp1-4.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-emmc = "/phycore-stm32mp1-4.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-nor-emmc = "/phycore-stm32mp1-4.dtb"
# Define ROOT overrides for all labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-sdcard = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-emmc = "${EXTLINUX_ROOT_EMMC}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-nor-emmc = "${EXTLINUX_ROOT_NOREMMC}"
# -----------------------------------------------------------------------------
# Append A7 examples labels for each target
UBOOT_EXTLINUX_LABELS_mp1-4_sdcard       += "phycore-stm32mp1-4-a7-examples-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-4_emmc += "phycore-stm32mp1-4-a7-examples-emmc"
UBOOT_EXTLINUX_LABELS_mp1-4_nor-emmc += "phycore-stm32mp1-4-a7-examples-nor-emmc"
# Define FDT overrides for A7 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-a7-examples-sdcard       = "/phycore-stm32mp1-4-a7-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-a7-examples-emmc = "/phycore-stm32mp1-4-a7-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-a7-examples-nor-emmc = "/phycore-stm32mp1-4-a7-examples.dtb"
# Define ROOT overrides for A7 labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-a7-examples-sdcard       = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-a7-examples-emmc = "${EXTLINUX_ROOT_EMMC}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-a7-examples-nor-emmc = "${EXTLINUX_ROOT_NOREMMC}"
# -----------------------------------------------------------------------------
# Append M4 examples labels for each target
UBOOT_EXTLINUX_LABELS_mp1-4_sdcard       += "phycore-stm32mp1-4-m4-examples-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-4_emmc += "phycore-stm32mp1-4-m4-examples-emmc"
UBOOT_EXTLINUX_LABELS_mp1-4_nor-emmc += "phycore-stm32mp1-4-m4-examples-nor-emmc"
# Define FDT overrides for M4 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-m4-examples-sdcard       = "/phycore-stm32mp1-4-m4-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-m4-examples-emmc = "/phycore-stm32mp1-4-m4-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-4-m4-examples-nor-emmc = "/phycore-stm32mp1-4-m4-examples.dtb"
# Define ROOT overrides for M4 labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-m4-examples-sdcard       = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-m4-examples-emmc = "${EXTLINUX_ROOT_EMMC}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-4-m4-examples-nor-emmc = "${EXTLINUX_ROOT_NOREMMC}"
