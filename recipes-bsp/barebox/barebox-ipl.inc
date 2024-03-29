DESCRIPTION = "Barebox IPL Inintial Program Loader: some machines need \
               a pre-bootloader. This one gets build by the barebox."
# Overwrite PROVIDES in barebox.inc
PROVIDES = "virtual/prebootloader"

# The barebox-ipl recipes include the bbclass barebox-environment-2.  The
# environment is not needed for the MLO, so don't generate it.
do_env[noexec] = "1"
do_env_write[noexec] = "1"

# Set include folder explicitly, because ${PN} of IPL recipe is not "barebox".
FILESEXTRAPATHS:prepend := "${THISDIR}/barebox/:"

# Pre Bootloader 
BAREBOX_IPL_BIN ?= "barebox.bin"
BAREBOX_IPL_IMAGE_NAME ?= "IPL-${MACHINE}-${PKGV}-${PKGR}${IMAGE_VERSION_SUFFIX}"
BAREBOX_IPL_IMAGE_NAME[vardepsexclude] = "IMAGE_VERSION_SUFFIX"
BAREBOX_IPL_BIN_LINK_NAME ?= "IPL"

do_install () {
	bbnote "Installing barebox-ipl image on target rootfs"
	install -d ${D}${base_bootdir}
	install -m 644 ${B}/${BAREBOX_IPL_BIN} ${D}${base_bootdir}/${BAREBOX_IPL_IMAGE_NAME}.img
	ln -sf ${BAREBOX_IPL_IMAGE_NAME}.img ${D}${base_bootdir}/${BAREBOX_IPL_BIN_LINK_NAME}
}
FILES:${PN} = ""
FILES:${PN} = "${base_bootdir}"

# Overwrites do_deploy in barebox.inc
do_deploy () {
	bbnote "Deploying barebox ipl"
	install -m 644 ${B}/${BAREBOX_IPL_BIN} ${DEPLOYDIR}/${BAREBOX_IPL_IMAGE_NAME}.img
	ln -sf ${BAREBOX_IPL_IMAGE_NAME}.img ${DEPLOYDIR}/${BAREBOX_IPL_BIN_LINK_NAME}

	install -m 644 ${B}/.config ${DEPLOYDIR}/${BAREBOX_IPL_IMAGE_NAME}.config
	ln -sf ${BAREBOX_IPL_IMAGE_NAME}.config ${DEPLOYDIR}/${BAREBOX_IPL_BIN_LINK_NAME}.config
}
