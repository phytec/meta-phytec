require common/recipes-bsp/barebox/barebox.inc
require phytec-barebox-am335x-git.inc

FILESEXTRAPATHS_prepend_am335x := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:"

# phyflex-am335x-2013-01
BRANCH_phyflex-am335x-2013-01 = "WIP/2014.08.0/smk/phyflex"
SRC_URI_append_phyflex-am335x-2013-01 = " file://defconfig"
# default environment
FILESEXTRAPATHS_prepend_phyflex-am335x-2013-01 := "${THISDIR}/env/default:"
SRC_URI_append_phyflex-am335x-2013-01 = " file://environment"
#    file://no-defenv.cfg \
#    file://oftree.cfg \
#    file://build-spi-image.cfg \
# 

# phycore-am335x-2012-01
BRANCH_phycore-am335x-2012-01 = "WIP/2014.08.0/smk/phyflex"
# am335x default defconfig
SRC_URI_append_phycore-am335x-2012-01 = " file://defconfig"
# default environment
FILESEXTRAPATHS_prepend_phycore-am335x-2012-01 := "${THISDIR}/env/default:"
SRC_URI_append_phycore-am335x-2012-01 = " file://environment"
