# Copyright (C) 2014 Stefan Mueller-Klieser <s.mueller-klieser@phytec.de>
# PHYTEC Messtechnik GmbH
DESCRIPTION = "Linux Kernel provided and supported by PHYTEC based on Linus Kernel"

require linux.inc

SRC_URI = "${GITSERVER}/linux-mainline;branch=${BRANCH}"
SRCREV = "${AUTOREV}"
PV = "${LINUX_VERSION}-git${SRCPV}"
BRANCH ??= "UNASSIGNED"
LINUX_VERSION ??= "UNASSIGNED"
