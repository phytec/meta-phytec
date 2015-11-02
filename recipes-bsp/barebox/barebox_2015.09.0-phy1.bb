inherit phygittag
inherit buildinfo
require barebox.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/features:"
FILESEXTRAPATHS_prepend := "${THISDIR}/env-2015.09.0-phy1:"

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://boardenv \
    file://machineenv \
    file://targettools.cfg \
"
S = "${WORKDIR}/git"

PR = "${INC_PR}.2"

# NOTE: Keep version in filename in sync with commit id and barebox-ipl!
SRCREV = "d402a392053daacb7cda698078e0b417425571d2"

COMPATIBLE_MACHINE = "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-maia-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-3"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-am335x-6"
COMPATIBLE_MACHINE .= "|phycore-am335x-7"
COMPATIBLE_MACHINE .= "|phyflex-am335x-1"
COMPATIBLE_MACHINE .= "|phyflex-am335x-2"
