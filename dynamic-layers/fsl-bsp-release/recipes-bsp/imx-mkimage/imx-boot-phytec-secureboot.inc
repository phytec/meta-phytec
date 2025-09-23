# Signs imx-boot images with NXP's CST tool

inherit hab

SRC_URI:append:secureboot=" \
                          file://csf_spl_template.txt \
                          file://csf_fit_template.txt"
DEPENDS:append:secureboot = " nxp-cst-native phytec-dev-ca-native"

CSF_TEMPLATE_PATH_SPL="${WORKDIR}/csf_spl_template.txt"
CSF_TEMPLATE_PATH_FIT="${WORKDIR}/csf_fit_template.txt"

BL31_BIN_PATH="${BOOT_STAGING}/bl31.bin"
BL32_BIN_PATH="${BOOT_STAGING}/tee.bin"


# Hardcoded adddresses taken from imx-boot
UBOOT_NODTB_BIN_ADDR="INVALID"
ATF_BIN_ADDR="INVALID"
TEE_BIN_ADDR="INVALID"

UBOOT_NODTB_BIN_ADDR:mx8m-nxp-bsp="0x40200000"
ATF_BIN_ADDR:mx8m-nxp-bsp="0x910000"

UBOOT_NODTB_BIN_ADDR:mx8mm-nxp-bsp="0x40200000"
ATF_BIN_ADDR:mx8mm-nxp-bsp="0x920000"

UBOOT_NODTB_BIN_ADDR:mx8mn-nxp-bsp="0x40200000"
ATF_BIN_ADDR:mx8mn-nxp-bsp="0x960000"

UBOOT_NODTB_BIN_ADDR:mx8mp-nxp-bsp="0x40200000"
ATF_BIN_ADDR:mx8mp-nxp-bsp="0x970000"

TEE_BIN_ADDR:mx8mm-nxp-bsp="0x56000000"
TEE_BIN_ADDR:mx8mn-nxp-bsp="0x56000000"
TEE_BIN_ADDR:mx8mp-nxp-bsp="0x56000000"

# Extract the addresses we need from imx-mkimage output
def get_hab_blocks_from_log(input_image_path, ubootimx_make_output):
    import re
    content = readfull(ubootimx_make_output)
    spl = re.search(r'spl hab block:\s+(.*)', content).group(1).split()

    spl_hab_block = dict()
    spl_hab_block['addr'] = int(spl[0], 16)
    spl_hab_block['offset'] = int(spl[1], 16)
    spl_hab_block['size'] = int(spl[2], 16)
    spl_hab_block['filename'] = input_image_path

    sld = re.search(r'sld hab block:\s+(.*)', content).group(1).split()
    sld_hab_block = dict()
    sld_hab_block['addr'] = int(sld[0], 16)
    sld_hab_block['offset'] = int(sld[1], 16)
    sld_hab_block['size'] = int(sld[2], 16)
    sld_hab_block['filename'] = input_image_path

    csf_spl_offset = int(re.search(r'csf_off\s+(.*)', content).group(1), 16)
    csf_fit_offset = int(re.search(r'sld_csf_off\s+(.*)', content).group(1), 16)

    result = dict()
    result['SPL_HAB_BLOCK'] = spl_hab_block
    result['SLD_HAB_BLOCK'] = sld_hab_block

    result['CSF_SPL_OFFSET'] = csf_spl_offset
    result['CSF_FIT_OFFSET'] = csf_fit_offset

    return result


def sign_hab(d, input_image_path, output_image_path, imxboot_logfile, \
             uboot_nodtb_bin_path, uboot_dtb_bin_path, bl31_bin_path, bl32_bin_path):
    import shutil
    uboot_nodtb_bin_addr = d.getVar('UBOOT_NODTB_BIN_ADDR')
    atf_bin_addr = d.getVar('ATF_BIN_ADDR')

    if uboot_nodtb_bin_addr == 'INVALID' or atf_bin_addr == 'INVALID':
        bb.fatal('Error: Required addresses for selected device are unknown.'
                 'Cannot sign')

    uboot_nodtb_bin_addr = int(uboot_nodtb_bin_addr, 16)
    atf_bin_addr = int(atf_bin_addr, 16)

    parsed_blocks = get_hab_blocks_from_log(input_image_path, imxboot_logfile)
    csf_template_path_spl = d.getVar('CSF_TEMPLATE_PATH_SPL')
    csf_template_path_fit = d.getVar('CSF_TEMPLATE_PATH_FIT')

    spl_hab_block = parsed_blocks['SPL_HAB_BLOCK']
    sld_hab_block = parsed_blocks['SLD_HAB_BLOCK']

    # generate csf for SPL
    template_spl = readfull(csf_template_path_spl)
    workdir = d.getVar('WORKDIR')

    csf_spl_txt_path = os.path.join(workdir, 'csf_spl.txt')
    csf_fit_txt_path = os.path.join(workdir, 'csf_fit.txt')

    csf_spl_bin_path = os.path.join(workdir, 'csf_fit.bin.signed')
    csf_fit_bin_path = os.path.join(workdir, 'csf_spl.bin.signed')

    gen_csf(d, template_spl, make_csf_hab_block([spl_hab_block]),
            os.path.join(workdir, csf_spl_txt_path))

    # generate csf for fit
    ubootblock = dict()
    ubootblock['filename'] = uboot_nodtb_bin_path
    ubootblock['size'] = os.stat(ubootblock['filename']).st_size
    ubootblock['addr'] = uboot_nodtb_bin_addr
    ubootblock['offset'] = 0x00

    # u-boot dtb for fit
    dtbblock = dict()
    dtbblock['filename'] = uboot_dtb_bin_path
    dtbblock['size'] = os.stat(dtbblock['filename']).st_size
    dtbblock['addr'] = ubootblock['addr']+ubootblock['size']
    dtbblock['offset'] = 0x00

    # ATF for fit
    atfblock = dict()
    atfblock['filename'] = bl31_bin_path
    atfblock['size'] = os.stat(atfblock['filename']).st_size
    atfblock['addr'] = atf_bin_addr
    atfblock['offset'] = 0x0

    hablist = [sld_hab_block, ubootblock, dtbblock, atfblock]

    # TEE is optional
    if os.path.isfile(bl32_bin_path):
        tee_bin_addr = d.getVar('TEE_BIN_ADDR')
        if tee_bin_addr == 'INVALID':
            bb.fatal('Error: Signed TEE not supported for this device (address not known)')
        tee_bin_addr = int(tee_bin_addr, 16)
        # TEE for fit
        teeblock = dict()
        teeblock['filename'] = bl32_bin_path
        teeblock['size'] = os.stat(teeblock['filename']).st_size
        teeblock['addr'] = tee_bin_addr
        teeblock['offset'] = 0x0

        hablist.append(teeblock)

    template_fit = readfull(csf_template_path_fit)
    gen_csf(d, template_fit, make_csf_hab_block(hablist), csf_fit_txt_path)

    # sign images
    if not cst_sign(d, csf_spl_txt_path, csf_spl_bin_path):
        raise Exception('Error: Failed to sign SPL binary')
    if not cst_sign(d, csf_fit_txt_path, csf_fit_bin_path):
        raise Exception('Error: Failed to sign FIT binary')

    signed_spl_csf = readfull_bin(csf_spl_bin_path)
    signed_fit_csf = readfull_bin(csf_fit_bin_path)

    if not shutil.copyfile(input_image_path, output_image_path):
        print('Failed to copy image')
        return False

    # Create signed flash binary
    output_fd = open(output_image_path, 'r+b')
    output_fd.seek(parsed_blocks['CSF_SPL_OFFSET'])
    output_fd.write(signed_spl_csf)
    output_fd.seek(parsed_blocks['CSF_FIT_OFFSET'])
    output_fd.write(signed_fit_csf)
    output_fd.close()

    image_name = os.path.basename(input_image_path)
    json_path = os.path.join(workdir, 'hab_info')
    if not os.path.exists(json_path):
        os.mkdir(json_path)
    json_file_path = os.path.join(json_path, f'hab_info_{image_name}.json')
    store_resign_info(json_file_path,
       parsed_blocks['CSF_SPL_OFFSET'], parsed_blocks['CSF_FIT_OFFSET'],
       [spl_hab_block], hablist, template_spl, template_fit, output_image_path)


python do_sign() {
    if "secureboot" in d.getVar('DISTRO_FEATURES'):
        imxboot_targets = d.getVar('IMXBOOT_TARGETS').split()
        uboot_configs = d.getVar('UBOOT_CONFIG').split()

        workdir = d.getVar('WORKDIR')
        source_dir = d.getVar('S')
        boot_name = d.getVar('BOOT_NAME')
        machine = d.getVar('MACHINE')
        soc_target = d.getVar('SOC_TARGET')
        boot_staging = d.getVar('BOOT_STAGING')
        uboot_dtb_name = d.getVar('UBOOT_DTB_NAME')
        deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE')
        boot_tools = d.getVar('BOOT_TOOLS')
        bl31_bin_path = d.getVar('BL31_BIN_PATH')
        bl32_bin_path = d.getVar('BL32_BIN_PATH')
        for target in imxboot_targets:
            for uboot_config in uboot_configs:
                img_name = '{0}-{1}-{2}.bin-{3}'.format(boot_name, machine,
                                                        uboot_config, target)
                make_name = 'make_output_{0}_{1}_{2}.log'.format(soc_target,
                                                                 target,
                                                                 uboot_config)
                input_image_path = os.path.join(source_dir, img_name)
                output_image_path = input_image_path + '-signed'
                imxboot_logfile = os.path.join(workdir, make_name)
                uboot_nodtb_bin_path = os.path.join(boot_staging,
                                                    'u-boot-nodtb.bin-' +
                                                    uboot_config)
                uboot_dtb_bin_path = os.path.join(deploy_dir_image, boot_tools, uboot_dtb_name)
                sign_hab(d, input_image_path, output_image_path,
                         imxboot_logfile, uboot_nodtb_bin_path,
                         uboot_dtb_bin_path, bl31_bin_path, bl32_bin_path)
    else:
        bb.note("Signing skipped - non secureboot build")
}

do_deploy:append:secureboot() {
 for target in ${IMXBOOT_TARGETS}; do
        #Use first "target" as IMAGE_IMXBOOT_TARGET
        if [ "${IMAGE_IMXBOOT_TARGET}" = "" ]; then
            IMAGE_IMXBOOT_TARGET="$target"
            echo "Set boot target as $IMAGE_IMXBOOT_TARGET"
        fi
        for type in ${UBOOT_CONFIG}; do
            sourcefile="${S}/${BOOT_NAME}-${MACHINE}-${type}.bin-${target}-signed"
            if [ -f "${sourcefile}" ]; then
                install -m 0644 "${sourcefile}" "${DEPLOYDIR}/"
                ln -sf "${BOOT_NAME}-${MACHINE}-${type}.bin-${IMAGE_IMXBOOT_TARGET}-signed" \
                                         		    "${DEPLOYDIR}/${BOOT_NAME}"
            fi
        done
 done

 install -m 0644 ${WORKDIR}/hab_info/* ${DEPLOYDIR}/
}

addtask sign after do_compile before do_deploy
