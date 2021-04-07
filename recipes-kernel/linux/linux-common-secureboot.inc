inherit hab

SRC_URI_append_secureboot = " file://csf_additional_images_template.txt \
                            "
#value is not being set for mx8mp in meta-freescale, so set it here.
UBOOT_ENTRYPOINT_mx8mp="0x40480000"
LINUX_LOAD_ADDR="${UBOOT_ENTRYPOINT}"
#true for most mx8*.
DTB_LOAD_ADDR="0x48000000"

LINUX_IMAGE_PATH="${B}/${KERNEL_OUTPUT_DIR}/Image"
LINUX_IMAGE_PATH_SIGNED="${B}/${KERNEL_OUTPUT_DIR}/Image-signed"
DTB_PATH="${B}/${KERNEL_OUTPUT_DIR}/dts/${KERNEL_DEVICETREE}"

DEPENDS_append_secureboot = " nxp-cst-native"

CSF_TEMPLATE_PATH_ADDITIONAL="${WORKDIR}/csf_additional_images_template.txt"

python do_sign() {
    if "secureboot" in d.getVar('DISTRO_FEATURES', True):
        if oe.data.typed_value('FITIMAGE_SIGN', d):
            bb.note('Skipping signing of kernel and dtb as FIT image will'
                    ' be signed')
            return
        import shutil
        input_image_path = d.getVar('LINUX_IMAGE_PATH', True)
        output_image_path = d.getVar('LINUX_IMAGE_PATH_SIGNED', True)

        dtb_image_path = d.getVar('DTB_PATH', True)

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
        padding_fd = open(output_image_path, 'ab')
        current_pos = padding_fd.tell()
        padding_remaining = image_header_size - current_pos
        for i in range(0, padding_remaining):
            padding_fd.write(b'\x00')

        if padding_fd.tell() != image_header_size:
            bb.fatal('Error: Unexpected position of file descriptor while'
                     ' trying to sign Linux image')

        # generate ivt for padded image
        headermagic = 0x412000D1
        loadaddr = linux_addr
        # self ptr to the IVT. it follows the paddded image immediately, so we know the address easily
        selfptr = loadaddr+image_header_size
        # csf follows ivt immediately. ivt is always 32 bytes, so just add it
        csfptr = selfptr+32
        ivt = gen_ivt(headermagic, loadaddr, 0x0, 0x0, 0x0, selfptr, csfptr, 0x0)

        padding_fd.write(ivt)
        padding_fd.flush()
        padded_ivt_size = padding_fd.tell()  # size in bytes of our image

        dtb_size = os.stat(dtb_image_path).st_size

        # hab block linux
        linuxblock = dict()
        linuxblock['addr'] = linux_addr
        linuxblock['offset'] = 0x00
        linuxblock['size'] = padded_ivt_size
        linuxblock['filename'] = output_image_path

        # hab block fdt
        fdtblock = dict()
        fdtblock['addr'] = dtb_addr
        fdtblock['offset'] = 0x00
        fdtblock['size'] = dtb_size
        fdtblock['filename'] = dtb_image_path

        workdir = d.getVar('WORKDIR', True)

        csf_additional_images_path = os.path.join(workdir,
                                                  'csf_additional_images.txt')
        csf_additional_images_path_bin = os.path.join(workdir,
                                                      'csf_additional_images.bin.signed')

        template = readfull(d.getVar('CSF_TEMPLATE_PATH_ADDITIONAL', True))
        gen_csf(d, template, make_csf_hab_block([linuxblock, fdtblock]), csf_additional_images_path)

        if execcmd('cst -i {0} -o {1}'.format(
                                            csf_additional_images_path,
                                            csf_additional_images_path_bin)) != 0:
            raise Exception('Error: Failed to sign additional images binary')

        signed_additional_images_csf = readfull_bin(csf_additional_images_path_bin)
        padding_fd.write(signed_additional_images_csf)
        padding_fd.close()
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
