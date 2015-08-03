# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de> \
# PHYTEC Messtechnik GmbH
# Recipe released under the GPL-3.0 license

require barebox.inc

DESCRIPTION = "Barebox IPL Inintial Program Loader: some machines need \
               a pre-bootloader. This one gets build by the barebox."
# Overwrite PROVIDES in barebox.inc
PROVIDES = "virtual/prebootloader"
S = "${WORKDIR}/barebox-${PV}"

# Pre Bootloader 
BAREBOX_IPL_BIN ?= "barebox.bin"
BAREBOX_IPL_IMAGE_BASE_NAME ?= "IPL-${MACHINE}-${PKGV}-${PKGR}-${DATETIME}"
BAREBOX_IPL_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"
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
	install -m 644 ${B}/${BAREBOX_IPL_BIN} ${D}${base_bootdir}/${BAREBOX_IPL_IMAGE_BASE_NAME}.img
	ln -sf ${BAREBOX_IPL_IMAGE_BASE_NAME}.img ${D}${base_bootdir}/${BAREBOX_IPL_BIN_SYMLINK}
}
FILES_${PN} = ""
FILES_${PN} = "${base_bootdir}"

# Overwrites do_deploy in barebox.inc
do_deploy () {
	bbnote "Deploying barebox ipl"
	install -m 644 ${B}/${BAREBOX_IPL_BIN} ${DEPLOYDIR}/${BAREBOX_IPL_IMAGE_BASE_NAME}.img
	ln -sf ${BAREBOX_IPL_IMAGE_BASE_NAME}.img ${DEPLOYDIR}/${BAREBOX_IPL_BIN_SYMLINK}

	install -m 644 ${B}/.config ${DEPLOYDIR}/${BAREBOX_IPL_IMAGE_BASE_NAME}.config
	ln -sf ${BAREBOX_IPL_IMAGE_BASE_NAME}.config ${DEPLOYDIR}/${BAREBOX_IPL_BIN_SYMLINK}.config
}
