# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de> \
# PHYTEC Messtechnik GmbH
# Recipe released under the GPL-3.0 license

require barebox.inc

DESCRIPTION = "Barebox IPL Inintial Program Loader: some machines need \
               a pre-bootloader. This one gets build by the barebox."
PROVIDES = "barebox-ipl"
S = "${WORKDIR}/barebox-${PV}"

# Pre Bootloader 
BAREBOX_IPL_BIN ?= "barebox.bin"
BAREBOX_IPL_BIN_DEPLOY ?= "IPL-${MACHINE}-${PKGV}-${PKGR}-${DATETIME}.bin"
BAREBOX_IPL_BIN_DEPLOY[vardepsexclude] = "DATETIME"
BAREBOX_IPL_BIN_SYMLINK ?= "IPL"
BAREBOX_IPL_DEFAULT_DEFCONFIG ?= ""

# dont use the environment prepare function from barebox
python do_prepare_env() {
    return
} 

# dont edit config-board env file
do_create_config_board() {
}

do_default_defconfig () {
	bbnote "generating .config for target ${BAREBOX_IPL_DEFAULT_DEFCONFIG}"
	unset CFLAGS LDFLAGS
	oe_runmake ${BAREBOX_IPL_DEFAULT_DEFCONFIG}
}

do_install () {
	bbnote "Installing barebox-ipl image on target rootfs"
	install -d ${D}${base_bootdir}
	install -m 644 ${B}/${BAREBOX_IPL_BIN} ${D}${base_bootdir}/${BAREBOX_IPL_BIN_DEPLOY}
	ln -sf ${BAREBOX_IPL_BIN_DEPLOY} ${D}${base_bootdir}/${BAREBOX_IPL_BIN_SYMLINK}
}
FILES_${PN} = ""
FILES_${PN} = "${base_bootdir}"

do_deploy () {
	bbnote "Deploying barebox ipl"
	install ${B}/${BAREBOX_IPL_BIN} ${DEPLOYDIR}/${BAREBOX_IPL_BIN_DEPLOY}
	ln -sf ${BAREBOX_IPL_BIN_DEPLOY} ${DEPLOYDIR}/${BAREBOX_IPL_BIN_SYMLINK}
}
