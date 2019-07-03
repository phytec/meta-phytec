do_image_wic[depends] += "\
    ${IMAGE_BOOTLOADER}:do_deploy \
    ${IMAGE_BOOTFILES_DEPENDS} \
"
