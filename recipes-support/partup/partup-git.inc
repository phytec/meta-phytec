SRC_URI = "git://github.com/phytec/partup;branch=main;protocol=https"
SRCREV = "80d9dab8f34d54a1e8edca570d29b27011f84e7a"

UPSTREAM_CHECK_COMMITS = "1"

PV = "4.0.0+git${SRCPV}"
S = "${WORKDIR}/git"

DEFAULT_PREFERENCE = "-1"
