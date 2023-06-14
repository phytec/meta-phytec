DEPENDS = "openssl byacc-native flex-native"

REQUIRED_DISTRO_FEATURES = "secureboot"
inherit features_check

S = "${WORKDIR}/cst-${PV}"

SRC_URI = " \
        file://cst-wrapper.sh \
"

CST_KEY_SOURCE ??= "file"

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/linux64/bin/cst ${D}${bindir}/cst-bin
    install -m 0755 ${S}/linux64/bin/srktool ${D}${bindir}/srktool

    install -m 0755 ${WORKDIR}/cst-wrapper.sh ${D}${bindir}/cst

    install -m 0755 ${S}/keys/hab4_pki_tree.sh ${D}${bindir}/hab4_pki_tree.sh
}

FILES_${PN} = "${bindir}"
BBCLASSEXTEND = "native"
