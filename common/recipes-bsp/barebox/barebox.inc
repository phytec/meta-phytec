# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
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
	       command handling using the yocto kern-tools-native and \
	       kconfig-frontends-native."
HOMEPAGE = "http://www.barebox.org"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader barebox"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=057bf9e50e1ca857d0eb97bfe4ba8e5d"
SRC_URI = "http://barebox.org/download/barebox-${PV}.tar.bz2"
SRC_URI_append = " file://0001-Makefile-add-TARGETCC.patch"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

inherit kernel-arch cml1 deploy
PACKAGE_ARCH = "${MACHINE_ARCH}"
# verbose build 0/1
VERBOSE_BUILD ?= "0"
EXTRA_OEMAKE = 'TARGETCC="${CC}" V=${VERBOSE_BUILD} CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}"'
# target rootfs boot directory
base_bootdir = "/boot"

# these variables need to be assigned in the machine.conf 
BAREBOX_LOCALVERSION ?= "-${DISTRO}-${DISTRO_VERSION}"
# name of the image created by barebox Makefile
BAREBOX_BIN ?= "barebox.bin"
# filename of file to be deployed by this recipe
BAREBOX_BIN_DEPLOY ?= "barebox-${MACHINE}-${PKGV}-${PKGR}.bin"
BAREBOX_BIN_SYMLINK ?= "barebox.bin"
BAREBOX_DEFAULT_DEFCONFIG ?= ""
# barebox environment
BAREBOX_DEFAULT_ENV ?= ""

# recipe private variables
BAREBOX_COMMON_ENVDIR = "${WORKDIR}/commonenv"
BAREBOX_ENVDIR = "${WORKDIR}/environment"

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
        bb.note("No defconfig file provided for the barebox recipe")
        if not os.path.isfile(config):
            bb.note("barebox is not configured")
            bb.build.exec_func("do_default_defconfig",d)
        bb.build.exec_func("do_savedefconfig",d)
        defconfig = os.path.join(workdir , 'defconfig.temp') 
    fragments = find_sccs(d)
    if not fragments:
        bb.note("no config fragments will be merged")
        bb.utils.copyfile(defconfig, config)
    else:
        bb.note("combining kconfig fragments into .config for barebox")
        cmd = 'merge_config.sh -m -O %s %s %s' % (S, defconfig, ' '.join(map(str,fragments)))
        subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    localversion = d.getVar('BAREBOX_LOCALVERSION', True)
    bb.note("Setting LOCALVERSION in .config to %s" % localversion)
    cmd = 'kconfig-tweak --file %s --set-str LOCALVERSION %s' % (config, localversion)
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
}
addtask do_prepare_configure after do_patch before do_configure
do_prepare_configure[depends] += "kern-tools-native:do_populate_sysroot"
do_prepare_configure[depends] += "kconfig-frontends-native:do_populate_sysroot"

# its possible to give two environment folders to barebox: commonenv and
# environment, if an environment directory is provided, use it
python do_prepare_env() {
    import shutil, subprocess
    machine = d.getVar('MACHINE')
    S = d.getVar('S', True)
    bareboxconfig = os.path.join(S, '.config')
    env_dir = d.getVar('BAREBOX_ENVDIR', True)
    common_env_dir = d.getVar('BAREBOX_COMMON_ENVDIR', True)
    env_path_string = ""
    if not (os.path.isdir(common_env_dir) or os.path.isdir(env_dir)):
        bb.note("No environment directory provided, using BAREBOX_DEFAULT_ENV")
        common_env_dir = d.getVar('BAREBOX_DEFAULT_ENV', True)
        if not common_env_dir:
            bb.note("no BAREBOX_DEFAULT_ENV provided, creating empty default")
            bb.utils.mkdirhier(os.path.join(S, '.commonenv'))
    if os.path.isdir(common_env_dir):
        bb.note("copying common environment to source directory")
        oe.path.copytree(common_env_dir, oe.path.join(S, '.commonenv'))
        env_path_string += ".commonenv"
    if os.path.isdir(env_dir):
        bb.note("copying environment to source directory")
        oe.path.copytree(env_dir, oe.path.join(S, '.environment'))
        env_path_string += " .environment"
    cmd = 'kconfig-tweak --file %s --set-str DEFAULT_ENVIRONMENT_PATH "%s"' % (bareboxconfig, env_path_string)
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
}
addtask prepare_env after do_prepare_configure before do_configure
do_prepare_env[depends] += "kern-tools-native:do_populate_sysroot"
do_prepare_env[depends] += "kconfig-frontends-native:do_populate_sysroot"

# create config-board environment file
do_create_config_board () {
    if [ -e ${S}/.commonenv/config-board ]; then
        bbnote "config-board file already provided, not doing anything"
    else
        #tty boot serial variables
        B_TTY0=`echo "${SERIAL_CONSOLES}" | sed 's/\ .*//'`
        B_TTY0_B=`echo "${B_TTY0}" | sed 's/\;.*//'`
        B_TTY0_N=`echo "${B_TTY0}" | sed 's/.*\;//'`
        #create config-board file
        cat > ${S}/.commonenv/config-board <<EOF
#!/bin/sh
# board defaults, do not change in running system.
# Change /env/config file instead.
global.hostname=${MACHINE}
global.linux.bootargs.base="console=${B_TTY0_N},${B_TTY0_B}"
EOF
    fi
}
addtask create_config_board after do_prepare_env before do_configure

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

	bbnote "Installing host barebox toolset"
	install -d ${STAGING_BINDIR_NATIVE}
	cd ${S}
	install -m 755 scripts/bareboxenv ${STAGING_BINDIR_NATIVE}/
}
addtask deploy before do_build after do_compile
