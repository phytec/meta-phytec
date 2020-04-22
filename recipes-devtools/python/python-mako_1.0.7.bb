require python-mako.inc

inherit setuptools3

RDEPENDS_${PN} = "python-threading \
                  python-netclient \
                  python-html \
"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
