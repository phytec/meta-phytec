IMAGE_CMD_emmc_append () {
	if [ -e ${EMMCIMG} ]; then
		# copy the MLO to address 0x0 and keep the partition table
		dd if=${DEPLOY_DIR_IMAGE}/${BAREBOX_IPL_BIN_LINK_NAME} of=${EMMCIMG} bs=446 count=1 conv=notrunc
		dd if=${DEPLOY_DIR_IMAGE}/${BAREBOX_IPL_BIN_LINK_NAME} of=${EMMCIMG} skip=1 seek=1 conv=notrunc
		# copy the MLO to address 0x20000, 0x40000, 0x60000
		dd if=${DEPLOY_DIR_IMAGE}/${BAREBOX_IPL_BIN_LINK_NAME} of=${EMMCIMG} seek=768 bs=512 conv=notrunc
		dd if=${DEPLOY_DIR_IMAGE}/${BAREBOX_IPL_BIN_LINK_NAME} of=${EMMCIMG} seek=512 bs=512 conv=notrunc
		dd if=${DEPLOY_DIR_IMAGE}/${BAREBOX_IPL_BIN_LINK_NAME} of=${EMMCIMG} seek=256 bs=512 conv=notrunc
	fi
}
