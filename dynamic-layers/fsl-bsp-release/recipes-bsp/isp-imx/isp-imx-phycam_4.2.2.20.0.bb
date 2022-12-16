require recipes-bsp/isp-imx/isp-imx_4.2.2.20.0.bb

SUMARY = "PHYTEC extension for i.MX Verisilicon Software ISP"
PROVIDES = "${PN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI = "${FSL_MIRROR}/isp-imx-${PV}.bin;fsl-eula=true"
SRC_URI += "\
    file://0001-units-isi-drv-Add-phyCAM-driver.patch \
    file://0001-mediacontrol-V4L2File-Try-both-video-devices.patch \
    file://run_isp.sh \
    file://imx8-phycam-isp.service \
    file://90-phycam-isp.rules \
"

S="${WORKDIR}/isp-imx-${PV}"

SYSTEMD_SERVICE:${PN} = "imx8-phycam-isp.service"

RDEPENDS:${PN} += " \
    phycam-setup \
    isp-imx-phycam-configs \
    kernel-module-isp-vvcam \
"

do_install() {
    install -d ${D}/${libdir}
    install -d ${D}/opt/imx8-isp/bin
    install -d ${D}${nonarch_base_libdir}/udev/rules.d

    install -m 0755 ${B}/generated/release/bin/isp_media_server ${D}/opt/imx8-isp/bin
    install -m 0755 ${B}/generated/release/bin/vvext ${D}/opt/imx8-isp/bin
    cp -r ${B}/generated/release/bin/*.drv ${D}/opt/imx8-isp/bin
    cp -r ${B}/generated/release/lib/*.so* ${D}/${libdir}

    install -m 0775 ${WORKDIR}/run_isp.sh ${D}/opt/imx8-isp/bin
    install -m 0644 ${WORKDIR}/90-phycam-isp.rules ${D}${nonarch_base_libdir}/udev/rules.d/

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/imx8-phycam-isp.service \
            ${D}${systemd_system_unitdir}
    fi
}

FILES:${PN} += "${nonarch_base_libdir}"
FILES:${PN}-dev += " \
    ${libdir}/libphycam.so \
"
