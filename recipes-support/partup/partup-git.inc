SRC_URI = "git://github.com/phytec/partup;branch=main;protocol=https"
SRCREV = "35ea1f25963454d6549d8778c66d04caa8fa217f"

UPSTREAM_CHECK_COMMITS = "1"

PV = "2.1.0+git${SRCPV}"
S = "${WORKDIR}/git"

DEFAULT_PREFERENCE = "-1"
