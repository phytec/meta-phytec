do_deploy:append() {
    if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage"; then
        if [ -n "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
            # create symlink to fitImage with initramfs
            ln -snf fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_NAME}${KERNEL_FIT_BIN_EXT} "${DEPLOYDIR}/fitImage-initramfs"
        fi
    fi
}
