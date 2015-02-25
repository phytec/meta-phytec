FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:${THISDIR}/${PV}:${THISDIR}/${PN}/features:"

SRC_URI += " \
    file://commonenv \
    file://environment \
    file://ext4.cfg \
    file://remove-default-environment.patch \
"

COMPATIBLE_MACHINE = "beagleboneblack-1"

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

