BRANCH_ti33x ??= "NOTASSIGNED"
SRC_URI_ti33x = "git://git.phytec.de/barebox;branch=${BRANCH}"
# floating revision
SRCREV_ti33x = "${AUTOREV}"
S_ti33x = "${WORKDIR}/git"
PV_ti33x ??= "NOTASSIGNED-git${SRCPV}"
COMPATIBLE_MACHINE_ti33x = "(ti33x)"
