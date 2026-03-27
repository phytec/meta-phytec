require recipes-bsp/isp-imx/isp-imx_${PV}.bb

SUMARY = "PHYTEC extension for i.MX Verisilicon Software ISP"
PROVIDES = "${PN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

ISP_IMX_SOURCE = "isp-imx-${PV}-${IMX_SRCREV_ABBREV}"

SRC_URI = "${FSL_MIRROR}/${ISP_IMX_SOURCE}.bin;fsl-eula=true"
SRC_URI += "\
    file://0001-units-isi-drv-Add-phyCAM-driver.patch \
    file://0002-units-isi-drv-phycam-Allow-to-configure-a-min-fps-fr.patch \
    file://run_isp.sh \
    file://imx8-phycam-isp.service \
    file://90-phycam-isp.rules \
    file://isp-mode-select-csi1.sh \
    file://isp-mode-select-csi2.sh \
"

S="${WORKDIR}/${ISP_IMX_SOURCE}"

SYSTEMD_SERVICE:${PN} = "imx8-phycam-isp.service"

RDEPENDS:${PN} += " \
    phycam-setup \
    isp-imx-phycam-configs \
    kernel-module-isp-vvcam \
"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}/${libdir}
    install -d ${D}/opt/imx8-isp/bin
    install -d ${D}${nonarch_base_libdir}/udev/rules.d

    install -m 0755 ${B}/generated/release/bin/isp_media_server ${D}/opt/imx8-isp/bin
    install -m 0755 ${B}/generated/release/bin/vvext ${D}/opt/imx8-isp/bin
    cp -r ${B}/generated/release/bin/*.drv ${D}/opt/imx8-isp/bin
    cp -r ${B}/generated/release/lib/*.so* ${D}/${libdir}

    install -m 0775 ${UNPACKDIR}/run_isp.sh ${D}/opt/imx8-isp/bin
    install -m 0644 ${UNPACKDIR}/90-phycam-isp.rules ${D}${nonarch_base_libdir}/udev/rules.d/

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/imx8-phycam-isp.service \
            ${D}${systemd_system_unitdir}
    fi

    install -m 0755 ${UNPACKDIR}/isp-mode-select-csi1.sh ${D}${bindir}/isp-mode-select-csi1
    install -m 0755 ${UNPACKDIR}/isp-mode-select-csi2.sh ${D}${bindir}/isp-mode-select-csi2
}

FILES:${PN} += "${nonarch_base_libdir}"
FILES:${PN} += "${bindir}"
FILES:${PN}-dev += " \
    ${libdir}/libphycam.so \
"
