require common/recipes-bsp/barebox/barebox-ipl.inc
require phytec-barebox-am335x-git.inc

FILESEXTRAPATHS_prepend_am335x := "${THISDIR}/ipl-defconfigs:${THISDIR}/defconfigs/features:"

# phyflex-am335x-2013-01
BRANCH_phyflex-am335x-2013-01 = "WIP/2014.08.0/smk/phyflex"
#using default config
#SRC_URI_append_phyflex-am335x-2013-01 = " file://defconfig"
#    file://build-spi-image.cfg \
#"

# phyflex-am335x-2013-01
BRANCH_phycore-am335x-2012-01 = "WIP/2014.08.0/smk/phyflex"

