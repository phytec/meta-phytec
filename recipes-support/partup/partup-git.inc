SRC_URI = "git://github.com/phytec/partup;branch=main;protocol=https"
SRCREV = "ffa8b7d51614f59599438c41d27b2f5bc4e97cb0"

UPSTREAM_CHECK_COMMITS = "1"

PV = "2.0.0+git${SRCPV}"
S = "${WORKDIR}/git"

DEFAULT_PREFERENCE = "-1"
