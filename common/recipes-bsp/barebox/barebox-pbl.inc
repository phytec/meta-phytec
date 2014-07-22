require barebox.inc

DESCRIPTION = "Barebox Pre-Bootloader: some machines need a pre-bootloader. This one is derived from the barebox."
PROVIDES = "barebox-pbl"

# Pre Bootloader 
BAREBOX_PBL_IMG ?= "barebox.bin"
BAREBOX_PBL_IMG_DEPLOY ?= "PBL-${MACHINE}-${PV}-${PR}.bin"
BAREBOX_PBL_IMG_SYMLINK ?= "PBL"
# Some platforms have a special combination image of PBL/Barebox
BAREBOX_COMBI_IMAGE ?= ""
BAREBOX_COMBI_IMAGE_DEPLOY ?= ""
BAREBOX_COMBI_IMAGE_SYMLINK ?= ""

do_deploy () {
	bbdebug 2 "Deploying barebox pbl"
	install ${S}/${BAREBOX_PBL_IMG} ${DEPLOYDIR}/${BAREBOX_PBL_IMG_DEPLOY}
	ln -sf ${BAREBOX_PBL_IMG_DEPLOY} ${DEPLOYDIR}/${BAREBOX_PBL_IMG_SYMLINK}

	if [ -n "${BAREBOX_COMBI_IMAGE}" ]
	then
		bbdebug 2 "Deploying barebox combi image"
		install ${S}/${BAREBOX_COMBI_IMAGE} ${DEPLOYDIR}/${BAREBOX_COMBI_IMAGE_DEPLOY}
		ln -sf ${BAREBOX_COMBI_IMAGE_DEPLOY} ${DEPLOYDIR}/${BAREBOX_COMBI_IMAGE_SYMLINK}
	fi
}
