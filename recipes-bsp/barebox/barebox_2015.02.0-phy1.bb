inherit phygittag
inherit buildinfo
require barebox.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/features:${THISDIR}:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://environment \
    file://targettools.cfg \
"
S = "${WORKDIR}/git"
BAREBOX_LOCALVERSION = "-${BSP_VERSION}"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id and barebox-ipl!
SRCREV = "eb699b609649147f2706b2c86a9599275bdecbdf"

COMPATIBLE_MACHINE = "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-maia-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phyflex-am335x-1"
