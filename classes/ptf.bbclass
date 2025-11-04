# This class is used to extract yocto build data for our test environment.
# ENV_PTF_* variables are consumed by our test system configs.

do_image[depends] += "virtual/kernel:do_shared_workdir"
do_image[depends] += "virtual/bootloader:do_deploy"
do_image_cpio[depends] += "virtual/kernel:do_shared_workdir"
do_image_cpio[depends] += "virtual/bootloader:do_deploy"
do_bundle[depends] += "virtual/kernel:do_shared_workdir"
do_bundle[depends] += "virtual/bootloader:do_deploy"

def get_bootloader_version(d):
    import os, re, bb

    bootloader = d.getVar('PREFERRED_PROVIDER_virtual/bootloader')
    uboot_file = d.getVar('UBOOT_BINARY')
    barebox_file = "barebox.config"
    bootloader_file = barebox_file if "barebox" in bootloader else uboot_file

    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
    image_path = os.path.join(deploy_dir, bootloader_file)

    if not os.path.exists(image_path):
        bb.fatal("Bootloader image not found: %s" % image_path)

    barebox_ver_re = re.compile(rb'(\d+\.\d+\.\d+)\s+Configuration')
    uboot_ver_re = re.compile(rb'U-Boot\s+(\d{4}\.\d{2})')
    pattern = barebox_ver_re if "barebox" in bootloader else uboot_ver_re

    with open(image_path, 'rb') as stream:
        data = stream.read()

    match = pattern.search(data)
    if not match:
        bb.fatal("Could not parse version from bootloader file")

    return match.group(1).decode()

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
    data['ENV_PTF_IMAGE'] = d.getVar('IMAGE_BASENAME')
    data['ENV_PTF_DISTRO'] = d.getVar('DISTRO')
    data['ENV_PTF_BOOTLOADER_VERSION'] = get_bootloader_version(d)

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
