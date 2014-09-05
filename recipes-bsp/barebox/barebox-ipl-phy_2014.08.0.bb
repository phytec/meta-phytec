require common/recipes-bsp/barebox/barebox-ipl.inc
require barebox-git.inc

FILESEXTRAPATHS_prepend_ti33x := "${THISDIR}/ipl-defconfigs:"

# phyflex-am335x-1
BRANCH_phyflex-am335x-1 = "WIP/2014.08.0/smk/phyflex"
#using default config
#SRC_URI_append_phyflex-am335x-2013-01 = " file://defconfig"
#    file://build-spi-image.cfg \
#"

# phyflex-am335x-1
BRANCH_phycore-am335x-1 = "WIP/2014.08.0/smk/phyflex"

