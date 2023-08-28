do_image_wic[depends] += " \
    ${@oe.utils.ifelse(d.getVar('UBOOT_PROVIDES_BOOT_CONTAINER') == '0', '${IMAGE_BOOTLOADER}:do_deploy', '')} \
    phytec-bootenv:do_deploy \
"
