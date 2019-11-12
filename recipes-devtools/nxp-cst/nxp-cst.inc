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
