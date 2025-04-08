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
PREFERRED_PROVIDER_virtual/trusted-firmware-a:openstlinuxcommon = "tf-a-stm32mp"
PREFERRED_PROVIDER_virtual-optee-os = "optee-os-stm32mp"
VIRTUAL-RUNTIME_optee-os:openstlinuxcommon = "optee-os-stm32mp"

# =========================================================================
# boot device
# =========================================================================
# List of supported boot devices
BOOTDEVICE_LABELS ??= "emmc nor-emmc nand-2-256 nand-2-64 nor-sdcard sdcard"

# =========================================================================
# Machine settings
# =========================================================================
# Define list of devicetree per supported storage
STM32MP_DT_FILES_EMMC   += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'emmc', '${BOOTLOADER_DT}', '', d)}"
STM32MP_DT_FILES_SDCARD += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'sdcard', '${BOOTLOADER_DT}', '', d)}"

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
WIFI_LIST = "laird-sterling-firmware"
BLUETOOTH_LIST = "laird-sterling-firmware"

# =========================================================================
# Flashlayout configuration
# =========================================================================
ENABLE_FLASHLAYOUT_CONFIG_FWUP = "${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', '1', '0', d)}"

# =========================================================================
# Image
# =========================================================================
# Define image partition size (supposed to be set as max size in image recipe)

# Proposed value for rootfs should fit our highest constraint: NAND size (1GiB)
#   With fw-update we have the following partitions:
#       FSBL1 + FSBL2 + METADATA1+ METADATA2 + FIP-A1 + FIP-A2 + FIP-B1 + FIP-B2 + Multivolume UBI = NAND size
#       Multivolume UBI = 1GiB - (512KiB + 512KiB + 512KiB + 512KiB + 4MiB + 4MiB + 4MiB + 4MiB) = 1006MiB
# Without fw-update we have the following partitions:
#   FSBL1 + FIP + FIP2 + Multivolume UBI = NAND size
#   Multivolume UBI = 1GiB - (2MiB + 4MiB + 4MiB) = 1014MiB
# With multivolume UBI split:
#   Multivolume UBI > uboot_config + uboot_config_r + bootfs + vendorfs + rootfs + userfs + UBI Overhead
# From http://www.linux-mtd.infradead.org/doc/ubi.html#L_overhead, we compute
# the UBI overhead for our NAND:
#   With fw-update:
#       (20*4096/1024 + 4) * 256KiB + (256KiB - 248KiB) * (1006MiB/256KiB - 20*4096/1024 - 4) = 53024KiB
#   Without fw-update:
#       (20*4096/1024 + 4) * 256KiB + (256KiB - 248KiB) * (1014MiB/256KiB - 20*4096/1024 - 4) = 53280KiB
# In addition, for each UBIFS, our NAND consummed 9 extra eraseblocks
# So:
#   rootfs < Multivolume UBI - (uboot_config + uboot_config_r + bootfs + vendorfs + userfs + UBI Overhead + 4 * 9*eraseblocks)
#   With fw-update:
#       rootfs < 1006MiB - (256KiB + 256KiB + 64MiB + 16MiB + 128MiB + 53024KiB + 4 * 9 * 256KiB)
#       rootfs < 736.7MiB
#   Without fw-update:
#       rootfs < 1014MiB - (256KiB + 256KiB + 64MiB + 16MiB + 128MiB + 53280KiB + 4 * 9 * 256KiB)
#       rootfs < 744.5MiB
# Proposed value for rootfs is:
#   With fw-update   : 736MiB
#   Without fw-update: 744MiB
# Define max size for ROOTFS image being built to this value
STM32MP_ROOTFS_MAXSIZE_NAND ?= "${@bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '753664', '762336', d)}"
# And configure the IMAGE_ROOTFS_MAXSIZE variable accordingly
IMAGE_ROOTFS_MAXSIZE ?= "${STM32MP_ROOTFS_MAXSIZE_NAND}"

# For RAUC distro feature, redefine labels of the updatable primary partitions (A/B partitions system)
STM32MP_BOOTFS_LABEL:update   = "bootfs-a"
STM32MP_ROOTFS_LABEL:update   = "rootfs-a"
STM32MP_VENDORFS_LABEL:update = "vendorfs-a"

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
        bin_name = items[0] + bin_suffix + '.' + items[1] + '.' + fstype
    else:
        bin_name = items[0] + bin_suffix + '.' + items[1]
    # Set string for PARTITIONS_CONFIG item: <binary_name>,<partlabel>,<size>,<type>
    part_format = bin_name + ',' + items[1] + ',' + items[3] + ',' + items[4]

    if config in d.getVar('PARTITIONS_IMAGES_UPDATABLE'):
        part_b_label = items[1].replace('-a', '-b')
        part_format = part_format + ' ' + ',' + part_b_label + ',' + items[3] + ',' + items[4]

    return part_format

#########################################################################################################
# Define UBI labels to build
#   Naming rules for UBI partitions:
#      nand_<PageSize>_<BlockSize>
#      nor_<BlockSize>
#   Like that a same UBI partition can be used for severals NAND/NOR providers
MULTIUBI_BUILD += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-256', 'nand_2_256', '', d)}"
MULTIUBI_BUILD += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-64', 'nand_2_64', '', d)}"

# UBI Args for 512MB NAND soldered by default on phyCORE-STM32MP1
# Micron MT29F8G08ABACAWP
# LEB = BLOCK_SIZE - (2 * page size): 64*2048 - (2*2048)
MKUBIFS_ARGS:nand_2_256 = "--min-io-size 2048 --leb-size 126976 --max-leb-cnt 972 --space-fixup"
UBINIZE_ARGS:nand_2_256 = "--min-io-size 2048 --peb-size 128KiB"
EXTRA_UBIFS_SIZE:nand_2_256 = "2304"

# UBI Args for 128MB NAND soldered by default on phyCORE-STM32MP1
# Micron MT29F1G08ABAEAWP
# LEB = BLOCK_SIZE - (2 * page size): 64*2048 - (2*2048)
MKUBIFS_ARGS:nand_2_64 = "--min-io-size 2048 --leb-size 126976 --max-leb-cnt 972 --space-fixup"
UBINIZE_ARGS:nand_2_64 = "--min-io-size 2048 --peb-size 128KiB"
EXTRA_UBIFS_SIZE:nand_2_64 = "2304"

##############################################################################################################

# =========================================================================
# Kernel
# =========================================================================
# List of additional device tree to install
KERNEL_DEVICETREE:openstlinux += "${@' '.join('%s.dtb' % d for d in '${CUBE_M4_EXAMPLES_DT}'.split())}"
KERNEL_DEVICETREE:openstlinux += "${@' '.join('%s.dtb' % d for d in '${LINUX_A7_EXAMPLES_DT}'.split())}"

# =========================================================================
# U-boot
# =========================================================================
# With OpenSTlinux, UBOOT_DEVICETREE must be empty as it is internal for var expansion
UBOOT_DEVICETREE:openstlinuxcommon ?= ""

# =========================================================================
# trusted-firmware-a
# =========================================================================
# With OpenSTlinux, TF_A_DEVICETREE must be empty as it is internal to TF_A_CONFIG var expansion
TF_A_DEVICETREE:openstlinuxcommon ?= ""

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
