do_image_wic[depends] += "\
    ${IMAGE_BOOTLOADER}:do_deploy \
    phytec-bootenv:do_deploy \
"
