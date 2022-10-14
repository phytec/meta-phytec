BAREBOX_BINARY ??= "barebox.bin"
WKS_BOOTIMAGESIZE ??= "20"

WICVARS_append = " BAREBOX_BINARY BOOTLOADER_SEEK WKS_BOOTIMAGESIZE"

do_image_wic[depends] += "\
    dosfstools-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    virtual/kernel:do_deploy \
    virtual/bootloader:do_deploy \
"

IMAGE_CMD_wic_append () {
	ln -fs "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.wic" "$out${IMAGE_NAME_SUFFIX}.sdcard"
	ln -fs "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.sdcard" "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.sdcard"
}
