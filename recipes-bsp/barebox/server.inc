BRANCH ??= "NOTASSIGNED"
SRC_URI = "git://git.phytec.de/barebox;branch=${BRANCH}"
# floating revision
SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"
PV ??= "NOTASSIGNED-git${SRCPV}"
COMPATIBLE_MACHINE_ti33x = "(ti33x)"
