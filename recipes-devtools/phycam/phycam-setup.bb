SUMARY = "PHYTEC phyCAM media pipeline setup"
HOMEPAGE = "http://www.phytec.de"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PR = "r0"

SRC_URI = " \
    file://90-phycam.rules \
"

SRC_URI:append:mx6ul-generic-bsp = " \
    file://setup-pipeline-csi1.sh \
"

SRC_URI:append:mx8mp-generic-bsp = " \
    file://setup-pipeline-csi1.sh \
    file://setup-pipeline-csi2.sh \
"

do_install() {
    install -d ${D}${nonarch_base_libdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/90-phycam.rules \
                    ${D}${nonarch_base_libdir}/udev/rules.d/

    if [ -e ${WORKDIR}/setup-pipeline-csi1.sh ]; then
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/setup-pipeline-csi1.sh \
                        ${D}${bindir}/setup-pipeline-csi1
    fi

    if [ -e ${WORKDIR}/setup-pipeline-csi2.sh ]; then
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/setup-pipeline-csi2.sh \
                        ${D}${bindir}/setup-pipeline-csi2
    fi
}

FILES:${PN} += " \
    ${nonarch_base_libdir} \
    ${bindir} \
"

RDEPENDS:${PN} += " \
    v4l-utils \
    media-ctl \
"
