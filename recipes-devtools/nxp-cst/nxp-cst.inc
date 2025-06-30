DEPENDS += "openssl byacc flex"

LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = " \
    file://LICENSE.bsd3;md5=14aba05f9fa6c25527297c8aac95fcf6 \
    file://LICENSE.hidapi;md5=e0ea014f523f64f0adb13409055ee59e \
    file://LICENSE.openssl;md5=3441526b1df5cc01d812c7dfc218cea6 \
"

SRC_URI = " \
    git://gitlab.apertis.org/pkg/imx-code-signing-tool.git;protocol=https;branch=${BRANCH} \
"


CST_KEY_SOURCE ??= "file"

EXTRA_OEMAKE = 'CC="${CC}" LD="${CC}" AR="${AR}" OBJCOPY="${OBJCOPY}"'

do_compile() {
    oe_runmake -C code/obj.linux64 OSTYPE=linux64 ENCRYPTION=yes COPTIONS="${CFLAGS} ${CPPFLAGS}" LDOPTIONS="${LDFLAGS}"
    oe_runmake -C add-ons/hab_csf_parser COPTS="${CFLAGS} ${CPPFLAGS} ${LDFLAGS}"
}

do_install() {
    install -d ${D}${bindir}

    install -m 0755 ${S}/code/obj.linux64/cst ${D}${bindir}/cst
    install -m 0755 ${S}/code/obj.linux64/srktool ${D}${bindir}/srktool
    install -m 755 ${S}/add-ons/hab_csf_parser/csf_parser ${D}${bindir}
}

FILES:${PN} = "${bindir}"
BBCLASSEXTEND = "native nativesdk"
