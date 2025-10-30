#@DESCRIPTION: Common Machine additional configuration for STM32MP with OpenSTLinux Distro

include conf/machine/include/st-machine-common-stm32mp.inc

# Define the EULA file to use
EULA_FILE_ST:stm32mpcommon = "${PHYCORE_STM32MP_BASE}/conf/eula/${MACHINE}"
EULA_FILE_ST_MD5SUM:stm32mpcommon = "8b505090fb679839cefbcc784afe8ce9"

# =========================================================================
# PREFERRED_PROVIDER
# =========================================================================
PREFERRED_PROVIDER_virtual/kernel:openstlinuxcommon= "linux-stm32mp"
PREFERRED_PROVIDER_virtual/bootloader:openstlinuxcommon = "u-boot-stm32mp"
PREFERRED_PROVIDER_u-boot:openstlinuxcommon ??= "u-boot-stm32mp"
PREFERRED_PROVIDER_virtual/trusted-firmware-a:openstlinuxcommon = "tf-a-stm32mp"
PREFERRED_PROVIDER_virtual/trusted-firmware-m:openstlinuxcommon ??= "tf-m-stm32mp"
PREFERRED_PROVIDER_virtual-optee-os = "optee-os-stm32mp"
VIRTUAL-RUNTIME_optee-os:openstlinuxcommon = "optee-os-stm32mp"

# =========================================================================
# boot device
# =========================================================================
# List of supported boot devices
BOOTDEVICE_LABELS ??= "emmc nor-emmc nand-2-128-256 nor-sdcard sdcard"

# =========================================================================
# Machine settings
# =========================================================================
# Define list of devicetree per supported storage
STM32MP_DT_FILES_EMMC   += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'emmc', '${BOOTLOADER_DT}', '', d)}"
STM32MP_DT_FILES_SDCARD += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'sdcard', '${BOOTLOADER_DT}', '', d)}"
STM32MP_DT_FILES_NOR += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nor-emmc', '${BOOTLOADER_DT}', '', d)}"
STM32MP_DT_FILES_NAND += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-128-256', '${BOOTLOADER_DT}', '', d)}"

# Add list of default OpenSTLinux machine features to remove
MACHINE_FEATURES_REMOVE_LIST ?= ""
MACHINE_FEATURES:remove:openstlinuxcommon = "${MACHINE_FEATURES_REMOVE_LIST}"

# Remove firmware secure update feature by default
# Keep the feature only for RAUC
MACHINE_FEATURES_REMOVE_LIST += "fw-update"
MACHINE_FEATURES_REMOVE_LIST:remove:update = "fw-update"

# =========================================================================
# Machine specific packages
# =========================================================================
# Specify WIFI and Bluetooth firmwares to install
# For PEB-WLBT-05 expansions we use the Laird Sterling LWB firmware
WIFI_LIST = "ezurio-sterling-firmware"
BLUETOOTH_LIST = "ezurio-sterling-firmware"

# =========================================================================
# Flashlayout configuration
# =========================================================================
ENABLE_FLASHLAYOUT_CONFIG_FWUP = "${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', '1', '0', d)}"

# Workaround to fix warning when ENABLE_FLASHLAYOUT_CONFIG_FWUP is set to '0':
# Redefine "STM32MP_SSBL1_PROGRAMMER_NAME" to "None" instead of empty string
STM32MP_SSBL1_PROGRAMMER_NAME = "None"
PARTITIONS_PROGRAMMER_CONFIG = "\
    ${@ '${STM32MP_FSBL_PROGRAMMER_DATA},${STM32MP_FSBL_PROGRAMMER_NAME},,Binary' if '${STM32MP_FSBL_PROGRAMMER_NAME}' else ''} \
    ${@ '${STM32MP_SSBL1_PROGRAMMER_DATA},${STM32MP_SSBL1_PROGRAMMER_NAME},,FIP' if '${STM32MP_SSBL1_PROGRAMMER_NAME}' != 'None' else ''} \
    ${@ '${STM32MP_SSBL2_PROGRAMMER_DATA},${STM32MP_SSBL2_PROGRAMMER_NAME},,FIP' if '${STM32MP_SSBL2_PROGRAMMER_NAME}' else ''} \
    "

# For STM32MP2, redefine SSLB1_SIZE to 4096  in every case (2048K not sufficient)
STM32MP_SSBL1_SIZE:stm32mp2common ?= "4096"

# Redefine flashlayout config for NAND
STM32MP_FSBL1_SIZE_UBOOT = "512"
STM32MP_FSBL2_SIZE_UBOOT = "${STM32MP_FSBL1_SIZE_UBOOT}"

PARTITIONS_BOOTLOADER_CONFIG[nand-2-128-256] = "\
    ${@ '${STM32MP_FSBL1_DATA},${STM32MP_FSBL1_NAME},${STM32MP_FSBL1_SIZE_UBOOT},Binary,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_FSBL1_NAME}' else ''} \
    ${@ '${STM32MP_FSBL2_DATA},${STM32MP_FSBL2_NAME},${STM32MP_FSBL2_SIZE_UBOOT},Binary,1' if '${STM32MP_FSBL2_NAME}' and bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', False, True, d) else ''} \
    ${@ '${STM32MP_METADATA_DATA},${STM32MP_METADATA_NAME},${STM32MP_METADATA_SIZE_UBOOT},FWU_MDATA,2' if '${STM32MP_METADATA_NAME}' else ''} \
    ${@ '${STM32MP_SSBL1_DATA},${STM32MP_SSBL1_NAME},${STM32MP_SSBL1_SIZE},FIP,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_SSBL1_NAME}' else ''} \
    ${@ '${STM32MP_SSBL2_DATA},${STM32MP_SSBL2_NAME},${STM32MP_SSBL1_SIZE},FIP,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_SSBL2_NAME}' else ''} \
    "

FLASHLAYOUT_TYPE_LABELS:nand-2-128-256 = "${STM32MP_DT_FILES_NAND}"
FLASHLAYOUT_PARTITION_DEVICE:nand-2-128-256 = "none:${FLASHLAYOUT_PROGRAMMER_SECTIONS},${DEVICE:NAND}:default"

# Redefine flashlayout config for SDCARD (to be equal to eMMC config)
PARTITIONS_BOOTLOADER_CONFIG[sdcard] = "${@d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'emmc')}"

# No specific partition offset for NOR device
FLASHLAYOUT_PARTITION_OFFSET:nor-emmc:${STM32MP_FSBL1_NAME} = ""
FLASHLAYOUT_PARTITION_OFFSET:nor-emmc:${STM32MP_FSBL2_NAME} = ""

# For RAUC, decrease rootfs partition size to 1GiB
# Othewise default rootfs sizes are two big (4GiB for sdcard, 3GiB for eMMC)
# For A partition:
FLASHLAYOUT_PARTITION_SIZE:emmc:${STM32MP_ROOTFS_LABEL}:update = "1048576"
FLASHLAYOUT_PARTITION_SIZE:nor-emmc:${STM32MP_ROOTFS_LABEL}:update = "1048576"
FLASHLAYOUT_PARTITION_SIZE:sdcard:${STM32MP_ROOTFS_LABEL}:update = "1048576"
# For B partition:
STM32MP_ROOTFS_B_LABEL = "rootbfs"
FLASHLAYOUT_PARTITION_SIZE:emmc:${STM32MP_ROOTFS_B_LABEL}:update = "1048576"
FLASHLAYOUT_PARTITION_SIZE:nor-emmc:${STM32MP_ROOTFS_B_LABEL}:update = "1048576"
FLASHLAYOUT_PARTITION_SIZE:sdcard:${STM32MP_ROOTFS_B_LABEL}:update = "1048576"

# =========================================================================
# Image
# =========================================================================
# For RAUC distro feature, redefine labels of the updatable primary partitions (A/B partitions system)
STM32MP_BOOTFS_LABEL:update   = "bootafs"
STM32MP_ROOTFS_LABEL:update   = "rootafs"
STM32MP_VENDORFS_LABEL:update = "vendorafs"

# For RAUC distro feature, define the updatable partitions images
PARTITIONS_IMAGES_UPDATABLE ?= ""
PARTITIONS_IMAGES_UPDATABLE:update = "bootfs vendorfs rootfs"

# Redefine 'partImage2partConfig' function defined in 'flashlayout-stm32mp.bbclass'
# to add A/B partionning in flashlayout when using RAUC distro feature
def partImage2partConfig(d, config, fstype=None):
    """
    Convert PARTITIONS_IMAGES['config'] setting format to format expected to feed
    PARTITIONS_CONFIG[xxx].
    Manage <image_name> update respect to 'fstype' provided and apply the rootfs
    namming or standard partition image one.
    If the partition image is updatable, i.e 'config' is part of PARTITIONS_IMAGES_UPDATABLE
    add a secondary partition (B) to duplicate the primary partition (A),
    but without binary (empty partition).
        FROM: <image_name>,<partition_label>,<mountpoint>,<size>,<type>
        TO  : <binary_name>,<partition_label>,<size>,<type>
    """

    items = d.getVarFlag('PARTITIONS_IMAGES', config).split(',') or ""
    if len(items) != 5:
        bb.fatal('[partImage2partConfig] Wrong settings for PARTITIONS_IMAGES[%s] : %s' % (config, items))
    overrides = d.getVar('OVERRIDES')
    if items[2] == '' and 'openstlinuxcommon' not in overrides.split(':'):
        bin_suffix = '-${MACHINE}'
    else:
        bin_suffix = '-${DISTRO}-${MACHINE}'
    if fstype:
        if items[1][-2:] != 'fs':
            bin_name = items[0] + bin_suffix + '.' + items[1] + 'fs.' + fstype
        else:
            bin_name = items[0] + bin_suffix + '.' + items[1] + '.' + fstype
    else:
        bin_name = items[0] + bin_suffix + '.' + items[1]
    # Set string for PARTITIONS_CONFIG item: <binary_name>,<partlabel>,<size>,<type>
    part_format = bin_name + ',' + items[1] + ',' + items[3] + ',' + items[4]

    if config in d.getVar('PARTITIONS_IMAGES_UPDATABLE'):
        part_b_label = items[1].replace('afs', 'bfs')
        part_format = part_format + ' ' + ',' + part_b_label + ',' + items[3] + ',' + items[4]

    return part_format

# =========================================================================
# Kernel
# =========================================================================
# List of additional device tree to install
KERNEL_DEVICETREE:append:openstlinuxcommon = " ${@' '.join('%s.dtb' % d for d in '${CUBE_M4_EXAMPLES_DT}'.split())}"
KERNEL_DEVICETREE:append:openstlinuxcommon = " ${@' '.join('%s.dtb' % d for d in '${LINUX_A7_EXAMPLES_DT}'.split())}"

# =========================================================================
# U-boot
# =========================================================================
# With OpenSTlinux, UBOOT_DEVICETREE must be empty as it is internal for var expansion
UBOOT_DEVICETREE:openstlinuxcommon ?= ""

# Define u-boot defconfig for specific ST programmer binaries
# Use same defconfig as the default ones
UBOOT_CONFIG[programmer_stm32mp25] = "phyflex_stm32mp2_defconfig,,u-boot.dtb"
UBOOT_CONFIG[programmer_stm32mp15] = "phycore-stm32mp15_defconfig,,u-boot.dtb"
UBOOT_CONFIG[programmer_stm32mp13] = "phycore-stm32mp13_defconfig,,u-boot.dtb"

# =========================================================================
# u-boot extlinux configuration
# =========================================================================
# Enable other device tree examples when specified
LABEL_A7_EXAMPLES += "${@ 'rootfs-a7-examples' if '${LINUX_A7_EXAMPLES_DT}' else ''}"
LABEL_M4_EXAMPLES += "${@ 'rootfs-m4-examples' if '${CUBE_M4_EXAMPLES_DT}' else ''}"
UBOOT_EXTLINUX_LABELS:target-sdcard += "${LABEL_A7_EXAMPLES} ${LABEL_M4_EXAMPLES}"
UBOOT_EXTLINUX_LABELS:target-emmc += "${LABEL_A7_EXAMPLES} ${LABEL_M4_EXAMPLES}"
UBOOT_EXTLINUX_LABELS:target-nand += "${LABEL_A7_EXAMPLES} ${LABEL_M4_EXAMPLES}"
# Define MENU DESCRIPTION overrides for new A7 labels
UBOOT_EXTLINUX_MENU_DESCRIPTION:rootfs-a7-examples = "${@ '${LINUX_A7_EXAMPLES_DT}'[3:]}"
# Define FDT overrides for new A7 labels
UBOOT_EXTLINUX_FDT:rootfs-a7-examples = "${@ '${LINUX_A7_EXAMPLES_DT}.dtb'[2:]}"
UBOOT_EXTLINUX_MENU_DESCRIPTION:rootfs-m4-examples = "${@ '${CUBE_M4_EXAMPLES_DT}'[3:]}"
# Define FDT overrides for new M4 labels
UBOOT_EXTLINUX_FDT:rootfs-m4-examples = "${@ '${CUBE_M4_EXAMPLES_DT}.dtb'[2:]}"

# =========================================================================
# trusted-firmware-a
# =========================================================================
# With OpenSTlinux, TF_A_DEVICETREE must be empty as it is internal to TF_A_CONFIG var expansion
TF_A_DEVICETREE:openstlinuxcommon ?= ""

# Enable FIP and TF-A config for NOR-EMMC boot
FIP_CONFIG += "${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', bb.utils.contains('BOOTDEVICE_LABELS', 'nor-emmc', 'optee-nor', '', d), '', d)}"
TF_A_CONFIG += "${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', bb.utils.contains('BOOTDEVICE_LABELS', 'nor-emmc', 'optee-nor', '', d), '', d)}"

# Enable FIP and TF-A config for NAND boot
FIP_CONFIG += "${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-128-256', 'optee-nand', '', d), '', d)}"
TF_A_CONFIG += "${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-128-256', 'optee-nand', '', d), '', d)}"

# =========================================================================
# WIC for sdcard raw image
# =========================================================================
WIC_CREATE_EXTRA_ARGS:openstlinuxcommon = "--no-fstab-update"
WKS_FILE_DEPENDS:openstlinuxcommon ?= " \
    virtual/bootloader \
    virtual/trusted-firmware-a \
    ${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', 'virtual-optee-os', '', d)} \
    ${STM32MP_BOOTFS_IMAGE} \
    ${@bb.utils.contains('ST_VENDORFS', '1', '${STM32MP_VENDORFS_IMAGE}', '', d)} \
    ${@bb.utils.contains('ST_USERFS', '1', '${STM32MP_USERFS_IMAGE}', '', d)} \
"

# If optee is part of the selected boot chains, consider optee by default.
# This default value (BOOTSCHEME) can be used for example for the WIC file generation or rauc bundle build.
BOOTSCHEME = "${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', 'optee', 'trusted', d)}"

WKS_FILES:openstlinuxcommon ?= "stm32mp1-sdimage-openstlinux.wks.in"
