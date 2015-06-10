inherit phygittag
inherit buildinfo
require common/recipes-bsp/barebox/barebox-ipl.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/barebox/features:"

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
S = "${WORKDIR}/git"
BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

PR = "${INC_PR}.0"

SRCREV = "eb699b609649147f2706b2c86a9599275bdecbdf"

COMPATIBLE_MACHINE = "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-maia-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phyflex-am335x-1"

do_deploy_append () {
    # deploy spi boot images
    spiimg=$(echo ${BAREBOX_IPL_BIN} | sed 's/.img/.spi.img/')
    if [ -e ${B}/$spiimg ] ; then
        install -m 0644 ${B}/$spiimg ${DEPLOYDIR}/${BAREBOX_IPL_BIN_DEPLOY}.spi
        ln -sf ${BAREBOX_IPL_BIN_DEPLOY}.spi ${DEPLOYDIR}/${BAREBOX_IPL_BIN_SYMLINK}.spi
    fi

    # deploy peripheral boot images (without GP header)
    perimg=$(echo ${BAREBOX_IPL_BIN} | sed 's/barebox/start/;s/-/_/g;s/mlo/sram/;s/img/pblx/')
    install -m 0644 ${B}/$perimg ${DEPLOYDIR}/${BAREBOX_IPL_BIN_DEPLOY}.per
    ln -sf ${BAREBOX_IPL_BIN_DEPLOY}.per ${DEPLOYDIR}/${BAREBOX_IPL_BIN_SYMLINK}.per
}
