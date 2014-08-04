require common/recipes-bsp/barebox/barebox.inc
require phytec-barebox-am335x-git.inc

FILESEXTRAPATHS_prepend_am335x := "${THISDIR}/defconfigs:${THISDIR}/features:"

# phyflex-am335x-2013-01
BRANCH_phyflex-am335x-2013-01 = "WIP/s.mueller-klieser@phytec.de/phyFLEX-mainline"
SRC_URI_append_phyflex-am335x-2013-01 = " \
    file://defconfig \
    file://build-spi-image.cfg \
"
