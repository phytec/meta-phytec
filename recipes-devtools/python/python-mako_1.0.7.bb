require python-mako.inc

inherit setuptools3

RDEPENDS:${PN} = "python-threading \
                  python-netclient \
                  python-html \
"
RDEPENDS:${PN}:class-native = ""

BBCLASSEXTEND = "native nativesdk"
