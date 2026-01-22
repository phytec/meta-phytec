PARTUP_ROOTFS_IMAGE_EXT_DEFAULT = "tar.gz"
PARTUP_ROOTFS_IMAGE_EXT_DEFAULT:update = "ext4"
PARTUP_ROOTFS_IMAGE_EXT ?= "${PARTUP_ROOTFS_IMAGE_EXT_DEFAULT}"
PARTUP_ROOT_FS ?= "${@bb.utils.contains('PARTUP_ROOTFS_IMAGE_EXT', 'tar.gz', 'ext4', 'null', d)}"
PARTUP_PACKAGE_FILES = " \
    ${BOOTLOADER_FILE} \
    ${PARTUP_BOOT_FILES} \
    ${IMAGE_LINK_NAME}.${PARTUP_ROOTFS_IMAGE_EXT} \
"
PARTUP_PACKAGE_DEPENDS ?= " \
    virtual/kernel \
    virtual/bootloader \
"

PARTUP_PACKAGE_FILES:append:update = " config-partition.ext4"
PARTUP_PACKAGE_DEPENDS:append:update = " config-partition"
PARTUP_PACKAGE_FILES:append:rauc-appfs = " rauc-appfs-example.tar.gz"
PARTUP_PACKAGE_DEPENDS:append:rauc-appfs = " rauc-appfs-example"

PARTUP_PACKAGE_DEPENDS:append:mx7-generic-bsp = " \
    phytec-bootenv \
"

PARTUP_PACKAGE_DEPENDS:append:mx8m-nxp-bsp = " \
    ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '0', '${IMAGE_BOOTLOADER}', '')} \
    phytec-bootenv \
"
PARTUP_PACKAGE_DEPENDS:append:mx9-generic-bsp = " \
    ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '0', '${IMAGE_BOOTLOADER}', '')} \
    phytec-bootenv \
"
PARTUP_PACKAGE_DEPENDS:append:mx93-nxp-bsp = " \
    imx-m33-demos \
"

PARTUP_PACKAGE_DEPENDS:append:j721s2 = " \
    phytec-bootenv \
"

PARTUP_LAYOUT_CONFIG ?= "layout-default.yaml"
PARTUP_LAYOUT_CONFIG:k3 ?= "layout-default-k3.yaml"
PARTUP_LAYOUT_CONFIG:update ?= "layout-rauc.yaml"
PARTUP_LAYOUT_CONFIG:update:k3 ?= "layout-rauc-k3.yaml"

PARTUP_SECTIONS += "clean appfs partitions"
PARTUP_SECTION_clean = "${@bb.utils.contains('PREFERRED_PROVIDER_virtual/bootloader', 'barebox', False, True, d)}"
PARTUP_SECTION_appfs = "${@bb.utils.contains('DISTRO_FEATURES', 'rauc-appfs', True, False, d)}"
PARTUP_SECTION_partitions = "1"

PARTUP_BOOT_FILES ??= "${IMAGE_BOOT_FILES}"
PARTUP_ARRAYS += "image_boot_files"
PARTUP_ARRAY_image_boot_files = "${PARTUP_BOOT_FILES}"

BOOTLOADER_SKIP ??= "${@bb.utils.contains('PREFERRED_PROVIDER_virtual/bootloader', 'barebox', 1, 0, d)}"
BOOTLOADER_SEEK ??= "1"
BOOTLOADER_FILE ??= "u-boot.bin"
BOOTLOADER_FILE:k3 ?= ""
BOOTLOADER_FILE:mx6-generic-bsp ?= "${BAREBOX_BINARY}"
BOOTLOADER_FILE:mx6ul-generic-bsp ?= "${BAREBOX_BINARY}"
BOOTLOADER_FILE:mx7-generic-bsp ?= "u-boot-with-spl.imx"
BOOTLOADER_FILE:mx8m-generic-bsp ?= "imx-boot"
BOOTLOADER_FILE:mx9-generic-bsp ?= "imx-boot"
UBOOT_ENV_OFFSET ??= "3840kiB"
UBOOT_ENV_OFFSET:k3 ?= "6656kiB"
UBOOT_ENV_OFFSET:mx9-generic-bsp ?= "7168kiB"
UBOOT_ENV_OFFSET_REDUND ??= "3968kiB"
UBOOT_ENV_OFFSET_REDUND:k3 ?= "6912kiB"
UBOOT_ENV_OFFSET_REDUND:mx91-generic-bsp ?= "7296kiB"
UBOOT_ENV_OFFSET_REDUND:mx93-generic-bsp ?= "7296kiB"
UBOOT_ENV_OFFSET_REDUND:mx95-generic-bsp ?= "7184kiB"
BOOT_PART_OFFSET ??= "4MiB"
BOOT_PART_OFFSET:mx9-generic-bsp ?= "8MiB"

do_layout_config[vardeps] += " \
    BOOTLOADER_SKIP \
    BOOTLOADER_SEEK \
    BOOTLOADER_FILE \
    UBOOT_ENV_OFFSET \
    UBOOT_ENV_OFFSET_REDUND \
    BOOT_PART_OFFSET \
"
