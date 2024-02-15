DEPENDS = "openssl-native byacc-native flex-native"

REQUIRED_DISTRO_FEATURES = "secureboot"
inherit features_check

SRC_URI = " \
    git://gitlab.apertis.org/pkg/imx-code-signing-tool.git;protocol=https;branch=${BRANCH} \
    file://cst-wrapper.sh \
"

S = "${WORKDIR}/git"

CST_KEY_SOURCE ??= "file"

EXTRA_OEMAKE = 'CC="${CC}" LD="${CC}" AR="${AR}" OBJCOPY="${OBJCOPY}"'

do_compile() {
    oe_runmake -C code/obj.linux64 OSTYPE=linux64 ENCRYPTION=yes COPTIONS="${CFLAGS} ${CPPFLAGS}" LDOPTIONS="${LDFLAGS}"
    oe_runmake -C add-ons/hab_csf_parser COPTS="${CFLAGS} ${CPPFLAGS} ${LDFLAGS}"
}

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/code/obj.linux64/cst ${D}${bindir}/cst-bin
    install -m 0755 ${S}/code/obj.linux64/srktool ${D}${bindir}/srktool
    install -m 755 ${S}/add-ons/hab_csf_parser/csf_parser ${D}${bindir}

    install -m 0755 ${WORKDIR}/cst-wrapper.sh ${D}${bindir}/cst
}

FILES:${PN} = "${bindir}"
BBCLASSEXTEND = "native nativesdk"
