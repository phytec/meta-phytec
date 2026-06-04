# Provide a stable "fitImage-initramfs" alias for the kernel FIT image.
do_deploy:append() {
    if [ -n "${INITRAMFS_IMAGE}" ] && [ "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
        deploy_dir="${DEPLOYDIR}"
        if [ -n "${KERNEL_DEPLOYSUBDIR}" ]; then
            deploy_dir="${DEPLOYDIR}/${KERNEL_DEPLOYSUBDIR}"
        fi
        ln -snf fitImage "$deploy_dir/fitImage-initramfs"
    fi
}
