DEPENDS = "openssl"

REQUIRED_DISTRO_FEATURES = "secureboot"
inherit features_check

S = "${WORKDIR}"

NXP_CST_BASE_PATH ??= "release"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/${NXP_CST_BASE_PATH}/linux64/bin/cst ${D}${bindir}/cst
    install -m 0755 ${B}/${NXP_CST_BASE_PATH}/linux64/bin/srktool ${D}${bindir}/srktool
    install -m 0755 ${B}/${NXP_CST_BASE_PATH}/keys/hab4_pki_tree.sh ${D}${bindir}/hab4_pki_tree.sh
}

FILES_${PN} = "${bindir}"
BBCLASSEXTEND = "native"
