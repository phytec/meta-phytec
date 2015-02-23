# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION = "Linux Kernel Phytec common base"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
require recipes-kernel/linux/linux-dtb.inc

DEPENDS += "lzop-native"
KERNEL_LOCALVERSION ?= "-${DISTRO}-${DISTRO_VERSION}"
GITSERVER = "git://git.phytec.de"
S = "${WORKDIR}/git"

# these variables need to be assigned in the machine.conf 
KERNEL_DEFAULT_DEFCONFIG ?= ""

# returns and .cfg filenames from SRC_URI
def find_sccs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        base, ext = os.path.splitext(os.path.basename(s))
        if ext and ext in [".cfg"]:
            sources_list.append(s)
    return sources_list

# uses kern-tools-native to merge the config fragments
python do_prepare_configure() {
    import subprocess
    workdir = d.getVar('WORKDIR', True)
    S = d.getVar('S', True)
    defconfig = os.path.join(workdir , 'defconfig')
    config = os.path.join(S, '.config')
    if not os.path.isfile(defconfig):
        bb.note("No defconfig file provided for the recipe")
        if not os.path.isfile(config):
            bb.note("kernel is not configured")
            bb.build.exec_func("do_default_defconfig",d)
        bb.build.exec_func("do_savedefconfig",d)
        defconfig = os.path.join(workdir , 'defconfig.temp') 
    fragments = find_sccs(d)
    if not fragments:
        bb.note("no config fragments will be merged")
        bb.utils.copyfile(defconfig, config)
    else:
        bb.note("combining kconfig fragments into .config")
        cmd = 'scripts/kconfig/merge_config.sh -m -O %s %s %s' % (S, defconfig, ' '.join(map(str,fragments)))
        subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    localversion = d.getVar('KERNEL_LOCALVERSION', True)
    bb.note("Setting LOCALVERSION in .config to %s" % localversion)
    cmd = 'kconfig-tweak --file %s --set-str LOCALVERSION %s' % (config, localversion)
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
}
addtask do_prepare_configure after do_patch before do_configure
do_prepare_configure[depends] += "kconfig-frontends-native:do_populate_sysroot"

do_default_defconfig () {
    bbnote "generating .config for target ${KERNEL_DEFAULT_DEFCONFIG}"
    oe_runmake ${KERNEL_DEFAULT_DEFCONFIG}
}

do_savedefconfig() {
    bbplain "saving defconfig to ${WORKDIR}/defconfig.temp"
    oe_runmake savedefconfig
    install -m 644 ${B}/defconfig ${WORKDIR}/defconfig.temp
}
addtask savedefconfig after do_configure

KERNEL_IMAGE_BASE_NAME = "${KERNEL_IMAGETYPE}-${PN}-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
KERNEL_DEFCONFIG_NAME = "${KERNEL_IMAGE_BASE_NAME}-defconfig"
KERNEL_DEFCONFIG_NAME[vardepsexclude] = "DATETIME"
do_deploy_append() {
    install -m 644 ${B}/defconfig ${DEPLOYDIR}/${KERNEL_DEFCONFIG_NAME}
    rm -rf ${DEPLOYDIR}/${KERNEL_IMAGETYPE}-defconfig
    ln -sf ${KERNEL_DEFCONFIG_NAME} ${DEPLOYDIR}/${KERNEL_IMAGETYPE}-defconfig
}
do_deploy[depends] += "${PN}:do_savedefconfig"
