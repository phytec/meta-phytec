DEPENDS = "openssl-native byacc-native flex-native"

REQUIRED_DISTRO_FEATURES = "secureboot"
inherit features_check

S = "${WORKDIR}/cst-${PV}"

SRC_URI = " \
        file://cst-wrapper.sh \
        file://openssl3_compatibility.patch \
"

CST_KEY_SOURCE ??= "file"
EXTRA_OEMAKE = 'CC="${CC}" LD="${CC}" AR="${AR}" OBJCOPY="${OBJCOPY}"'

do_compile() {
    # Build a PKCS11-enabled version
     cd "${S}/code/cst"
     oe_runmake OSTYPE=linux64 COPTIONS="${CFLAGS}" LDOPTIONS="${LDFLAGS}" rel_bin
     mkdir -p ${S}/linux64/lib
     cp "${S}/code/cst/code/obj.linux64/libfrontend.a" "${S}/linux64/lib"
     cd "${S}/code/back_end-engine/src"
     mkdir -p ${S}/linux64/lib
     oe_runmake OSTYPE=linux64 COPTIONS="${CFLAGS}" LDOPTIONS="${LDFLAGS}"
}

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/code/back_end-engine/src/cst ${D}${bindir}/cst-pkcs11

    install -m 0755 ${S}/linux64/bin/cst ${D}${bindir}/cst-bin
    install -m 0755 ${S}/linux64/bin/srktool ${D}${bindir}/srktool

    install -m 0755 ${WORKDIR}/cst-wrapper.sh ${D}${bindir}/cst

    install -m 0755 ${S}/keys/hab4_pki_tree.sh ${D}${bindir}/hab4_pki_tree.sh
}

FILES:${PN} = "${bindir}"
BBCLASSEXTEND = "native"
