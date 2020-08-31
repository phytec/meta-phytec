FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"


SRC_URI += " \
    file://0001-Customize-ST-GTK-demo-launcher-for-PHYTEC-board.patch;patchdir=${WORKDIR} \
    ftp://ftp.phytec.de/pub/Software/Linux/BSP-Yocto-STM32MP1/resources/phyCORE-STM32MP1.png \
"


do_install_append() {
    install -m 0644 ${WORKDIR}/phyCORE-STM32MP1.png ${D}${prefix}/local/demo/pictures/
}
