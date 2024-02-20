SRC_URI = "git://github.com/phytec/partup;branch=main;protocol=https"
SRCREV = "b95bad357340370750a3269b8de4d1269d448305"

UPSTREAM_CHECK_COMMITS = "1"

PV = "1.0.0+git${SRCPV}"
S = "${WORKDIR}/git"

DEFAULT_PREFERENCE = "-1"
