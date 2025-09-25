SRC_URI = "git://github.com/phytec/partup;branch=main;protocol=https"
SRCREV = "ed48627942285199e0302ec84d91d80b7557dc83"

UPSTREAM_CHECK_COMMITS = "1"

PV = "3.0.0+git${SRCPV}"
S = "${WORKDIR}/git"

DEFAULT_PREFERENCE = "-1"
