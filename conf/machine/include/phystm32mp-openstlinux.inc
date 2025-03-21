#@DESCRIPTION: Common Machine additional configuration for STM32MP with OpenSTLinux Distro

require conf/machine/include/phystm32mp-openstlinux-extlinux-config.inc
require conf/machine/include/phystm32mp-openstlinux-flashlayout.inc
require conf/machine/include/phystm32mp-openstlinux-flashlayout-deleteall.inc
require conf/machine/include/phystm32mp-openstlinux-storage-device.inc

include conf/machine/include/st-machine-features-stm32mp.inc
include conf/machine/include/st-machine-providers-stm32mp.inc

# Define the EULA file to use
EULA_FILE_ST:stm32mpcommon = "${PHYCORE_STM32MP_BASE}/conf/eula/${MACHINE}"
EULA_FILE_ST_MD5SUM:stm32mpcommon = "8b505090fb679839cefbcc784afe8ce9"

# =========================================================================
# boot scheme
# =========================================================================
# List of supported boot schemes
BOOTSCHEME_LABELS ??= "optee"

# =========================================================================
# boot device
# =========================================================================
# List of supported boot devices
BOOTDEVICE_LABELS ??= "emmc nor-emmc nand-2-256 nand-2-64 nor-sdcard sdcard"

# =========================================================================
# Machine settings
# =========================================================================
# Default machine feature
MACHINE_FEATURES:openstlinuxcommon += "tpm2"

# Configure autoresize for any of the 'ext4' storage devices (through InitRD image)
MACHINE_FEATURES:openstlinuxcommon += "${@bb.utils.contains_any('BOOTDEVICE_LABELS',  ['emmc', 'sdcard'], 'autoresize', '', d)} "

# Enable installation of INITRD image on bootfs side
MACHINE_FEATURES:openstlinuxcommon += "initrd"
# Remove InitRD package install from DISTRO_EXTRA_RRECOMMENDS to add it explicitly
# in our bootfs image (this avoid dependency loops as rootfs depends on bootfs...)
DISTRO_EXTRA_RRECOMMENDS:remove = " ${@bb.utils.contains('COMBINED_FEATURES', 'initrd', '${INITRD_PACKAGE}', '', d)} "


# =========================================================================
# Machine specific packages
# =========================================================================
# Enable Software watchdog when sysvinit
MACHINE_EXTRA_RRECOMMENDS:append = " ${@bb.utils.contains('DISTRO_FEATURES','sysvinit',' watchdog ','',d)} "


# Specify WIFI and Bluetooth firmwares to install
# For PEB-WLBT-05 expansions we use the Laird Sterling LWB firmware
WIFI_LIST += "laird-sterling-firmware"
BLUETOOTH_LIST += "laird-sterling-firmware"

# =========================================================================
# Bootloader raw partition configuration : data, label, size (Kbytes)
# =========================================================================
# -----------------------------------------------------------------------------
# NOTE: There are few restrictions to follow:
#       -   The partition for the first boot loader should follow the naming
#           rule: fsbl*
#       -   The partition for the secondary boot loader should follow the naming
#           rule: fip
# -----------------------------------------------------------------------------
ENABLE_FLASHLAYOUT_CONFIG_FWUP ?= "${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', '1', '0', d)}"
# Keep the binary copy configuration for legacy flashlayout file style
ENABLE_FLASHLAYOUT_PARTITION_BINCOPY = "${@bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '0', '1', d)}"

STM32MP_FSBL_PROGAMMER_NAME ?= "fsbl-boot"
STM32MP_SSBL_PROGAMMER_NAME ?= "fip-boot"

STM32MP_FSBL1_DATA ?= "arm-trusted-firmware/tf-a-<TYPE>-<DEVICE>.stm32"
STM32MP_FSBL1_NAME ?= "${@bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', 'fsbl', 'fsbl1', d)}"
STM32MP_FSBL1_SIZE ?= "256"
STM32MP_FSBL2_DATA ?= "${STM32MP_FSBL1_DATA}"
STM32MP_FSBL2_NAME ?= "fsbl2"
STM32MP_FSBL2_SIZE ?= "${STM32MP_FSBL1_SIZE}"
STM32MP_SSBL1_DATA ?= "fip/fip-<TYPE>-<BOOTSCHEME>${FIP_SIGN_SUFFIX}.bin"
STM32MP_SSBL1_NAME ?= "${@bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', 'fip-a', 'fip', d)}"
STM32MP_SSBL1_SIZE ?= "4096"
STM32MP_SSBL2_DATA ?= "${@bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '', '${STM32MP_SSBL1_DATA}', d)}"
STM32MP_SSBL2_NAME ?= "${@bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', 'fip-b', 'fip2', d)}"
STM32MP_SSBL2_SIZE ?= "${STM32MP_SSBL1_SIZE}"
STM32MP_UENV_DATA ?= ""
STM32MP_UENV_NAME ?= "u-boot-env"
STM32MP_UENV_SIZE ?= "512"
STM32MP_METADATA_DATA ?= "${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', 'arm-trusted-firmware/${TF_A_METADATA_BINARY}', '', d)}"
STM32MP_METADATA_NAME ?= "${@bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', 'metadata', '', d)}"
STM32MP_METADATA_SIZE ?= "256"

# Specific override for NAND device type regarding partition sizes to follow
# the hard coded configuration on U-Boot source code
STM32MP_FSBL1_SIZE_UBOOT ?= "${@bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '512', '1024', d)}"
STM32MP_METADATA_SIZE_UBOOT ?= "512"

# Bootloader Partitions configuration
PARTITIONS_BOOTLOADER_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'emmc', 'emmc', '', d)}"
PARTITIONS_BOOTLOADER_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-256', 'nand-2-256', '', d)}"
PARTITIONS_BOOTLOADER_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-64', 'nand-2-64', '', d)}"
PARTITIONS_BOOTLOADER_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nor-emmc', 'nor-emmc', '', d)}"
PARTITIONS_BOOTLOADER_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'sdcard', 'sdcard', '', d)}"

# <binary_name>,<partlabel>,<size>,<type>,<copy>
PARTITIONS_BOOTLOADER_CONFIG[emmc] ?= "\
    ${@ '${STM32MP_FSBL1_DATA},${STM32MP_FSBL1_NAME},${STM32MP_FSBL1_SIZE},Binary,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_FSBL1_NAME}' else ''} \
    ${@ '${STM32MP_FSBL2_DATA},${STM32MP_FSBL2_NAME},${STM32MP_FSBL2_SIZE},Binary,1' if '${STM32MP_FSBL2_NAME}' and bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', False, True, d) else ''} \
    ${@ '${STM32MP_METADATA_DATA},${STM32MP_METADATA_NAME},${STM32MP_METADATA_SIZE},Binary,2' if '${STM32MP_METADATA_NAME}' else ''} \
    ${@ '${STM32MP_SSBL1_DATA},${STM32MP_SSBL1_NAME},${STM32MP_SSBL1_SIZE},FIP,1' if '${STM32MP_SSBL1_NAME}' else ''} \
    ${@ '${STM32MP_SSBL2_DATA},${STM32MP_SSBL2_NAME},${STM32MP_SSBL2_SIZE},FIP,1' if '${STM32MP_SSBL2_NAME}' and bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', True, False, d) else ''} \
    ${@ '${STM32MP_UENV_DATA},${STM32MP_UENV_NAME},${STM32MP_UENV_SIZE},Binary,1' if '${STM32MP_UENV_NAME}' else ''} \
    "
PARTITIONS_BOOTLOADER_CONFIG[nand-2-256] ?= "\
    ${@ '${STM32MP_FSBL1_DATA},${STM32MP_FSBL1_NAME},${STM32MP_FSBL1_SIZE_UBOOT},Binary,2' if '${STM32MP_FSBL1_NAME}' else ''} \
    ${@ '${STM32MP_METADATA_DATA},${STM32MP_METADATA_NAME},${STM32MP_METADATA_SIZE_UBOOT},Binary,2' if '${STM32MP_METADATA_NAME}' else ''} \
    ${@ '${STM32MP_SSBL1_DATA},${STM32MP_SSBL1_NAME},${STM32MP_SSBL1_SIZE},FIP,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_SSBL1_NAME}' else ''} \
    ${@ '${STM32MP_SSBL2_DATA},${STM32MP_SSBL2_NAME},${STM32MP_SSBL2_SIZE},FIP,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_SSBL2_NAME}' else ''} \
    "
PARTITIONS_BOOTLOADER_CONFIG[nand-2-64] ?= "\
    ${@ '${STM32MP_FSBL1_DATA},${STM32MP_FSBL1_NAME},${STM32MP_FSBL1_SIZE_UBOOT},Binary,2' if '${STM32MP_FSBL1_NAME}' else ''} \
    ${@ '${STM32MP_METADATA_DATA},${STM32MP_METADATA_NAME},${STM32MP_METADATA_SIZE_UBOOT},Binary,2' if '${STM32MP_METADATA_NAME}' else ''} \
    ${@ '${STM32MP_SSBL1_DATA},${STM32MP_SSBL1_NAME},${STM32MP_SSBL1_SIZE},FIP,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_SSBL1_NAME}' else ''} \
    ${@ '${STM32MP_SSBL2_DATA},${STM32MP_SSBL2_NAME},${STM32MP_SSBL2_SIZE},FIP,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_SSBL2_NAME}' else ''} \
    "

PARTITIONS_BOOTLOADER_CONFIG[nor-emmc] ?= "\
    ${@ '${STM32MP_FSBL1_DATA},${STM32MP_FSBL1_NAME},${STM32MP_FSBL1_SIZE},Binary,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_FSBL1_NAME}' else ''} \
    ${@ '${STM32MP_FSBL2_DATA},${STM32MP_FSBL2_NAME},${STM32MP_FSBL2_SIZE},Binary,1' if '${STM32MP_FSBL2_NAME}' and bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', False, True, d) else ''} \
    ${@ '${STM32MP_METADATA_DATA},${STM32MP_METADATA_NAME},${STM32MP_METADATA_SIZE},Binary,2' if '${STM32MP_METADATA_NAME}' else ''} \
    ${@ '${STM32MP_SSBL1_DATA},${STM32MP_SSBL1_NAME},${STM32MP_SSBL1_SIZE},FIP,1' if '${STM32MP_SSBL1_NAME}' else ''} \
    ${@ '${STM32MP_SSBL2_DATA},${STM32MP_SSBL2_NAME},${STM32MP_SSBL2_SIZE},FIP,1' if '${STM32MP_SSBL2_NAME}' and bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', True, False, d) else ''} \
    ${@ '${STM32MP_UENV_DATA},${STM32MP_UENV_NAME},${STM32MP_UENV_SIZE},Binary,1' if '${STM32MP_UENV_NAME}' else ''} \
    "
PARTITIONS_BOOTLOADER_CONFIG[sdcard] ?= "\
    ${@ '${STM32MP_FSBL1_DATA},${STM32MP_FSBL1_NAME},${STM32MP_FSBL1_SIZE},Binary,' + bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', '2', '1', d) if '${STM32MP_FSBL1_NAME}' else ''} \
    ${@ '${STM32MP_FSBL2_DATA},${STM32MP_FSBL2_NAME},${STM32MP_FSBL2_SIZE},Binary,1' if '${STM32MP_FSBL2_NAME}' and bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', False, True, d) else ''} \
    ${@ '${STM32MP_METADATA_DATA},${STM32MP_METADATA_NAME},${STM32MP_METADATA_SIZE},Binary,2' if '${STM32MP_METADATA_NAME}' else ''} \
    ${@ '${STM32MP_SSBL1_DATA},${STM32MP_SSBL1_NAME},${STM32MP_SSBL1_SIZE},FIP,1' if '${STM32MP_SSBL1_NAME}' else ''} \
    ${@ '${STM32MP_SSBL2_DATA},${STM32MP_SSBL2_NAME},${STM32MP_SSBL2_SIZE},FIP,1' if '${STM32MP_SSBL2_NAME}' and bb.utils.contains('ENABLE_FLASHLAYOUT_CONFIG_FWUP', '1', True, False, d) else ''} \
    ${@ '${STM32MP_UENV_DATA},${STM32MP_UENV_NAME},${STM32MP_UENV_SIZE},Binary,1' if '${STM32MP_UENV_NAME}' else ''} \
    "

# =========================================================================
# Image
# =========================================================================
# For sparse, align on 4096b
IMAGE_ROOTFS_ALIGNMENT:openstlinuxcommon ?= "4"

# Enable licence summary and configure License content generation
ENABLE_IMAGE_LICENSE_SUMMARY ?= "1"

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

# Partitions configuration
IMAGE_CLASSES:openstlinuxcommon += "st-partitions-image"

# Enable use of extra partition(s)
ST_BOOTFS   ?= "1"
ST_VENDORFS ?= "1"
ST_USERFS   ?= "1"

PARTITIONS_IMAGES += "${@bb.utils.contains('ST_BOOTFS', '1', 'bootfs', '', d)}"
PARTITIONS_IMAGES += "${@bb.utils.contains('ST_VENDORFS', '1', 'vendorfs', '', d)}"
PARTITIONS_IMAGES += "rootfs"
PARTITIONS_IMAGES += "${@bb.utils.contains('ST_USERFS', '1', 'userfs', '', d)}"

# Define image to use for extra partitions
STM32MP_BOOTFS_IMAGE      ?= "st-image-bootfs"
STM32MP_BOOTFS_LABEL      ?= "bootfs"
STM32MP_BOOTFS_MOUNTPOINT ?= "/boot"
# Proposed value for bootfs is 64MB
STM32MP_BOOTFS_SIZE       ?= "65536"
STM32MP_ROOTFS_IMAGE      ?= "${IMAGE_BASENAME}"
STM32MP_ROOTFS_LABEL      ?= "rootfs"
# Configure the rootfs size with IMAGE_ROOTFS_MAXSIZE variable
STM32MP_ROOTFS_SIZE       ?= "${IMAGE_ROOTFS_MAXSIZE}"
STM32MP_USERFS_IMAGE      ?= "st-image-userfs"
STM32MP_USERFS_LABEL      ?= "userfs"
STM32MP_USERFS_MOUNTPOINT ?= "/usr/local"
# Proposed value for userfs is 128MB
STM32MP_USERFS_SIZE         ?= "131072"
STM32MP_VENDORFS_IMAGE      ?= "st-image-vendorfs"
STM32MP_VENDORFS_LABEL      ?= "vendorfs"
STM32MP_VENDORFS_MOUNTPOINT ?= "/vendor"
# Proposed value for vendorfs is 16MB
STM32MP_VENDORFS_SIZE       ?= "16384"

# <image_name>,<partition_label>,<mountpoint>,<size>,<type>
PARTITIONS_IMAGES[bootfs]   ?= "${STM32MP_BOOTFS_IMAGE},${STM32MP_BOOTFS_LABEL},${STM32MP_BOOTFS_MOUNTPOINT},${STM32MP_BOOTFS_SIZE},System"
PARTITIONS_IMAGES[vendorfs] ?= "${STM32MP_VENDORFS_IMAGE},${STM32MP_VENDORFS_LABEL},${STM32MP_VENDORFS_MOUNTPOINT},${STM32MP_VENDORFS_SIZE},FileSystem"
PARTITIONS_IMAGES[rootfs]   ?= "${STM32MP_ROOTFS_IMAGE},${STM32MP_ROOTFS_LABEL},,${STM32MP_ROOTFS_SIZE},FileSystem"
PARTITIONS_IMAGES[userfs]   ?= "${STM32MP_USERFS_IMAGE},${STM32MP_USERFS_LABEL},${STM32MP_USERFS_MOUNTPOINT},${STM32MP_USERFS_SIZE},FileSystem"

# =========================================================================
# Image partition configuration : data, label, size (Kbytes)
# =========================================================================
STM32MP_UBIFS_DATA ?= "${IMAGE_LINK_NAME}_<CONFIG>_multivolume.ubi"
STM32MP_UBIFS_NAME ?= "ubifs"
STM32MP_UBIFS_SIZE ?= ""

# Partitions configuration
PARTITIONS_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'emmc', 'emmc', '', d)}"
PARTITIONS_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-256', 'nand-2-256', '', d)}"
PARTITIONS_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-64', 'nand-2-64', '', d)}"
PARTITIONS_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nor-emmc', 'nor-emmc', '', d)}"
PARTITIONS_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'sdcard', 'sdcard', '', d)}"

PARTITIONS_SUFFIX ?= "ext4"

# For RAUC distro feature, redefine labels of the updatable primary partitions (A/B partitions system)
STM32MP_BOOTFS_LABEL:update   = "bootfs-a"
STM32MP_ROOTFS_LABEL:update   = "rootfs-a"
STM32MP_VENDORFS_LABEL:update = "vendorfs-a"

# For RAUC distro feature, define the updatable partitions images
PARTITIONS_IMAGES_UPDATABLE ?= ""
PARTITIONS_IMAGES_UPDATABLE:update = "bootfs vendorfs rootfs"

# Redefine 'partImage2partConfig' function defined in 'flashlayout-stm32mp.bbclass'
# to add A/B partionning in flashlayout when using RAUC distro feature
def partImage2partConfig(config, fstype, d):
    """
    Convert PARTITIONS_IMAGES['config'] setting format to format expected to feed
    PARTITIONS_CONFIG[xxx].
    Manage <image_name> update respect to 'fstype' provided and apply the rootfs
    namming or standard partition image one.
    If the partition image is updatable, i.e 'config' is part of PARTITIONS_IMAGES_UPDATABLE
    add a secondary partition (B) to duplicate the primary partition (A),
    but without binary (empty partition).
        FROM: <image_name>,<partition_label>,<mountpoint>,<size>,<type>
        TO  : <binary_name>,<partition_label>,<size>,<type> [,<partition_b_label>,<size>,<type>]
    """

    items = d.getVarFlag('PARTITIONS_IMAGES', config).split(',') or ""
    if len(items) != 5:
        bb.fatal('Wrong settings for PARTITIONS_IMAGES[%s] : %s' % (config, items))
    if items[2] != '':
        bin_name = items[0] + '-${DISTRO}-${MACHINE}' + '.' + fstype
    else:
        bin_name = items[0] + '-${MACHINE}' + '.' + fstype
    # Set string for PARTITIONS_CONFIG item: <binary_name>,<partlabel>,<size>,<type>
    part_format = bin_name + ',' + items[1] + ',' + items[3] + ',' + items[4]

    if config in d.getVar('PARTITIONS_IMAGES_UPDATABLE'):
        part_b_label = items[1].replace('-a', '-b')
        part_format = part_format + ' ' + ',' + part_b_label + ',' + items[3] + ',' + items[4]

    return part_format

# Partition configuration
# <binary_name>,<partlabel>,<size>,<type>,<copy>
PARTITIONS_CONFIG_MMC ?= "\
    ${@' '.join(['%s' % partImage2partConfig(config, '${PARTITIONS_SUFFIX}', d) for config in d.getVar('PARTITIONS_IMAGES').split()])} \
    "
PARTITIONS_CONFIG_NAND ?= "\
    ${STM32MP_UBIFS_DATA},${STM32MP_UBIFS_NAME},${STM32MP_UBIFS_SIZE},System,1 \
    "

PARTITIONS_CONFIG[emmc] ?= "${PARTITIONS_CONFIG_MMC}"
PARTITIONS_CONFIG[nand-2-256] ?= "${PARTITIONS_CONFIG_NAND}"
PARTITIONS_CONFIG[nand-2-64] ?= "${PARTITIONS_CONFIG_NAND}"
PARTITIONS_CONFIG[nor-emmc] ?= "${PARTITIONS_CONFIG_MMC}"
PARTITIONS_CONFIG[sdcard] ?= "${PARTITIONS_CONFIG_MMC}"

# UBI Configuration
IMAGE_CLASSES:openstlinuxcommon += "image_types-stubi"

# Define two empty volumes to manage U-Boot config beginning of multivolume UBIFS
STM32MP_UBI_VOLUME:prepend = "uboot_config:256:empty uboot_config_r:256:empty "

# Define UBI volume label to use in kernel command line to mount UBI file system
UBI_VOLNAME ?= "rootfs"

# Manage specific override for UBI bootfs label name to 'boot' (hardcoded on U-Boot)
UBI_VOLNAME:pn-${STM32MP_BOOTFS_IMAGE} = "boot"

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

# Define specific EXT4 command line:
#   - Create minimal inode number (as it is done by default in image_types.bbclass)
#   - Add label name (maximum length of the volume label is 16 bytes)
#     So use IMAGE_NAME_SUFFIX name by removing the '.' and truncing to 16 caracters
#   - Deactivate metadata_csum and dir_index (hashed b-trees): update not supported
#     by U-Boot
EXTRA_IMAGECMD:ext4:openstlinuxcommon = "-i 4096 -L ${@d.getVar('IMAGE_NAME_SUFFIX').replace('.', '', 1)[:16]} -O ^metadata_csum,^dir_index"

# Allow debug on the platform with gdb and openocd tools
EXTRA_IMAGEDEPENDS:append:openstlinuxcommon = " \
    gdb-cross-${TARGET_ARCH} \
    openocd-stm32mp-native \
    stm32wrapper4dbg-native \
    sdcard-raw-tools-native \
    "

# Make sure to provide all expected tools in SDK
ST_TOOLS_FOR_SDK = " \
    nativesdk-gcc-arm-none-eabi \
    nativesdk-binutils \
    nativesdk-openocd-stm32mp \
    nativesdk-stm32wrapper4dbg \
    nativesdk-sdcard-raw-tools \
    nativesdk-ncurses-libncursesw \
    "

# Add tools to build native application with SDK
# Currently mandatory for kernel and u-boot
ST_TOOLS_FOR_SDK:append = " \
    nativesdk-cpp-symlinks \
    nativesdk-gcc-symlinks \
    nativesdk-libyaml \
    nativesdk-libyaml-dev \
    "

# For support of perl ansicolor
ST_TOOLS_FOR_SDK_PERL = " \
    nativesdk-perl-module-term-ansicolor \
    nativesdk-perl-module-encode \
    nativesdk-perl-module-encode-encoding \
    nativesdk-perl-module-encode-mime-header \
"

# For some scripts in kernel source code
ST_TOOLS_FOR_SDK_PERL:append = " \
    nativesdk-perl-module-file-spec-functions \
    nativesdk-perl-module-integer \
    nativesdk-perl-module-pod-text \
"

ST_TOOLS_FOR_SDK:append = " ${ST_TOOLS_FOR_SDK_PERL} "

# For support of python module for u-boot
ST_TOOLS_FOR_SDK:append = " \
    nativesdk-python3-dev \
    nativesdk-python3-setuptools \
    nativesdk-swig \
    nativesdk-dtc \
    nativesdk-flex \
    "

# For support of python module for optee-os
ST_TOOLS_FOR_SDK:append = " \
    nativesdk-python3-pyelftools \
    nativesdk-python3-pycryptodomex \
    nativesdk-python3-pillow \
    nativesdk-python3-cffi \
    nativesdk-python3-pycparser \
    nativesdk-python3-cryptography \
"

# For support of string convertion (iconv) in SDK
ST_TOOLS_FOR_SDK:append = " \
    nativesdk-glibc-gconv-utf-16 \
    nativesdk-glibc-gconv-utf-32 \
    "
# For support wayland-scanner in SDK
ST_TOOLS_FOR_SDK:append = " \
    nativesdk-wayland \
    "

# For CMSIS-SVD file parser with GDB
ST_TOOLS_FOR_SDK:append = " \
    nativesdk-svd-tools \
    "

# For Trusted Application development using OP-TEE TA SDK
ST_TOOLS_FOR_SDK:append = " \
    nativesdk-optee-sdk \
    "

# Fip tool
ST_TOOLS_FOR_SDK:append = " \
    nativesdk-${FIPTOOL_WRAPPER} \
    "

# Make sure to append mkimage to SDK for kernel uImage build
ST_DEPENDENCIES_BUILD_FOR_SDK = " \
    ${@bb.utils.contains('KERNEL_IMAGETYPE', 'uImage', 'nativesdk-u-boot-mkimage', '', d)} \
    ${@bb.utils.contains('KERNEL_ALT_IMAGETYPE', 'uImage', 'nativesdk-u-boot-mkimage', '', d)} \
  "
# Make sure to append openssl to SDK for kernel-module and scripts build
ST_DEPENDENCIES_BUILD_FOR_SDK:append = " nativesdk-openssl-dev "

# Make sure to append bison to SDK for u-boot build
ST_DEPENDENCIES_BUILD_FOR_SDK:append = " \
    ${@bb.utils.contains('EXTRA_IMAGEDEPENDS', 'u-boot-stm32mp', 'nativesdk-bison', '', d)} \
  "

# for populate_sdk, we will have all the tools
TOOLCHAIN_HOST_TASK:append:openstlinuxcommon = " ${ST_TOOLS_FOR_SDK} "
TOOLCHAIN_HOST_TASK:append:openstlinuxcommon = " ${ST_DEPENDENCIES_BUILD_FOR_SDK} "
# for populate_sdk_ext, the tools are not desired as mandatory tools (aka basic
# tools for devtool)
TOOLCHAIN_HOST_TASK:remove:task-populate-sdk-ext:openstlinuxcommon = " ${ST_TOOLS_FOR_SDK} "
TOOLCHAIN_HOST_TASK:remove:task-populate-sdk-ext:openstlinuxcommon = " ${ST_DEPENDENCIES_BUILD_FOR_SDK} "
# buildtools is used only by populate_sdk_ext
# populate_sdk_ext = buildtools + uninative + layer
# buildtools: sdk part of esdk (like sdk generated by populate_sdk)
# uninative: basic tools for devtool
TOOLCHAIN_HOST_TASK:remove:pn-buildtools-tarball:openstlinuxcommon = " ${ST_TOOLS_FOR_SDK_PERL} "

TOOLCHAIN_TARGET_TASK:openstlinuxcommon += " bash-dev libgomp-dev"
TOOLCHAIN_TARGET_TASK:remove:pn-buildtools-tarball:openstlinuxcommon = " bash-dev libgomp-dev"

# =========================================================================
# Debug trace
# =========================================================================
# acitvate/desactive the debug and trace on boot stage: tf-a, optee, u-boot and kernel
ST_DEBUG_TRACE ?= "1"

# =========================================================================
# Kernel
# =========================================================================
# Maxsize authorized for uncompressed kernel binary
# Define to null to skip kernel image size check
KERNEL_IMAGE_MAXSIZE:openstlinuxcommon ?= ""

# Enable kernel config file deploy
KERNEL_CONFIG_DEPLOY ?= "1"

# List of additional device tree to install
KERNEL_DEVICETREE:openstlinux += "${@' '.join('%s.dtb' % d for d in '${CUBE_M4_EXAMPLES_DT}'.split())}"
KERNEL_DEVICETREE:openstlinux += "${@' '.join('%s.dtb' % d for d in '${LINUX_A7_EXAMPLES_DT}'.split())}"

# Define the devicetree for Linux A7 examples
LINUX_A7_EXAMPLES_DT ?= ""

# =========================================================================
# u-boot
# =========================================================================
# Define U-boot splashscreen file naming
UBOOT_SPLASH_PORTRAIT_IMAGE = "splash_portrait"
UBOOT_SPLASH_LANDSCAPE_IMAGE = "splash_landscape"

# Enable MTDPART check for UBOOT_CONFIG
UBOOT_MTDPART_CHECK_ENABLE ?= ""
# Set U-Boot MTD partition configurations
UBOOT_MTDPART_NAND_2_256_BOOT ?= "${@','.join(['%sk(%s)' % (align_size(d, 'NAND', l.split(',')[2], l.split(',')[4]), l.split(',')[1]) for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nand-2-256').split()])}"
UBOOT_MTDPART_NAND_2_64_BOOT  ?= "${@','.join(['%sk(%s)' % (align_size(d, 'NAND', l.split(',')[2], l.split(',')[4]), l.split(',')[1]) for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nand-2-64').split()])}"
UBOOT_MTDPART_NOR_BOOT        ?= "${@','.join(['%sk(%s)' % (align_size(d, 'NOR', l.split(',')[2], l.split(',')[4]), l.split(',')[1]) for l in d.getVarFlag('PARTITIONS_BOOTLOADER_CONFIG', 'nor-emmc').split()])}"

UBOOT_MTDPART_CHECK ?= "\
    ${@bb.utils.contains_any('BOOTDEVICE_LABELS', 'nand-2-256' , 'CONFIG_MTDPARTS_NAND0_BOOT=${UBOOT_MTDPART_NAND_2_256_BOOT}', '', d)} \
    ${@bb.utils.contains_any('BOOTDEVICE_LABELS', 'nand-2-64', 'CONFIG_MTDPARTS_NAND0_BOOT=${UBOOT_MTDPART_NAND_2_64_BOOT}', '', d)} \
    ${@bb.utils.contains('BOOTDEVICE_LABELS', 'nor-emmc', 'CONFIG_MTDPARTS_NOR0_BOOT=${UBOOT_MTDPART_NOR_BOOT}', '', d)} \
    "

MACHINE_EXTRA_RRECOMMENDS:append:openstlinuxcommon = " \
    u-boot-fw-config-stm32mp \
"
ST_UBOOT_DEBUG_TRACE ?= "${@bb.utils.contains('ST_DEBUG_TRACE', '1', '1', '0', d)}"

# =========================================================================
# trusted-firmware-a
# =========================================================================
# Configure trusted-firmware-a build

# With OpenSTlinux, TF_A_DEVICETREE must be empty as it is internal to TF_A_CONFIG var expansion
TF_A_DEVICETREE:openstlinuxcommon ?= ""

TF_A_CONFIG += "${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', 'optee', '', d)}"
TF_A_CONFIG += "${@bb.utils.contains('BOOTSCHEME_LABELS', 'trusted', 'trusted', '', d)}"
# Manage specific config settings
TF_A_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'emmc', 'emmc', '', d)}"
TF_A_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-256', 'nand', '', d)}"
TF_A_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nand-2-64', 'nand', '', d)}"
TF_A_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'nor-emmc', 'nor', '', d)}"
TF_A_CONFIG += "${@bb.utils.contains('BOOTDEVICE_LABELS', 'sdcard', 'sdcard', '', d)}"
TF_A_CONFIG += "uart"
TF_A_CONFIG += "usb"

# Append SSP config to TF_A_CONFIG
TF_A_SSP_ENABLE ?= "0"
TF_A_CONFIG += "${@bb.utils.contains('TF_A_SSP_ENABLE', '1', 'uart-ssp usb-ssp', '', d)}"

# Default configuration for signing trusted-firmware-a binary
TF_A_SIGN_ENABLE ?= "0"

# Configure TF-A to build the metadata binary
TF_A_ENABLE_METADATA ?= "${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', '1', '0', d)}"
TF_A_METADATA_BINARY ?= "metadata.bin"

# Configure the default MTD_START_OFFSET
TF_A_MTD_START_OFFSET_NAND    ?= "0x00200000"
TF_A_MTD_START_OFFSET_NOR     ?= "${@bb.utils.contains('MACHINE_FEATURES', 'fw-update', '0x00100000', '0x00080000', d)}"

ST_TF_A_DEBUG_TRACE ?= "${@bb.utils.contains('ST_DEBUG_TRACE', '1', '1', '0', d)}"

# =========================================================================
# optee
# =========================================================================
ST_OPTEE_DEBUG_TRACE ?= "${@bb.utils.contains('ST_DEBUG_TRACE', '1', '1', '0', d)}"

# =========================================================================
# fip
# =========================================================================
# Configure fip build
FIP_CONFIG += "${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', 'optee', '', d)}"
FIP_CONFIG += "${@bb.utils.contains('BOOTSCHEME_LABELS', 'trusted', 'trusted', '', d)}"

# Define config for each FIP_CONFIG
FIP_CONFIG[optee]   ?= "optee,${FIP_DEVICETREE_OPTEE}"
FIP_CONFIG[trusted] ?= "tfa,${FIP_DEVICETREE_TRUSTED}"

# List of device tree to use for fip binary creation
FIP_DEVICETREE_OPTEE   ?= "${STM32MP_DEVICETREE}"
FIP_DEVICETREE_TRUSTED ?= "${STM32MP_DEVICETREE}"

# Define fiptool wrapper name
FIPTOOL_WRAPPER ?= "fiptool-stm32mp"

# Manage specific config to sign FIP
FIP_SIGN_ENABLE ?= "${@bb.utils.contains('TF_A_SIGN_ENABLE', '1', '1', '', d)}"
FIP_SIGN_SUFFIX ?= "${@bb.utils.contains('FIP_SIGN_ENABLE', '1', '_Signed', '', d)}"

# =========================================================================
# Xserver
# =========================================================================
XSERVER:openstlinuxcommon ?= " \
    xserver-xorg \
    xserver-xorg-module-libint10 \
    xf86-input-evdev \
    xf86-video-modesetting \
"

# =========================================================================
# sysdig
# =========================================================================
PREFERRED_PROVIDER_sysdig:openstlinuxcommon = "sysdig-stm32mp"

# =========================================================================
# M4 copro
# =========================================================================
# Define the devicetree for M4 examples
CUBE_M4_EXAMPLES_DT ?= ""

# Define the name of default copro firmware executed @boot time
# This name is cherry picked from list defined in m4projects-stm32mp1.bb
DEFAULT_COPRO_FIRMWARE ?= "OpenAMP_TTY_echo"

# Define M4 example installation dir
M4_INSTALLDIR = "${STM32MP_USERFS_MOUNTPOINT}"
M4_PACKAGE_4USERFS = "${@bb.utils.contains('ST_USERFS','1','1','0',d)}"

# =========================================================================
# GCNANO userland configuration
# =========================================================================
# Configure use of vendorfs if enable to install gcnano libs
GCNANO_USERLAND_OUTPUT_LIBDIR = "${@bb.utils.contains('ST_VENDORFS', '1', '${STM32MP_VENDORFS_MOUNTPOINT}/lib', '${libdir}', d)}"

# =========================================================================
# WIC for sdcard raw image
# =========================================================================
WIC_CREATE_EXTRA_ARGS:openstlinuxcommon = "--no-fstab-update"
WKS_FILE_DEPENDS:openstlinuxcommon ?= " \
    virtual/bootloader \
    virtual/trusted-firmware-a \
    ${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', 'virtual/optee-os', '', d)} \
    ${STM32MP_BOOTFS_IMAGE} \
    ${@bb.utils.contains('ST_VENDORFS', '1', '${STM32MP_VENDORFS_IMAGE}', '', d)} \
    ${@bb.utils.contains('ST_USERFS', '1', '${STM32MP_USERFS_IMAGE}', '', d)} \
"

# If optee is part of the selected boot chains, consider optee by default.
# This default value (BOOTSCHEME) can be used for example for the WIC file generation or rauc bundle build.
BOOTSCHEME = "${@bb.utils.contains('BOOTSCHEME_LABELS', 'optee', 'optee', 'trusted', d)}"

WKS_FILES:openstlinuxcommon ?= "stm32mp1-sdimage-openstlinux.wks.in"
