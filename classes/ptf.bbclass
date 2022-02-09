# This class is used to extract yocto build data for our test environment.
# ENV_PTF_* variables are consumed by our test system configs.

inherit linux-kernel-base

def get_bootloader_makefile(d):
    bootloader = d.getVar('PREFERRED_PROVIDER_virtual/bootloader')
    mult = os.path.join(d.getVar('BASE_WORKDIR'), d.getVar('MULTIMACH_TARGET_SYS'))
    bl_mkfile = '%s/*/git/Makefile' % os.path.join(mult, bootloader)
    import glob
    return open(glob.glob(bl_mkfile)[0], 'r').read()

def get_bootloader_version(makefile):
    import re
    version = re.compile(r'(VERSION = \d\d\d\d)').findall(makefile)
    patchlevel = re.compile(r'(PATCHLEVEL = \d\d)').findall(makefile)
    return '%s.%s' % (version[0].split(' = ')[1], patchlevel[0].split(' = ')[1])

def build_ptf_data(d):
    data = {}
    kernel = get_kernelversion_file(d.getVar('STAGING_KERNEL_BUILDDIR')).split('-')[0]
    data['ENV_PTF_LINUX_VERSION'] = kernel
    data['ENV_PTF_LINUX_MAJOR_VERSION'] = kernel[0 : kernel.rfind('.')]

    from oe.rootfs import image_list_installed_packages
    pkgs = image_list_installed_packages(d)
    data['ENV_PTF_BUSYBOX_VERSION'] = pkgs['busybox']['ver'].split('-')[0]
    data['ENV_PTF_YOCTO_RELEASE'] = d.getVar('DISTRO_CODENAME')
    mk = get_bootloader_makefile(d)
    data['ENV_PTF_BOOTLOADER_VERSION'] = get_bootloader_version(mk)
    dtb = d.getVar('KERNEL_DEVICETREE').split(' ')[0]
    dtb = dtb.split('/')[1] if '/' in dtb else dtb
    data['ENV_PTF_DEVICETREE'] = dtb
    kernel_image = '%s-%s.bin' % (d.getVar('KERNEL_IMAGETYPE'), d.getVar('MACHINE'))
    data['ENV_PTF_KERNEL_IMAGE'] = kernel_image

    return data

python ptf_save_env_file () {
    try:
        data = build_ptf_data(d)
        deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
        img = d.getVar('IMAGE_BASENAME')
        ptf_env = os.path.join(deploy_dir, '%s-ptf.env' % img)
        with open(ptf_env, 'w') as f:
            for k in data:
                f.write('%s="%s"\n' % (k, data[k]))
    except Exception as e:
        bb.error("Failed to build ptf data:")
        bb.error(str(e))
}

ROOTFS_POSTPROCESS_COMMAND += "ptf_save_env_file ; "
