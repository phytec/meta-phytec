# This is a helper class for creating patitioned images with WIC for sd cards and emmc
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
	mv "$out${IMAGE_NAME_SUFFIX}.wic" "$out${IMAGE_NAME_SUFFIX}.sdcard"
	ln -fs "${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.sdcard" "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.sdcard"
}

IMAGE_CMD_emmc () {
	if [ -e ${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.sdcard ]; then
		SDIMG=${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.sdcard
	else
		if [ -e ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.sdcard ]; then
			SDIMG=`readlink -f ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.sdcard`
		else
			SDIMG=${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.wic
		fi
	fi
	EMMCIMG=${IMAGE_NAME}.rootfs.emmc
	cp ${SDIMG} ${IMGDEPLOYDIR}/${EMMCIMG}

	ln -sf ${EMMCIMG} ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.emmc
}

IMAGE_TYPEDEP_emmc = "wic"

do_image_emmc[depends] += " \
    parted-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    dosfstools-native:do_populate_sysroot \
    e2fsprogs-native:do_populate_sysroot \
    virtual/kernel:do_deploy \
    virtual/bootloader:do_deploy \
    virtual/prebootloader:do_deploy \
"
