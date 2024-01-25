PARTUP_PACKAGE_FILES = " \
    ${BOOTLOADER_FILE} \
    ${IMAGE_BOOT_FILES} \
    ${IMAGE_LINK_NAME}.ext4 \
"
PARTUP_PACKAGE_FILES:append:update = " config-partition.tar.gz"
PARTUP_PACKAGE_DEPENDS:append:update = " config-partition"

PARTUP_PACKAGE_DEPENDS:append:mx8m-nxp-bsp = " \
    ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '0', '${IMAGE_BOOTLOADER}', '')} \
    phytec-bootenv \
"

PARTUP_PACKAGE_DEPENDS:append:mx8m-mainline-bsp = " \
    virtual/kernel \
    virtual/bootloader \
"

PARTUP_LAYOUT_CONFIG = "partup-layout-default.yaml"
PARTUP_LAYOUT_CONFIG:update = "partup-layout-rauc.yaml"

PARTUP_SECTIONS += "clean"
PARTUP_SECTION_clean = "${@bb.utils.contains('PREFERRED_PROVIDER_virtual/bootloader', 'barebox', False, True, d)}"

PARTUP_ARRAYS += "image_boot_files"
PARTUP_ARRAY_image_boot_files = "${IMAGE_BOOT_FILES}"

BOOTLOADER_SKIP ??= "${@bb.utils.contains('PREFERRED_PROVIDER_virtual/bootloader', 'barebox', 1, 0, d)}"
BOOTLOADER_SEEK ??= "1"
BOOTLOADER_FILE ??= "u-boot.bin"
BOOTLOADER_FILE:k3 ?= "${SPL_BINARY}"
BOOTLOADER_FILE:mx6-generic-bsp ?= "${BAREBOX_BINARY}"
BOOTLOADER_FILE:mx6ul-generic-bsp ?= "${BAREBOX_BINARY}"
BOOTLOADER_FILE:mx8m-generic-bsp ?= "imx-boot"
BOOTLOADER_FILE:mx93-generic-bsp ?= "imx-boot"
UBOOT_ENV_OFFSET ??= "3840kiB"
UBOOT_ENV_OFFSET:mx93-generic-bsp ?= "7168kiB"
UBOOT_ENV_OFFSET_REDUND ??= "3968kiB"
UBOOT_ENV_OFFSET_REDUND:mx93-generic-bsp ?= "7296kiB"
BOOT_PART_OFFSET ??= "4MiB"
BOOT_PART_OFFSET:mx93-generic-bsp ?= "8MiB"

do_layout_config[vardeps] += " \
    BOOTLOADER_SKIP \
    BOOTLOADER_SEEK \
    BOOTLOADER_FILE \
"
