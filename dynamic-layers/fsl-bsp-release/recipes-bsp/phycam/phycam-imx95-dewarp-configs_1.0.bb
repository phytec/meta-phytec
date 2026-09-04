DESCRIPTION = "PHYTEC phyCAM dewarp calibration maps for i.MX 95"
HOMEPAGE = "http://www.phytec.de"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=16469200a43ccc97395e139bb705a9e2"

SECTION = "multimedia"

PR = "r0"
SRC_URI = "https://download.phytec.de/Software/Linux/Applications/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "088c38f52d10e760d08849c181d3709d"
SRC_URI[sha256sum] = "1c9a3dd30303c47ff4bab33b4c587b696fef2abb1c43bbe1dd7da02b1f321c4f"

S = "${UNPACKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    DESTDIR="${D}${datadir}/phycam-imx-dewarp"

    install -d ${D}${datadir}/phycam-imx-dewarp

    for file in $(find ${S} -type f -iname "*.bin"); do
        install -m 0644 -D ${file} ${D}${datadir}/phycam-imx-dewarp/
    done
}

FILES:${PN} += " \
    ${datadir}/phycam-imx-dewarp \
"
