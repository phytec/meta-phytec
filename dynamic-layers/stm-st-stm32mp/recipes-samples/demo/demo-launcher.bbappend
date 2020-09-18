FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"


SRC_URI += " \
    file://0001-Customize-ST-GTK-demo-launcher-for-PHYTEC-board.patch;patchdir=${WORKDIR} \
    ftp://ftp.phytec.de/pub/Software/Linux/BSP-Yocto-STM32MP1/resources/phyCORE-STM32MP1.png \
"
SRC_URI[md5sum] = "4fe1b9775321d7e7a90a81e21ac7bd1a"
SRC_URI[sha256sum] = "052563ad8cd9738bdb3a435c2ad85d2c10a63a14ba5b3505fbaa51b56f6bb2db"

do_install_append() {
    install -m 0644 ${WORKDIR}/phyCORE-STM32MP1.png ${D}${prefix}/local/demo/pictures/
}
