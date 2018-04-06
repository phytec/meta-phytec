# Copyright (C) 2018 PHYTEC Messtechnik GmbH,
# Author: Stefan Riedmueller <s.riedmueller@phytec.de>

inherit phygittag
inherit buildinfo
include linux-common.inc

GIT_URL = "git://git.phytec.de/${PN}"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

PR = "${INC_PR}.0"

# NOTE: PV must be in the format "x.y.z-.*". It cannot begin with a 'v'.
# NOTE: Keep version in filename in sync with commit id!
SRCREV = "922d055cddf889b3fe152f4aab7c252a4a168982"

S = "${WORKDIR}/git"

INTREE_DEFCONFIG = "imx_v6_v7_defconfig"

COMPATIBLE_MACHINE  =  "phyflex-imx6-1"
COMPATIBLE_MACHINE .= "|phyflex-imx6-2"
COMPATIBLE_MACHINE .= "|phyflex-imx6-3"
COMPATIBLE_MACHINE .= "|phyflex-imx6-4"
COMPATIBLE_MACHINE .= "|phyflex-imx6-5"
COMPATIBLE_MACHINE .= "|phyflex-imx6-6"
COMPATIBLE_MACHINE .= "|phyflex-imx6-7"
COMPATIBLE_MACHINE .= "|phyflex-imx6-8"
COMPATIBLE_MACHINE .= "|phyflex-imx6-9"
COMPATIBLE_MACHINE .= "|phyflex-imx6-10"

COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-3"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-4"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-5"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-6"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-7"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-9"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-10"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-11"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-12"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-13"
SRC_URI_append_phyboard-mira-imx6-13 = " \
    file://0001-mira-peb-eval-01-mux-gpios-and-do-not-load-driver.patch \
    file://0002-phycore-som-add-spi-port-on-the-expansion-connector.patch \
    file://defconfig \
"
