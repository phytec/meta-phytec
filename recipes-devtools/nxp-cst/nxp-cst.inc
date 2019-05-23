SUMMARY = "NXP Code signing Tool for the High Assurance Boot library"
DESCRIPTION = "Provides software code signing support designed for use with \
i.MX processors that integrate the HAB library in the internal boot ROM."
HOMEPAGE = "https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = " \
    file://release/LICENSE.nxp;md5=6604ef69bd4ea2c604f8779985efd277 \
    file://release/LICENSE.openssl;md5=06698624268f7be8151210879bbcbcab \
"

DEPENDS = "openssl"

REQUIRED_DISTRO_FEATURES = "secureboot"
inherit distro_features_check

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/release/linux64/bin/cst ${D}${bindir}/cst
    install -m 0755 ${B}/release/linux64/bin/srktool ${D}${bindir}/srktool
    install -m 0755 ${B}/release/keys/hab4_pki_tree.sh ${D}${bindir}/hab4_pki_tree.sh
}

FILES_${PN} = "${bindir}"
BBCLASSEXTEND = "native"
