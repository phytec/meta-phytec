require common/recipes-bsp/barebox/barebox.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/features:${THISDIR}:"

SRC_URI = "git://git.phytec.de/barebox;branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
    file://ext4.cfg \
"
# floating revision
#SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"
COMPATIBLE_MACHINE_ti33x = "(ti33x)"

BRANCH = "v2014.10.0-phy"
#PV = "v2014.10.0-phy-git${SRCPV}"
BAREBOX_LOCALVERSION = "-${BSP_VERSION}"
PV = "v2014.10.0-phy1"
# TAG = ${PV}
SRCREV = "8b015cd0815904b5c20ea5130884fdcea3344439"

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
addtask appendbootconfig_to_configboard after do_create_config_board before do_configure
