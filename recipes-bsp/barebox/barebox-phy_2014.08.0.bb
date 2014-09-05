require common/recipes-bsp/barebox/barebox.inc
require barebox-git.inc

FILESEXTRAPATHS_prepend_ti33x := "${THISDIR}/defconfigs:${THISDIR}/env:"

SRC_URI += "file://defconfig"

# phyflex-am335x-2013-01
BRANCH_phyflex-am335x-1 = "WIP/2014.08.0/smk/phyflex"
# special environment
SRC_URI_append_phyflex-am335x-1 = " file://environment"
#    file://no-defenv.cfg \
#    file://oftree.cfg \
#    file://build-spi-image.cfg \
# 

# phycore-am335x-2012-01
BRANCH_phycore-am335x-1 = "WIP/2014.08.0/smk/phyflex"
# default environment
SRC_URI_append_phycore-am335x-1 = " file://environment"
