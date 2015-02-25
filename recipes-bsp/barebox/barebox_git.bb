inherit buildinfo
require common/recipes-bsp/barebox/barebox.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/features:${THISDIR}:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
    file://targettools.cfg \
"

# tag revision (NOTE: Keep TAG, PV and SRCREV in sync!)
BRANCH = "v2014.10.0-phy"
TAG = "v2014.10.0-phy1"
SRCREV = "8b015cd0815904b5c20ea5130884fdcea3344439"
#SRCREV = "${TAG}"
PV = "${TAG}"

# use this for building the HEAD of the git branch 
#SRCREV = "${AUTOREV}"
#PV = "v2014.10.0-phy-git${SRCPV}"

S = "${WORKDIR}/git"

do_appendbootconfig_to_configboard () {
    bbnote "config-board: append bootconfig"
    cat >>  ${S}/.commonenv/config-board <<EOF
if [ \$bootsource = mmc ]; then
        global.boot.default="mmc nand spi net"
elif [ \$bootsource = nand ]; then
        global.boot.default="nand spi mmc net"
elif [ \$bootsource = spi ]; then
        global.boot.default="spi nand mmc net"
elif [ \$bootsource = net ]; then
        global.boot.default="net nand spi mmc"
fi
EOF
}
addtask appendbootconfig_to_configboard after do_prepare_env before do_configure

BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

COMPATIBLE_MACHINE = "(ti33x)"
