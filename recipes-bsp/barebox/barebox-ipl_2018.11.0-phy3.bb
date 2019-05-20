require barebox_${PV}.bb
require barebox-ipl.inc

SRC_URI_append = " file://netboot.cfg"

PR = "${INC_PR}.1"

do_deploy_append () {
    # deploy spi boot images
    spiimg=$(echo ${BAREBOX_IPL_BIN} | sed 's/.img/.spi.img/')
    if [ -e ${B}/$spiimg ] ; then
        install -m 0644 ${B}/$spiimg ${DEPLOYDIR}/${BAREBOX_IPL_IMAGE_NAME}.img.spi
        ln -sf ${BAREBOX_IPL_IMAGE_NAME}.img.spi ${DEPLOYDIR}/${BAREBOX_IPL_BIN_LINK_NAME}.spi
    fi

    # deploy peripheral boot images (without GP header)
    perimg=$(echo ${BAREBOX_IPL_BIN} | sed 's/barebox/start/;s/-/_/g;s/mlo/sram/;s/img/pblx/')
    install -m 0644 ${B}/$perimg ${DEPLOYDIR}/${BAREBOX_IPL_IMAGE_NAME}.img.per
    ln -sf ${BAREBOX_IPL_IMAGE_NAME}.img.per ${DEPLOYDIR}/${BAREBOX_IPL_BIN_LINK_NAME}.per
}

INTREE_DEFCONFIG = "am335x_mlo_defconfig"
