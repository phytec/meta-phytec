DESCRIPTION = "Barebox - a bootloader that inherits the best of U-Boot and the Linux kernel"
HOMEPAGE = "http://www.barebox.org/"
SECTION = "bootloaders"
PROVIDES = "barebox-pbl"
PRIORITY = "optional"

inherit kernel-arch deploy

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX}"
#INSANE_SKIP_${PN} = "True"

# Pre Bootloader 
BAREBOX_PBL_BIN ?= "barebox.bin"
BAREBOX_PBL_BIN_DEPLOY ?= "PBL-${MACHINE}-${PV}-${PR}.bin"
BAREBOX_PBL_BIN_SYMLINK ?= "PBL"
# Some platforms have a special combination image of PBL/Barebox
BAREBOX_COMBI_IMAGE ?= ""
BAREBOX_COMBI_IMAGE_DEPLOY ?= ""
BAREBOX_COMBI_IMAGE_SYMLINK ?= ""

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
	bbdebug 2 "Deploying barebox pbl"
	install ${S}/${BAREBOX_PBL_BIN} ${DEPLOYDIR}/${BAREBOX_PBL_BIN_DEPLOY}
	ln -sf ${BAREBOX_PBL_BIN_DEPLOY} ${DEPLOYDIR}/${BAREBOX_PBL_BIN_SYMLINK}

	if [ -n "${BAREBOX_COMBI_IMAGE}" ]
	then
		bbdebug 2 "Deploying barebox combi image"
		install ${S}/${BAREBOX_COMBI_IMAGE} ${DEPLOYDIR}/${BAREBOX_COMBI_IMAGE_DEPLOY}
		ln -sf ${BAREBOX_COMBI_IMAGE_DEPLOY} ${DEPLOYDIR}/${BAREBOX_COMBI_IMAGE_SYMLINK}
	fi
}
addtask deploy before do_build after do_compile
