DESCRIPTION = "Barebox (formerly known as u-boot-v2) is a bootloader that inherits the best of U-Boot and the Linux kernel: The size and look-and-feel of u-boot, with driver model and lots of design concepts from the kernel." 
HOMEPAGE = "http://www.barebox.org/"
SECTION = "bootloaders"
PROVIDES = "virtual/bootloader barebox"
PRIORITY = "optional"

inherit kernel-arch deploy

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX}"
#INSANE_SKIP_${PN} = "True"

# Name of the binary created by barebox Makefile
BAREBOX_BIN ?= "barebox.bin"
# Filename of binary deploy
BAREBOX_BIN_DEPLOY ?= "barebox-${MACHINE}-${PV}-${PR}.bin"
BAREBOX_BIN_SYMLINK ?= "barebox.bin"
# Barebox environment
BAREBOX_ENV ?= "defaultenv/barebox_default_env"
BAREBOX_ENV_DEPLOY ?= "bareboxenv-${MACHINE}-${PV}-${PR}.bin"
BAREBOX_ENV_SYMLINK ?= "barebox.env"

do_configure_prepend() {
	cp ${WORKDIR}/defconfig ${S}/.config
	oe_runmake oldconfig
}

do_compile () {
	if [ "${@base_contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold" ] ; then
		sed -i 's/$(CROSS_COMPILE)ld/$(CROSS_COMPILE)ld.bfd/g' Makefile
	fi
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS
	oe_runmake all
}

do_deploy () {
	bbdebug 2 "Deploying barebox"
	install -d ${DEPLOYDIR}
	install ${S}/${BAREBOX_BIN} ${DEPLOYDIR}/${BAREBOX_BIN_DEPLOY}
	install ${S}/${BAREBOX_ENV} ${DEPLOYDIR}/${BAREBOX_ENV_DEPLOY}

	bbdebug 2 "Creating symlinks"
	cd ${DEPLOYDIR}
	rm -f ${BAREBOX_BIN_SYMLINK} ${BAERBOX_ENV_SYMLINK}
	ln -sf ${BAREBOX_BIN_DEPLOY} ${BAREBOX_BIN_SYMLINK}
	ln -sf ${BAREBOX_ENV_DEPLOY} ${BAREBOX_ENV_SYMLINK}
	
	# TODO: why? who needs these scripts?
	bbdebug 2 "Installing host barebox toolset"
	install -d ${STAGING_BINDIR_NATIVE}
	cd ${S}
	install -m 755 scripts/mkimage ${STAGING_BINDIR_NATIVE}/
	install -m 755 scripts/bareboxenv ${STAGING_BINDIR_NATIVE}/
}
addtask deploy before do_build after do_compile
