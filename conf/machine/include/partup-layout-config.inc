PARTUP_PACKAGE_FILES = " \
    ${BOOTLOADER_FILE} \
    ${IMAGE_BOOT_FILES} \
    ${IMAGE_LINK_NAME}.ext4 \
"
PARTUP_PACKAGE_FILES:update += " \
    config-partition.tar.gz \
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

do_layout_config[vardeps] += " \
    BOOTLOADER_SKIP \
    BOOTLOADER_SEEK \
    BOOTLOADER_FILE \
"