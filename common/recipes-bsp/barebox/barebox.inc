# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de> PHYTEC Messtechnik GmbH
# Released under the GPL-3.0 license

SUMMARY = "The Barebox Bootloader"
HOMEPAGE = "http://www.barebox.org"
DESCRIPTION = "Barebox (formerly known as u-boot-v2) is a bootloader that inherits the best of U-Boot and the Linux kernel: The size and look-and-feel of u-boot, with driver model and lots of design concepts from the kernel." 
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader barebox"

inherit kernel-arch cml1 deploy
DEPENDS += "kconfig-frontends"
EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX}"

# Barebox uses .bin and .img for output files
BAREBOX_SUFFIX ?= "bin"
# Name of the binary created by barebox Makefile
BAREBOX_IMG ?= "barebox.${BAREBOX_SUFFIX}"
# Filename of binary deploy
BAREBOX_IMG_DEPLOY ?= "barebox-${MACHINE}-${PV}-${PR}.${BAREBOX_SUFFIX}"
BAREBOX_IMG_SYMLINK ?= "barebox.${BAREBOX_SUFFIX}"
# Barebox environment
BAREBOX_ENV ?= "defaultenv/barebox_default_env"
BAREBOX_ENV_DEPLOY ?= "bareboxenv-${MACHINE}-${PV}-${PR}"
BAREBOX_ENV_SYMLINK ?= "barebox.env"

# config fragment collect, copied from kernel-yocto.bbclass
# returns all the elements from the src uri that are .scc files
def find_sccs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        base, ext = os.path.splitext(os.path.basename(s))
        if ext and ext in [".scc", ".cfg"]:
            sources_list.append(s)
        elif base and base in 'defconfig':
            sources_list.append(s)

    return sources_list

# uses kconfig-frontends to combine the config fragments
python do_configure_prepend() {
    fragments = find_sccs(SRC_URI)
    for f in fragmentlist:
         kconfig-merge defconfig f
    kconfig-tweak --set-str LOCALVERSION "-PD14.1.0" defconfig
    cp ${WORKDIR}/defconfig ${S}/.config
}

do_compile () {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ] ; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' config.mk
	fi
	unset CFLAGS CPPFLAGS LDFLAGS
	oe_runmake all
}

do_deploy () {
	bbdebug 2 "Deploying barebox"
	install -d ${DEPLOYDIR}
	install ${S}/${BAREBOX_IMG} ${DEPLOYDIR}/${BAREBOX_IMG_DEPLOY}
	install ${S}/${BAREBOX_ENV} ${DEPLOYDIR}/${BAREBOX_ENV_DEPLOY}

	bbdebug 2 "Creating symlinks"
	cd ${DEPLOYDIR}
	rm -f ${BAREBOX_IMG_SYMLINK} ${BAERBOX_ENV_SYMLINK}
	ln -sf ${BAREBOX_IMG_DEPLOY} ${BAREBOX_IMG_SYMLINK}
	ln -sf ${BAREBOX_ENV_DEPLOY} ${BAREBOX_ENV_SYMLINK}
	
	bbdebug 2 "Installing host barebox toolset"
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


