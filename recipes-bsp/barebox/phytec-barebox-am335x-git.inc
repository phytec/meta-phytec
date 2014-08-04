BRANCH_am335x = "NOTASSIGNED"
SRC_URI_am335x = "git://${HOME}/barebox-am335x;protocol=file;branch=${BRANCH}"
# floating revision
SRCREV_am335x = "${AUTOREV}"
S_am335x = "${WORKDIR}/git"
PV_am335x = "2013.05.1phy-git${SRCPV}"
#PR_am335x = ""
COMPATIBLE_MACHINE_am335x = "(am335x)"

