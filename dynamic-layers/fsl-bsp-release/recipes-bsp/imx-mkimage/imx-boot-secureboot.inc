inherit ahab

BUILD_SIGN_DIR = "${WORKDIR}/build-sign"

python do_sign() {
    if 'secureboot' not in d.getVar('DISTRO_FEATURES'):
        return

    import subprocess
    import shutil
}

python do_sign:append:mx91-generic-bsp() {
    logfile = os.path.join(d.getVar('WORKDIR'), 'temp/log.do_compile')

    target = 'u-boot-atf-container.img'
    image_path = os.path.join(d.getVar('BOOT_STAGING'), target)
    signed_image_path = os.path.join(d.getVar('BOOT_STAGING'), f'{target}-signed')
    offset_ch, offset_sb = csf_get_offsets_from_file(target, logfile)
    ahab_sign(image_path, signed_image_path, offset_ch, offset_sb, d)

    bb.note(f'Copying signed ATF container {signed_image_path} to {image_path}')
    shutil.copy2(signed_image_path, image_path)

    flashbin = 'flash.bin'
    image_path = os.path.join(d.getVar('BOOT_STAGING'), flashbin)
    for target in d.getVar('IMXBOOT_TARGETS').split():
        bb.note(f'Signing for target \'{target}\'')
        signed_image_path = d.expand(f'${{S}}/${{BOOT_CONFIG_MACHINE}}-{target}-signed')
        make_process = subprocess.run(['make', '-C', d.getVar('S'),
                d.expand('SOC=${IMX_BOOT_SOC_TARGET}'), target],
                stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        offset_ch, offset_sb = csf_get_offsets_from_string(flashbin,
                make_process.stdout.decode('utf-8'))
        ahab_sign(image_path, signed_image_path, offset_ch, offset_sb, d)
}

python do_sign:append:mx93-generic-bsp() {
    logfile = os.path.join(d.getVar('WORKDIR'), 'temp/log.do_compile')

    target = 'u-boot-atf-container.img'
    image_path = os.path.join(d.getVar('BOOT_STAGING'), target)
    signed_image_path = os.path.join(d.getVar('BOOT_STAGING'), f'{target}-signed')
    offset_ch, offset_sb = csf_get_offsets_from_file(target, logfile)
    ahab_sign(image_path, signed_image_path, offset_ch, offset_sb, d)

    bb.note(f'Copying signed ATF container {signed_image_path} to {image_path}')
    shutil.copy2(signed_image_path, image_path)

    flashbin = 'flash.bin'
    image_path = os.path.join(d.getVar('BOOT_STAGING'), flashbin)
    for target in d.getVar('IMXBOOT_TARGETS').split():
        bb.note(f'Signing for target \'{target}\'')
        signed_image_path = d.expand(f'${{S}}/imx-boot${{BOOT_VARIANT}}-${{MACHINE}}-${{UBOOT_CONFIG}}.bin-{target}-signed')
        make_process = subprocess.run(['make', '-C', d.getVar('S'),
                d.expand('SOC=${IMX_BOOT_SOC_TARGET}'), target],
                stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        offset_ch, offset_sb = csf_get_offsets_from_string(flashbin,
                make_process.stdout.decode('utf-8'))
        ahab_sign(image_path, signed_image_path, offset_ch, offset_sb, d)
}

do_sign[dirs] = "${BUILD_SIGN_DIR}"
do_sign[cleandirs] = "${BUILD_SIGN_DIR}"
addtask sign after do_compile before do_install

do_install:append:secureboot() {
    for target in ${IMXBOOT_TARGETS}; do
        install -m 0644 ${S}/imx-boot${BOOT_VARIANT}-${MACHINE}-${UBOOT_CONFIG}.bin-${target}-signed ${D}/boot/
    done
}

do_deploy:append:secureboot() {
    for target in ${IMXBOOT_TARGETS}; do
        # Use first "target" as IMAGE_IMXBOOT_TARGET
        if [ "$IMAGE_IMXBOOT_TARGET" = "" ]; then
            IMAGE_IMXBOOT_TARGET="$target"
            echo "Setting boot target as $IMAGE_IMXBOOT_TARGET"
        fi
        install -m 0644 ${S}/imx-boot${BOOT_VARIANT}-${MACHINE}-${UBOOT_CONFIG}.bin-${target}-signed ${DEPLOYDIR}
    done

    ln -sf imx-boot${BOOT_VARIANT}-${MACHINE}-${UBOOT_CONFIG}.bin-${IMAGE_IMXBOOT_TARGET}-signed ${DEPLOYDIR}/imx-boot
}
