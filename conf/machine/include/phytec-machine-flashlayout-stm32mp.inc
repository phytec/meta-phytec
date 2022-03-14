#@DESCRIPTION: STM32MP machine flashlayout configuration

inherit flashlayout-stm32mp

# Add specific dependencies to get all binaries generated before flashlayout files
FLASHLAYOUT_DEPEND_TASKS += "${@bb.utils.contains('EXTRA_IMAGEDEPENDS', 'virtual/trusted-firmware-a', 'virtual/trusted-firmware-a:do_deploy', '', d)}"
FLASHLAYOUT_DEPEND_TASKS += "${@bb.utils.contains('EXTRA_IMAGEDEPENDS', 'virtual/bootloader', 'virtual/bootloader:do_deploy', '', d)}"
FLASHLAYOUT_DEPEND_TASKS += "${@bb.utils.contains('EXTRA_IMAGEDEPENDS', 'virtual/optee-os', 'virtual/optee-os:do_deploy', '', d)}"

# -----------------------------------------------------------------------------
# For the stm32mp flashlayout files, configuration is done to get output file
# with following name scheme:
#   FlashLayout_<CONFIG_LABEL>[_<TYPE_LABEL>-<BOOTSCHEME_LABEL>].tsv
#
# The different labels are stored as list in different vars:
#   - <BOOTSCHEME_LABEL>    FLASHLAYOUT_BOOTSCHEME_LABELS
#   - <CONFIG_LABEL>        FLASHLAYOUT_CONFIG_LABELS
#   - <TYPE_LABEL>          FLASHLAYOUT_TYPE_LABELS
#
# The flashlayout file name is generated with following loop logic:
#   for bootscheme in FLASHLAYOUT_BOOTSCHEME_LABELS     (expanded according to ongoing OVERRIDES)
#       for config in FLASHLAYOUT_CONFIG_LABELS         (expanded according to ongoing $bootscheme and OVERRIDES)
#           for typelabel in FLASHLAYOUT_TYPE_LABELS    (expanded according to ongoing $bootscheme, $config and OVERRIDES)
#               if FLASHLAYOUT_BOOTSCHEME_LABELS items > 1 && FLASHLAYOUT_TYPE_LABELS items > 1
#                   flashlayout_name = FlashLayout_$config_$typelabel-$bootscheme.tsv
#               else
#                   flashlayout_name = FlashLayout_$config.tsv
#
# Each flashlayout file should contain lines that set the partition properties:
#   <Opt> <Id> <Name> <Type> <IP> <Offset> <Binary>
#
# The partition properties are set with 'FLASHLAYOUT_PARTITION_xxx' vars:
#   - <Opt>     FLASHLAYOUT_PARTITION_ENABLE
#   - <Id>      FLASHLAYOUT_PARTITION_ID
#   - <Name>    Item from FLASHLAYOUT_PARTITION_LABELS list
#   - <Type>    FLASHLAYOUT_PARTITION_TYPE with optional FLASHLAYOUT_PARTITION_COPY (in case greater than 1)
#   - <IP>      FLASHLAYOUT_PARTITION_DEVICE
#   - <Offset>  FLASHLAYOUT_PARTITION_OFFSET
#   - <Binary>  FLASHLAYOUT_PARTITION_BIN2LOAD
#
# For the '$flashlayout_name' flashlayout file, the partition lines are generated
# with following loop logic:
#   for partition in FLASHLAYOUT_PARTITION_LABELS       (expanded according to ongoing $bootscheme, $config and OVERRIDES)
#       OVERRIDES_PREV = OVERRIDES
#       OVERRIDES = $partition:OVERRIDES
#       Compute partition properties for $partiton      (expanded according to ongoing $bootscheme, $config and OVERRIDES)
#       Write new partition line to $flashlayout_name
#       OVERRIDES = OVERRIDES_PREV
#
# Modifying the OVERRIDES inside the loop allows dynamic configuration for each
# partition property. This leaves plenty of possibility and guarantee specific
# '$partition' override.
# -----------------------------------------------------------------------------

# -----------------------------------------------------------------------------
# Define bootscheme labels
# -----------------------------------------------------------------------------
FLASHLAYOUT_BOOTSCHEME_LABELS += "${BOOTSCHEME_LABELS}"

# -----------------------------------------------------------------------------
# Define config labels
# -----------------------------------------------------------------------------
# Priority var assignment (where <OVERRIDES> are the usual override mechanism):
#   1) FLASHLAYOUT_CONFIG_LABELS_<BOOTSCHEME>_<OVERRIDES>
#   2) FLASHLAYOUT_CONFIG_LABELS_<BOOTSCHEME>
#   3) FLASHLAYOUT_CONFIG_LABELS_<OVERRIDES>
#   4) FLASHLAYOUT_CONFIG_LABELS
# -----------------------------------------------------------------------------
FLASHLAYOUT_CONFIG_LABELS += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'emmc', 'emmc', '', d)}"
FLASHLAYOUT_CONFIG_LABELS += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nor-emmc', 'nor-emmc', '', d)}"
FLASHLAYOUT_CONFIG_LABELS += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-256', 'nand-2-256', '', d)}"
FLASHLAYOUT_CONFIG_LABELS += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-64', 'nand-2-64', '', d)}"
FLASHLAYOUT_CONFIG_LABELS += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nor-sdcard', 'nor-sdcard', '', d)}"
FLASHLAYOUT_CONFIG_LABELS += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'sdcard', 'sdcard', '', d)}"

# Set by default this variable to 0, and set to 1 only when we are using st-example-image-*
ST_EXAMPLE_IMAGE ??= "0"

#FIXME need to manage overall device size to abort flashlayout creation in case of oversizing the storage devices

# Remove NAND flashlayout when we are using st-example-image-* as rootfs too big for a NAND device size inferior or equal to 1 GBytes
FLASHLAYOUT_CONFIG_LABELS_remove = "${@bb.utils.contains('ST_EXAMPLE_IMAGE', '1', 'nand-2-256', '', d)}"
FLASHLAYOUT_CONFIG_LABELS_remove = "${@bb.utils.contains('ST_EXAMPLE_IMAGE', '1', 'nand-2-64', '', d)}"

# -----------------------------------------------------------------------------
# Define label types for each config
# -----------------------------------------------------------------------------
# NOTE: We use devicetree to allow proper filtering on supported storage device
# -----------------------------------------------------------------------------
# Priority var assignment (where <OVERRIDES> are the usual override mechanism):
#   1) FLASHLAYOUT_TYPE_LABELS_<BOOTSCHEME>_<CONFIG>_<OVERRIDES>
#   2) FLASHLAYOUT_TYPE_LABELS_<BOOTSCHEME>_<CONFIG>
#   3) FLASHLAYOUT_TYPE_LABELS_<BOOTSCHEME>_<OVERRIDES>
#   4) FLASHLAYOUT_TYPE_LABELS_<BOOTSCHEME>
#   5) FLASHLAYOUT_TYPE_LABELS_<CONFIG>_<OVERRIDES>
#   6) FLASHLAYOUT_TYPE_LABELS_<CONFIG>
#   7) FLASHLAYOUT_TYPE_LABELS_<OVERRIDES>
#   8) FLASHLAYOUT_TYPE_LABELS
# -----------------------------------------------------------------------------
# EMMC
FLASHLAYOUT_TYPE_LABELS_emmc = "${STM32MP_DT_FILES_PHYCORE}"
# NAND
FLASHLAYOUT_TYPE_LABELS_nand-2-64 = "${STM32MP_DT_FILES_PHYCORE}"
FLASHLAYOUT_TYPE_LABELS_nand-2-256 = "${STM32MP_DT_FILES_PHYCORE}"
# NOR
FLASHLAYOUT_TYPE_LABELS_nor-emmc       = "${STM32MP_DT_FILES_PHYCORE}"
FLASHLAYOUT_TYPE_LABELS_nor-nand-2-64 = "${STM32MP_DT_FILES_PHYCORE}"
FLASHLAYOUT_TYPE_LABELS_nor-nand-2-256 = "${STM32MP_DT_FILES_PHYCORE}"
FLASHLAYOUT_TYPE_LABELS_nor-sdcard     = "${STM32MP_DT_FILES_PHYCORE}"
# SDCARD
FLASHLAYOUT_TYPE_LABELS_sdcard = "${STM32MP_DT_FILES_PHYCORE}"

# -----------------------------------------------------------------------------
# Define partitions to consider for flashlayout file generation
# -----------------------------------------------------------------------------
# Priority var assignment (where <OVERRIDES> are the usual override mechanism):
#   1) FLASHLAYOUT_PARTITION_LABELS_<BOOTSCHEME>_<CONFIG>_<OVERRIDES>
#   2) FLASHLAYOUT_PARTITION_LABELS_<BOOTSCHEME>_<CONFIG>
#   3) FLASHLAYOUT_PARTITION_LABELS_<BOOTSCHEME>_<OVERRIDES>
#   4) FLASHLAYOUT_PARTITION_LABELS_<BOOTSCHEME>
#   5) FLASHLAYOUT_PARTITION_LABELS_<CONFIG>_<OVERRIDES>
#   6) FLASHLAYOUT_PARTITION_LABELS_<CONFIG>
#   7) FLASHLAYOUT_PARTITION_LABELS_<OVERRIDES>
#   8) FLASHLAYOUT_PARTITION_LABELS
# -----------------------------------------------------------------------------
FLASHLAYOUT_PROGRAMMER_SECTIONS ?= "${STM32MP_FSBL1_NAME}-boot ${STM32MP_SSBL1_NAME}-boot"

FLASHLAYOUT_PARTITION_LABELS_optee_emmc = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'emmc').split()])} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fip', '', ' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_OPTEE_CONFIG', 'emmc').split()]), d)} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'emmc').split()])} \
    "
FLASHLAYOUT_PARTITION_LABELS_optee_nand-2-256 = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nand-2-256').split()])} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fip', '', ' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_OPTEE_CONFIG', 'nand').split()]), d)} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'nand-2-256').split()])} \
    "
FLASHLAYOUT_PARTITION_LABELS_optee_nand-2-64 = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nand-2-64').split()])} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fip', '', ' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_OPTEE_CONFIG', 'nand').split()]), d)} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'nand-2-64').split()])} \
    "
FLASHLAYOUT_PARTITION_LABELS_optee_nor-emmc = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nor-emmc').split()])} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fip', '', ' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_OPTEE_CONFIG', 'nor-emmc').split()]), d)} \
    empty \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'nor-emmc').split()])}\
    "
FLASHLAYOUT_PARTITION_LABELS_optee_sdcard  = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'sdcard').split()])} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fip', '', ' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_OPTEE_CONFIG', 'sdcard').split()]), d)} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'sdcard').split()])} \
    "

FLASHLAYOUT_PARTITION_LABELS_trusted_emmc = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'emmc').split()])} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'emmc').split()])} \
    "
FLASHLAYOUT_PARTITION_LABELS_trusted_nand-2-256 = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nand-2-256').split()])} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'nand-2-256').split()])} \
    "
FLASHLAYOUT_PARTITION_LABELS_trusted_nand-2-64 = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nand-2-64').split()])} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'nand-2-64').split()])} \
    "
FLASHLAYOUT_PARTITION_LABELS_trusted_nor-emmc = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nor-emmc').split()])} \
    empty \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'nor-emmc').split()])} \
    "
FLASHLAYOUT_PARTITION_LABELS_trusted_sdcard = "\
    ${FLASHLAYOUT_PROGRAMMER_SECTIONS} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'sdcard').split()])} \
    ${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'sdcard').split()])} \
    "

# -----------------------------------------------------------------------------
# Partition properties configuration
# -----------------------------------------------------------------------------
# Priority var assignment (where <OVERRIDES> are the usual override mechanism
# with dynamic append of ongoing 'partition name' item):
#           1) FLASHLAYOUT_PARTITION_xxx_<BOOTSCHEME>_<CONFIG>_<OVERRIDES>
#           2) FLASHLAYOUT_PARTITION_xxx_<BOOTSCHEME>_<CONFIG>
#           3) FLASHLAYOUT_PARTITION_xxx_<BOOTSCHEME>_<OVERRIDES>
#           4) FLASHLAYOUT_PARTITION_xxx_<BOOTSCHEME>
#           5) FLASHLAYOUT_PARTITION_xxx_<CONFIG>_<OVERRIDES>
#           6) FLASHLAYOUT_PARTITION_xxx_<CONFIG>
#           7) FLASHLAYOUT_PARTITION_xxx_<OVERRIDES>
#           8) FLASHLAYOUT_PARTITION_xxx
#           9) Default 'FLASHLAYOUT_PARTITION_xxx' to 'none' when not defined
# -----------------------------------------------------------------------------
FLASHLAYOUT_PARTITION_ENABLE = "P"
FLASHLAYOUT_PARTITION_ENABLE_${STM32MP_FSBL1_NAME}-boot = "-"
FLASHLAYOUT_PARTITION_ENABLE_${STM32MP_SSBL1_NAME}-boot = "-"
FLASHLAYOUT_PARTITION_ENABLE_empty = "PE"
FLASHLAYOUT_PARTITION_ENABLE_nor-emmc_${STM32MP_UENV_NAME} = "PED"
# Need to make sure to delete partition that contains U-Boot env before update (gpt partitions only)
FLASHLAYOUT_PARTITION_ENABLE_sdcard_${STM32MP_SSBL1_NAME} = "PD"
FLASHLAYOUT_PARTITION_ENABLE_emmc_${STM32MP_SSBL1_NAME} = "PD"

# -----------------------------------------------------------------------------
# Partition ID
# -----------------------------------------------------------------------------
# For FSBL and SSBL binaries loaded in RAM to program the devices there are two
# reserved IDs on STM32CubeProgrammer side:
#   0x01 for FSBL
#   0x03 for SSBL
FLASHLAYOUT_PARTITION_ID_${STM32MP_FSBL1_NAME}-boot = "0x01"
FLASHLAYOUT_PARTITION_ID_${STM32MP_SSBL1_NAME}-boot = "0x03"

FLASHLAYOUT_PARTITION_TYPE                  = "Binary"
FLASHLAYOUT_PARTITION_TYPE_ubifs            = "System"

FLASHLAYOUT_PARTITION_COPY ?= "1"

FLASHLAYOUT_PARTITION_DEVICE_emmc       = "none:${FLASHLAYOUT_PROGRAMMER_SECTIONS},${DEVICE_EMMC}:default"
FLASHLAYOUT_PARTITION_DEVICE_nor-emmc   = "none:${FLASHLAYOUT_PROGRAMMER_SECTIONS},${DEVICE_NOR}:default,${DEVICE_EMMC}:${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'nor-emmc').split()])}"
FLASHLAYOUT_PARTITION_DEVICE_nand-2-256 = "none:${FLASHLAYOUT_PROGRAMMER_SECTIONS},${DEVICE_NAND}:default"
FLASHLAYOUT_PARTITION_DEVICE_nand-2-64  = "none:${FLASHLAYOUT_PROGRAMMER_SECTIONS},${DEVICE_NAND}:default"
FLASHLAYOUT_PARTITION_DEVICE_nor-sdcard = "none:${FLASHLAYOUT_PROGRAMMER_SECTIONS},${DEVICE_NOR}:default,${DEVICE_SDCARD}:${@' '.join(['%s' % l.split(',')[1] for l in d.getVarFlag('PARTITIONS_CONFIG', 'nor-sdcard').split()])}"
FLASHLAYOUT_PARTITION_DEVICE_sdcard     = "none:${FLASHLAYOUT_PROGRAMMER_SECTIONS},${DEVICE_SDCARD}:default"

FLASHLAYOUT_PARTITION_OFFSET_${STM32MP_FSBL1_NAME}-boot = "0x0"
FLASHLAYOUT_PARTITION_OFFSET_${STM32MP_SSBL1_NAME}-boot = "0x0"
FLASHLAYOUT_PARTITION_OFFSET_emmc_${STM32MP_FSBL1_NAME} = "${DEVICE_START_OFFSET_BOOT0_EMMC}"
FLASHLAYOUT_PARTITION_OFFSET_emmc_${STM32MP_FSBL2_NAME} = "${DEVICE_START_OFFSET_BOOT1_EMMC}"
FLASHLAYOUT_PARTITION_OFFSET_emmc_${STM32MP_SSBL1_NAME} = "${DEVICE_START_OFFSET_EMMC}"

# Size defined in Kbytes
FLASHLAYOUT_PARTITION_SIZE_empty    = "0"

# Set name of FSBL and SSBL binaries loaded in RAM to program the devices
# and set volume file name for ubifs partition
# -----------------------------------------------------------------------------
# Use following pattern in binary name to expand to specific label config:
#       '<CONFIG>'     (to insert label from FLASHLAYOUT_CONFIG_LABELS - NB: substitution for all '-' in label by '_')
#       '<BOOTSCHEME>' (to insert label from FLASHLAYOUT_BOOTSCHEME_LABELS)
#       '<DEVICE>'     (to insert label  from FLASHLAYOUT_PARTITION_DEVICE)
#       '<TYPE>'       (to insert label  from FLASHLAYOUT_TYPE_LABELS)
# These patterns are processed to expand binary name for each config.
# -----------------------------------------------------------------------------
FLASHLAYOUT_PARTITION_BIN2LOAD_${STM32MP_FSBL1_NAME}-boot = "${@bb.utils.contains('MACHINE_FEATURES', 'fip', 'arm-trusted-firmware/tf-a-<TYPE>-usb.stm32', 'arm-trusted-firmware/tf-a-<TYPE>-serialboot.stm32', d)}"
FLASHLAYOUT_PARTITION_BIN2LOAD_${STM32MP_SSBL1_NAME}-boot = "${STM32MP_SSBL1_DATA}"
FLASHLAYOUT_PARTITION_BIN2LOAD_ubifs                      = "${IMAGE_LINK_NAME}_<CONFIG>_multivolume.ubi"

# -----------------------------------------------------------------------------
# Use the 'FLASHLAYOUT_PARTITION_REPLACE_PATTERNS' var to allow dynamic binary
# renaming for the bootloader binaries. This is only required for fsbl1-boot and
# ssbl-boot partitions that provides the binary to flash the device.
# The format to follow is:
#   '<PATTERN2REPLACE_1>;<PATTERN2SET_1> <PATTERN2REPLACE_2>;<PATTERN2SET_2>'
# And the pattern to replace in binary name is only searched as:
#   '-<PATTERN>$'
#   or
#   '-<PATTERN>-'
# -----------------------------------------------------------------------------
