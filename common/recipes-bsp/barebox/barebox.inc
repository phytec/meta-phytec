# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de> \
# PHYTEC Messtechnik GmbH
# Released under the GPL-3.0 license
SUMMARY = "The Barebox Bootloader"
DESCRIPTION = "Barebox (formerly known as u-boot-v2) is a bootloader that \
               inherits the best of U-Boot and the Linux kernel: The size and \
	       look-and-feel of u-boot, with driver model and lots of design \
	       concepts from the kernel.\
	       The bb recipe adds config fragment support and kconfig \
	       command handling using the yocto kern-tools-native." 
HOMEPAGE = "http://www.barebox.org"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader barebox"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=057bf9e50e1ca857d0eb97bfe4ba8e5d"
SRC_URI = "http://barebox.org/download/${PN}-${PV}.tar.bz2"

inherit kernel-arch cml1 deploy
DEPENDS += "kern-tools-native"
PACKAGE_ARCH = "${MACHINE_ARCH}"
EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX}"

# recipes implementing this include need to override these variables
# barebox uses .bin and .img for output files
BAREBOX_SUFFIX ?= "bin"
# name of the binary created by barebox Makefile
BAREBOX_IMG ?= "barebox.${BAREBOX_SUFFIX}"
# filename of binary deploy
BAREBOX_IMG_DEPLOY ?= "barebox-${MACHINE}-${PV}-${PR}.${BAREBOX_SUFFIX}"
BAREBOX_IMG_SYMLINK ?= "barebox.${BAREBOX_SUFFIX}"
# barebox environment
BAREBOX_ENV ?= "defaultenv/barebox_default_env"
BAREBOX_ENV_DEPLOY ?= "bareboxenv-${MACHINE}-${PV}-${PR}"
BAREBOX_ENV_SYMLINK ?= "barebox.env"

# returns .scc and .cfg files from SRC_URI
def find_sccs(d):
    sources=src_patches(d, True)
    sources_list=[]
    bb.debug(2,"source variables:")
    for s in sources:
        bb.debug(2," -- " + s)
        base, ext = os.path.splitext(os.path.basename(s))
        if ext and ext in [".scc", ".cfg"]:
            sources_list.append(s)
    return sources_list

# uses kconfig-frontends to combine the config fragments
python do_prepare_configure() {
    import shutil, subprocess
    workdir = d.getVar('WORKDIR',True)
    defconfig = os.path.join(workdir , 'defconfig')
    S = d.getVar('S',True)
    if not os.path.isfile(defconfig):
        bb.note("No defconfig file provided for the barebox recipe. \
                using default config file provided by barebox.")
        #TODO implement default config call
    os.chdir(workdir)
    fragments = find_sccs(d)
    if not fragments:
        bb.note("no config fragments will be merged")
    else:
        bb.note("combining kconfig fragments for barebox")
        for f in fragments:
            cmd = 'merge_config.sh -m -O %s %s %s' % (S, defconfig, f)
            subprocess.call(cmd, shell=True)
    bareboxconfig = os.path.join(S, '.config')
    #TODO implement version string handling
    cmd = 'kconfig-tweak --file %s --set-str LOCALVERSION %s' % (bareboxconfig, "-PD14.1.0")
    subprocess.call(cmd,shell=True)
}
do_configure[prefuncs] += "do_prepare_configure"
do_configure[deptask] = "do_prepare_configure"

do_compile () {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ]; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' config.mk
	fi
	unset CFLAGS CPPFLAGS LDFLAGS
	oe_runmake all
}

do_deploy () {
	bbnote "Deploying barebox"
	install -d ${DEPLOYDIR}
	install ${S}/${BAREBOX_IMG} ${DEPLOYDIR}/${BAREBOX_IMG_DEPLOY}
	install ${S}/${BAREBOX_ENV} ${DEPLOYDIR}/${BAREBOX_ENV_DEPLOY}

	bbnote "Creating symlinks"
	cd ${DEPLOYDIR}
	rm -f ${BAREBOX_IMG_SYMLINK} ${BAERBOX_ENV_SYMLINK}
	ln -sf ${BAREBOX_IMG_DEPLOY} ${BAREBOX_IMG_SYMLINK}
	ln -sf ${BAREBOX_ENV_DEPLOY} ${BAREBOX_ENV_SYMLINK}
	
	bbnote "Installing host barebox toolset"
	install -d ${STAGING_BINDIR_NATIVE}
	cd ${S}
	install -m 755 scripts/bareboxenv ${STAGING_BINDIR_NATIVE}/
}
addtask deploy before do_build after do_compile

# as soon as this gets moved from kernel-yocto.bbclass into cml1.bbclass
# or some kconfig handling class, remove it here
# savedefconfig saves a minimal config from the current confige removing
# entries matching the default values
do_savedefconfig() {
	oe_runmake savedefconfig
}
addtask savedefconfig after do_configure


