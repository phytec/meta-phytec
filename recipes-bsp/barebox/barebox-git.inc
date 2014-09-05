BRANCH_ti33x = "NOTASSIGNED"
SRC_URI_ti33x = "git://${HOME}/barebox-am335x;protocol=file;branch=${BRANCH}"
# floating revision
SRCREV_ti33x = "${AUTOREV}"
S_ti33x = "${WORKDIR}/git"
PV_ti33x = "2014.08.0-phy-git${SRCPV}"
COMPATIBLE_MACHINE_am335x = "(ti33x)"
