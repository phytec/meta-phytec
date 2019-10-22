#@DESCRIPTION: STM32MP machine extlinux file configuration

# Set configuration file to monitor
UBOOT_EXTLINUX_CONFIGURE_FILES_append = " ${PHYCORE_STM32MP_BASE}/conf/machine/include/phytec-machine-extlinux-config-stm32mp.inc:True "

# Define the config flags to use to generate all extlinux targets
UBOOT_EXTLINUX_CONFIG_FLAGS += "${@bb.utils.contains('FLASHLAYOUT_CONFIG_LABELS', 'emmc', '%s' % bb.utils.contains('BOOTSCHEME_LABELS', 'optee', 'emmc emmc-optee', 'emmc', d), '', d)}"
UBOOT_EXTLINUX_CONFIG_FLAGS += "${@bb.utils.contains_any('FLASHLAYOUT_CONFIG_LABELS', [ 'nand-4-256', 'nor-nand-4-256' ], 'nand', '', d)}"
UBOOT_EXTLINUX_CONFIG_FLAGS += "${@bb.utils.contains('FLASHLAYOUT_CONFIG_LABELS', 'nor-emmc', 'nor-emmc', '', d)}"
UBOOT_EXTLINUX_CONFIG_FLAGS += "${@bb.utils.contains('FLASHLAYOUT_CONFIG_LABELS', 'nor-sdcard', 'nor-sdcard', '', d)}"
UBOOT_EXTLINUX_CONFIG_FLAGS += "${@bb.utils.contains('FLASHLAYOUT_CONFIG_LABELS', 'sdcard', '%s' % bb.utils.contains('BOOTSCHEME_LABELS', 'optee', 'sdcard nor-emmc', 'sdcard', d), '', d)}"

# Provide the list of supported devicetree for each config flag
UBOOT_EXTLINUX_DEVICEFLAG_emmc          ?= "${STM32MP_DT_FILES_PHYCORE}"
UBOOT_EXTLINUX_DEVICEFLAG_emmc-optee    ?= "${STM32MP_DT_FILES_PHYCORE}"
UBOOT_EXTLINUX_DEVICEFLAG_nand          ?= "${STM32MP_DT_FILES_PHYCORE}"
UBOOT_EXTLINUX_DEVICEFLAG_nor-emmc      ?= "${STM32MP_DT_FILES_PHYCORE}"
UBOOT_EXTLINUX_DEVICEFLAG_nor-sdcard    ?= "${STM32MP_DT_FILES_PHYCRE}"
UBOOT_EXTLINUX_DEVICEFLAG_sdcard        ?= "${STM32MP_DT_FILES_PHYCORE}"
UBOOT_EXTLINUX_DEVICEFLAG_nor-emmc  ?= "${STM32MP_DT_FILES_PHYCORE}"

# Set generic extlinux bootdevice variable to ease definition
EXTLINUX_BOOTDEVICE_EMMC      = "mmc1"
EXTLINUX_BOOTDEVICE_SDCARD    = "mmc0"
EXTLINUX_BOOTDEVICE_NORSDCARD = "nor0"
EXTLINUX_BOOTDEVICE_NOREMMC   = "nor0-mmc1"
EXTLINUX_BOOTDEVICE_NAND      = "nand0"

# Set generic extlinux root variable to ease definition
EXTLINUX_ROOT_EMMC         = "root=/dev/mmcblk1p4"
EXTLINUX_ROOT_EMMC_OPTEE   = "root=/dev/mmcblk1p7"
EXTLINUX_ROOT_NAND         = "ubi.mtd=UBI rootfstype=ubifs root=ubi0:rootfs"
EXTLINUX_ROOT_NOREMMC      = "root=/dev/mmcblk1p3"
EXTLINUX_ROOT_NORSDCARD    = "root=/dev/mmcblk0p3"
EXTLINUX_ROOT_SDCARD       = "root=/dev/mmcblk0p6"

# -----------------------------------------------------------------------------
# REMINDER: how 'exlinux.conf' files are built
#
# The 'extlinux.conf' files are generated under ${UBOOT_EXTLINUX_INSTALL_DIR}:
#   ${UBOOT_EXTLINUX_INSTALL_DIR}/${UBOOT_EXTLINUX_BOOTPREFIXES_${UBOOT_EXTLINUX_TARGETS}[0]}extlinux/extlinux.conf
#   ${UBOOT_EXTLINUX_INSTALL_DIR}/${UBOOT_EXTLINUX_BOOTPREFIXES_${UBOOT_EXTLINUX_TARGETS}[1]}extlinux/extlinux.conf
#   ...
#
# File content (${UBOOT_EXTLINUX_BOOTPREFIXES_${UBOOT_EXTLINUX_TARGETS}[0]}extlinux/exlinux.conf):
#   menu title Select the boot mode
#   TIMEOUT ${UBOOT_EXTLINUX_TIMEOUT}
#   DEFAULT ${UBOOT_EXTLINUX_DEFAULT_LABEL_${UBOOT_EXTLINUX_TARGETS}[0]}
#   LABEL ${UBOOT_EXTLINUX_LABELS_${UBOOT_EXTLINUX_TARGETS}[0]}[0]
#       KERNEL ${UBOOT_EXTLINUX_KERNEL}     < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_KERNEL_${IMAGE_UBOOT_EXTLINUX_LABELS}[0]}  >
#       FDT ${UBOOT_EXTLINUX_FDT}           < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_FDT_${IMAGE_UBOOT_EXTLINUX_LABELS}[0]}     >
#       APPEND ${UBOOT_EXTLINUX_ROOT}       < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_ROOT_${IMAGE_UBOOT_EXTLINUX_LABELS}[0]}    >
#   LABEL ${UBOOT_EXTLINUX_LABELS_${UBOOT_EXTLINUX_TARGETS}[0]}[1]
#       KERNEL ${UBOOT_EXTLINUX_KERNEL}     < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_KERNEL_${IMAGE_UBOOT_EXTLINUX_LABELS}[1]}  >
#       FDT ${UBOOT_EXTLINUX_FDT}           < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_FDT_${IMAGE_UBOOT_EXTLINUX_LABELS}[1]}     >
#       APPEND ${UBOOT_EXTLINUX_ROOT}       < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_ROOT_${IMAGE_UBOOT_EXTLINUX_LABELS}[1]}    >
#
# File content (${UBOOT_EXTLINUX_BOOTPREFIXES_${UBOOT_EXTLINUX_TARGETS}[0]}extlinux/exlinux.conf):
#   menu title Select the boot mode
#   TIMEOUT ${UBOOT_EXTLINUX_TIMEOUT}
#   DEFAULT ${UBOOT_EXTLINUX_DEFAULT_LABEL_${UBOOT_EXTLINUX_TARGETS}[1]}
#   LABEL ${UBOOT_EXTLINUX_LABELS_${UBOOT_EXTLINUX_TARGETS}[1]}[0]
#       KERNEL ${UBOOT_EXTLINUX_KERNEL}     < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_KERNEL_${IMAGE_UBOOT_EXTLINUX_LABELS}[0]}  >
#       FDT ${UBOOT_EXTLINUX_FDT}           < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_FDT_${IMAGE_UBOOT_EXTLINUX_LABELS}[0]}     >
#       APPEND ${UBOOT_EXTLINUX_ROOT}       < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_ROOT_${IMAGE_UBOOT_EXTLINUX_LABELS}[0]}    >
#   LABEL ${UBOOT_EXTLINUX_LABELS_${UBOOT_EXTLINUX_TARGETS}[1]}[1]
#       KERNEL ${UBOOT_EXTLINUX_KERNEL}     < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_KERNEL_${IMAGE_UBOOT_EXTLINUX_LABELS}[1]}  >
#       FDT ${UBOOT_EXTLINUX_FDT}           < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_FDT_${IMAGE_UBOOT_EXTLINUX_LABELS}[1]}     >
#       APPEND ${UBOOT_EXTLINUX_ROOT}       < OR OVERRIDE WITH :    ${UBOOT_EXTLINUX_ROOT_${IMAGE_UBOOT_EXTLINUX_LABELS}[1]}    >
# -----------------------------------------------------------------------------

# Set extlinux console for stm32mp machine
UBOOT_EXTLINUX_CONSOLE = "console=${@d.getVar('SERIAL_CONSOLE').split()[1]},${@d.getVar('SERIAL_CONSOLE').split()[0]}"

# -----------------------------------------------------------------------------
# PHYCORE STM32mp1-1 configuration
# -----------------------------------------------------------------------------
# Define available targets to use
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-1_sdcard"
UBOOT_EXTLINUX_CONFIGURED_TARGETS += "mp1-1_nor-emmc"
# Define bootprefix for each target
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-1_sdcard       = "${EXTLINUX_BOOTDEVICE_SDCARD}_phycore-stm32mp1-1_"
UBOOT_EXTLINUX_BOOTPREFIXES_mp1-1_nor-emmc = "${EXTLINUX_BOOTDEVICE_NOREMMC}_phycore-stm32mp1-1_"
# Define labels for each target
UBOOT_EXTLINUX_LABELS_mp1-1_sdcard       = "phycore-stm32mp1-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-1_nor-emmc = "phycore-stm32mp1-nor-emmc"
# Define default boot config for each target
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-1_sdcard       ?= "phycore-stm32mp1-sdcard"
UBOOT_EXTLINUX_DEFAULT_LABEL_mp1-1_nor-emmc ?= "phycore-stm32mp1-nor-emmc"
# Define FDT overrides for all labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-sdcard       = "/phycore-stm32mp1-1.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-nor-emmc = "/phycore-stm32mp1-1.dtb"
# Define ROOT overrides for all labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-sdcard = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-nor-emmc = "${EXTLINUX_ROOT_NOREMMC}"
# -----------------------------------------------------------------------------
# Append A7 examples labels for each target
UBOOT_EXTLINUX_LABELS_mp1-1_sdcard       += "phycore-stm32mp1-a7-examples-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-1_nor-emmc += "phycore-stm32mp1-a7-examples-nor-emmc"
# Define FDT overrides for A7 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-a7-examples-sdcard       = "/phycore-stm32mp1-1-a7-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-a7-examples-nor-emmc = "/phycore-stm32mp1-1-a7-examples.dtb"
# Define ROOT overrides for A7 labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-a7-examples-sdcard       = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-a7-examples-nor-emmc = "${EXTLINUX_ROOT_NOREMMC}"
# -----------------------------------------------------------------------------
# Append M4 examples labels for each target
UBOOT_EXTLINUX_LABELS_mp1-1_sdcard       += "phycore-stm32mp1-m4-examples-sdcard"
UBOOT_EXTLINUX_LABELS_mp1-1_nor-emmc += "phycore-stm32mp1-m4-examples-nor-emmc"
# Define FDT overrides for M4 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-m4-examples-sdcard       = "/phycore-stm32mp1-1-m4-examples.dtb"
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-m4-examples-nor-emmc = "/phycore-stm32mp1-1-m4-examples.dtb"
# Define ROOT overrides for M4 labels
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-m4-examples-sdcard       = "${EXTLINUX_ROOT_SDCARD}"
UBOOT_EXTLINUX_ROOT_phycore-stm32mp1-m4-examples-nor-emmc = "${EXTLINUX_ROOT_NOREMMC}"

