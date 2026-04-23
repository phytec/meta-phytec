do_deploy:append() {
    if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage"; then
        ln -snf fitImage-its-${KERNEL_FIT_NAME}.its "${DEPLOYDIR}/fitImage.its"

        if [ -n "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
            # deploy fitImage with initramfs as fitImage-initramfs
            ln -snf fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_NAME}${KERNEL_FIT_BIN_EXT} "${DEPLOYDIR}/fitImage-initramfs"
            ln -snf fitImage-its-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_NAME}.its "${DEPLOYDIR}/fitImage-initramfs.its"
        fi
    fi
}
