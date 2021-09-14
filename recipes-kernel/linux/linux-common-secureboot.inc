inherit hab

#value is not being set for mx8mp in meta-freescale, so set it here.
UBOOT_ENTRYPOINT_mx8mp="0x40480000"
LINUX_LOAD_ADDR="${UBOOT_ENTRYPOINT}"
#true for most mx8*.
DTB_LOAD_ADDR="0x48000000"

LINUX_IMAGE_PATH="${B}/${KERNEL_OUTPUT_DIR}/Image"
LINUX_IMAGE_PATH_SIGNED="${B}/${KERNEL_OUTPUT_DIR}/Image-signed"
DTBS_PATH="${B}/${KERNEL_OUTPUT_DIR}/dts/"

DEPENDS_append_secureboot = " nxp-cst-native"

python do_sign() {
    if "secureboot" in d.getVar('DISTRO_FEATURES', True):
        if oe.data.typed_value('FITIMAGE_SIGN', d):
            bb.note('Skipping signing of kernel and dtb as FIT image will'
                    ' be signed')
            return
        import shutil
        input_image_path = d.getVar('LINUX_IMAGE_PATH', True)
        output_image_path = d.getVar('LINUX_IMAGE_PATH_SIGNED', True)

        first_dtb_name = d.getVar('KERNEL_DEVICETREE', True).split()[0]
        dtb_image_path = os.path.join(d.getVar('DTBS_PATH', True), first_dtb_name)

        linux_addr = int(d.getVar('LINUX_LOAD_ADDR'), 16)
        dtb_addr = int(d.getVar('DTB_LOAD_ADDR'), 16)

        kernelimgtype = d.getVar('KERNEL_IMAGETYPE', True)
        if kernelimgtype != 'Image':
            bb.fatal('Image type not supported for secureboot')

        if os.path.exists(output_image_path):
            os.remove(output_image_path)
        if not shutil.copyfile(input_image_path, output_image_path):
            bb.fatal('Failed to copy Linux image while trying to create signature')

        image_header_size = get_linux_image_size(input_image_path)
        dtb_size = os.stat(dtb_image_path).st_size
        # hab block dtb
        dtbblock = dict()
        dtbblock['addr'] = dtb_addr
        dtbblock['offset'] = 0x00
        dtbblock['size'] = dtb_size
        dtbblock['filename'] = dtb_image_path

        sign_inplace(d, output_image_path, image_header_size, linux_addr, [dtbblock])
}

do_deploy_append_secureboot() {
    if [ -f "${LINUX_IMAGE_PATH_SIGNED}" ] ; then
        base_name="${imageType}-${KERNEL_IMAGE_NAME}-signed"
        install -m 0644 "${LINUX_IMAGE_PATH_SIGNED}" "${DEPLOYDIR}/${base_name}.bin"
        symlink_name="${imageType}-${KERNEL_IMAGE_LINK_NAME}-signed"
        ln -sf "${base_name}.bin" "$deployDir/${symlink_name}.bin"
        ln -sf "${base_name}.bin" "$deployDir/${imageType}-signed"
    fi
}

addtask sign after do_compile before do_deploy
