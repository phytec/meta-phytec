DEPENDS = "openssl byacc-native flex-native"

REQUIRED_DISTRO_FEATURES = "secureboot"
inherit features_check

S = "${WORKDIR}/cst-${PV}"

CST_KEY_SOURCE ??= "file"

do_compile() {
    # Build a PKCS11-enabled version if necessary, else use prebuilt binaries
    if [ "${CST_KEY_SOURCE}" = "token" ] ; then
        cd "${S}/code/cst"
        make OSTYPE=linux64 rel_bin
        mkdir -p ${S}/linux64/lib
        cp "${S}/code/cst/code/obj.linux64/libfrontend.a" "${S}/linux64/lib"
        cd "${S}/code/back_end-engine/src"
        make
    fi
}

do_install() {
    install -d ${D}${bindir}
    if [ "${CST_KEY_SOURCE}" = "token" ] ; then
        install -m 0755 ${S}/code/back_end-engine/src/cst ${D}${bindir}/cst
        install -m 0755 ${S}/code/cst/release/linux64/bin/srktool ${D}${bindir}/srktool
    else
        install -m 0755 ${S}/linux64/bin/cst ${D}${bindir}/cst
        install -m 0755 ${S}/linux64/bin/srktool ${D}${bindir}/srktool
    fi
    install -m 0755 ${S}/keys/hab4_pki_tree.sh ${D}${bindir}/hab4_pki_tree.sh
}

FILES_${PN} = "${bindir}"
BBCLASSEXTEND = "native"
