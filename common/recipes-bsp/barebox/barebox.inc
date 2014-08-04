# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
# Recipe released under the GPL-3.0 license
#
# TODO: see if there is some development towards moving all kconfig
#       funcionality into the kconfig-frontends package and adjust
#       the recipe accordingly.
#

SUMMARY = "The Barebox Bootloader"
DESCRIPTION = "Barebox (formerly known as u-boot-v2) is a bootloader that \
               inherits the best of U-Boot and the Linux kernel: The size and \
	       look-and-feel of u-boot, with driver model and lots of design \
	       concepts from the kernel. \
	       The bb recipe adds config fragment support and kconfig \
	       command handling using the yocto kern-tools-native."
HOMEPAGE = "http://www.barebox.org"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader barebox"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=057bf9e50e1ca857d0eb97bfe4ba8e5d"
SRC_URI = "http://barebox.org/download/barebox-${PV}.tar.bz2"

inherit kernel-arch cml1 deploy
DEPENDS += "kern-tools-native"
PACKAGE_ARCH = "${MACHINE_ARCH}"
# verbose build 0/1
VERBOSE_BUILD ?= "0"
EXTRA_OEMAKE = 'V=${VERBOSE_BUILD} CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}"'
# target rootfs boot directory
base_bootdir = "/boot"

# these variables need to be assigned in the machine.conf 
# name of the image created by barebox Makefile
BAREBOX_BIN ?= "barebox.bin"
# filename of file to be deployed by this recipe
BAREBOX_BIN_DEPLOY ?= "barebox-${MACHINE}-${PV}-${PR}.bin"
BAREBOX_BIN_SYMLINK ?= "barebox.bin"
BAREBOX_DEFAULT_DEFCONFIG ?= ""
# barebox environment
BAREBOX_ENV ?= "defaultenv/barebox_default_env"
BAREBOX_ENV_DEPLOY ?= "bareboxenv-${MACHINE}-${PV}-${PR}"
BAREBOX_ENV_SYMLINK ?= "barebox.env"

# returns .scc and .cfg filenames from SRC_URI
def find_sccs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        base, ext = os.path.splitext(os.path.basename(s))
        if ext and ext in [".scc", ".cfg"]:
            sources_list.append(s)
    return sources_list

# uses kern-tools-native to merge the config fragments
python do_prepare_configure() {
    import shutil, subprocess
    workdir = d.getVar('WORKDIR', True)
    S = d.getVar('S', True)
    defconfig = os.path.join(workdir , 'defconfig')
    if not os.path.isfile(defconfig):
        bb.note("No defconfig file provided for the barebox recipe")
        config = os.path.join(S, '.config')
        if not os.path.isfile(config):
            bb.note("barebox is not configured")
            bb.build.exec_func("do_default_defconfig",d)
        bb.build.exec_func("do_savedefconfig",d)
        defconfig = os.path.join(workdir , 'defconfig.temp') 
    os.chdir(workdir)
    fragments = find_sccs(d)
    if not fragments:
        bb.note("no config fragments will be merged")
    else:
        bb.note("combining kconfig fragments into .config for barebox")
        cmd = 'merge_config.sh -m -O %s %s %s' % (S, defconfig, ' '.join(map(str,fragments)))
        subprocess.call(cmd, shell=True)
    localversion = "-${DISTRO}-${DISTRO_VERSION}"
    bb.note("Setting LOCALVERSION in .config to %s" % localversion)
    bareboxconfig = os.path.join(S, '.config')
    cmd = 'kconfig-tweak --file %s --set-str LOCALVERSION %s' % (bareboxconfig, localversion)
    subprocess.call(cmd,shell=True)
}
do_configure[prefuncs] += "do_prepare_configure"

# if an environment directory is provided, use it
python do_prepare_env() {
    import shutil, subprocess
    workdir = d.getVar('WORKDIR', True)
    S = d.getVar('S', True)
    bareboxconfig = os.path.join(S, '.config')
    env_dir = oe.path.join(workdir , 'environment')
    if not os.path.exists(env_dir):
        bb.note("No environment directory provided, using the default env")
    else:
        bb.note("Adding custom environment")
        cmd = 'kconfig-tweak --file %s --set-str DEFAULT_ENVIRONMENT_PATH %s' % (bareboxconfig, env_dir)
        subprocess.call(cmd,shell=True)
}
do_configure[prefuncs] += "do_prepare_env"

do_default_defconfig () {
	bbnote "generating .config for target ${BAREBOX_DEFAULT_DEFCONFIG}"
	unset CFLAGS LDFLAGS
	oe_runmake ${BAREBOX_DEFAULT_DEFCONFIG}
}

do_savedefconfig() {
	bbplain "saving defconfig to ${WORKDIR}/defconfig.temp"
	oe_runmake savedefconfig
	install -m 644 ${S}/defconfig ${WORKDIR}/defconfig.temp
}
addtask savedefconfig after do_configure

do_compile () {
	unset CFLAGS LDFLAGS
	oe_runmake all
}

do_install () {
	bbnote "Installing barebox image on target rootfs"
	install -d ${D}${base_bootdir}
	install ${S}/${BAREBOX_BIN} ${D}${base_bootdir}/${BAREBOX_BIN_DEPLOY}
	ln -sf ${BAREBOX_BIN_DEPLOY} ${D}${base_bootdir}/${BAREBOX_BIN_SYMLINK}

	if [ -e ${S}/${BAREBOX_ENV} ] ; then
		bbnote "Installing barebox-env on target rootfs"
		install ${S}/${BAREBOX_ENV} ${D}${base_bootdir}/${BAREBOX_ENV_DEPLOY}
		ln -sf ${BAREBOX_ENV_DEPLOY} ${D}${base_bootdir}/${BAREBOX_ENV_SYMLINK}
	fi

	if [ -e ${S}/scripts/bareboxenv-target ] ; then
		bbnote "Installing target env tool"
		install -d ${D}${base_sbindir}
		install -m 744 ${S}/scripts/bareboxenv-target ${D}${base_sbindir}/bareboxenv
	fi

	if [ -e ${S}/scripts/bareboxcrc32-target ] ; then
		bbnote "Installing target crc32 tool"
		install -d ${D}${base_sbindir}
		install -m 744 ${S}/scripts/bareboxcrc32-target ${D}${base_sbindir}/bareboxcrc32
	fi

	if [ -e ${S}/scripts/kernel-install-target ] ; then
		bbnote "Installing target kernel-install tool"
		install -d ${D}${base_sbindir}
		install -m 744 ${S}/scripts/kernel-install-target ${D}${base_sbindir}/barebox-kernel-install
	fi
}
FILES_${PN} = ""
FILES_${PN} += "${base_bootdir}"
FILES_${PN}-dev = ""
RDEPENDS_${PN}-dev = ""
FILES_${PN}-dev += "${base_sbindir}"


do_deploy () {
	bbnote "Deploying barebox"
	install -d ${DEPLOYDIR}
	install ${S}/${BAREBOX_BIN} ${DEPLOYDIR}/${BAREBOX_BIN_DEPLOY}
	cd ${DEPLOYDIR}
	rm -f ${BAREBOX_BIN_SYMLINK} 
	ln -sf ${BAREBOX_BIN_DEPLOY} ${BAREBOX_BIN_SYMLINK}

	if [ -e ${S}/${BAREBOX_ENV} ] ; then
		bbnote "Deploying barebox env"
		install ${S}/${BAREBOX_ENV} ${DEPLOYDIR}/${BAREBOX_ENV_DEPLOY}
		cd ${DEPLOYDIR}
		rm -f ${BAERBOX_ENV_SYMLINK}
		ln -sf ${BAREBOX_ENV_DEPLOY} ${BAREBOX_ENV_SYMLINK}
	fi

	bbnote "Installing host barebox toolset"
	install -d ${STAGING_BINDIR_NATIVE}
	cd ${S}
	install -m 755 scripts/bareboxenv ${STAGING_BINDIR_NATIVE}/
}
addtask deploy before do_build after do_compile
