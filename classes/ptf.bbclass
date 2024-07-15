# This class is used to extract yocto build data for our test environment.
# ENV_PTF_* variables are consumed by our test system configs.

do_image_cpio[depends] += "virtual/kernel:do_shared_workdir"
do_image_cpio[depends] += "virtual/bootloader:do_unpack"
do_bundle[depends] += "virtual/kernel:do_shared_workdir"
do_bundle[depends] += "virtual/bootloader:do_unpack"

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

def get_kernelversion_file(p):
    fn = p + '/kernel-abiversion'

    try:
        with open(fn, 'r') as f:
            return f.readlines()[0].strip()
    except IOError:
        return None

def build_ptf_data(d):
    data = {}
    kernel = get_kernelversion_file(d.getVar('STAGING_KERNEL_BUILDDIR')).split('-')[0]
    data['ENV_PTF_LINUX_VERSION'] = kernel
    data['ENV_PTF_LINUX_MAJOR_VERSION'] = kernel[0 : kernel.rfind('.')]

    from oe.rootfs import image_list_installed_packages
    pkgs = image_list_installed_packages(d)
    if 'busybox' in pkgs and 'ver' in pkgs['busybox']:
        data['ENV_PTF_BUSYBOX_VERSION'] = pkgs['busybox']['ver'].split('-')[0]

    data['ENV_PTF_YOCTO_RELEASE'] = d.getVar('DISTRO_CODENAME')
    mk = get_bootloader_makefile(d)
    data['ENV_PTF_BOOTLOADER_VERSION'] = get_bootloader_version(mk)

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
