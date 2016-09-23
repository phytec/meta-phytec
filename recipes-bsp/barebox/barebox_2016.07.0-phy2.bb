inherit phygittag
inherit buildinfo
require barebox.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/env-2016.07.0-phy2:"

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI_append = " \
    file://commonenv \
    file://boardenv \
    file://machineenv \
"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id and barebox-ipl!
SRCREV = "68979cfa3e050163d56102038166634cb1c1eb34"

DEPENDS += "u-boot-mkimage-native"

do_compile_append_rk3288 () {
	mkimage -A arm -T firmware -C none -O u-boot -a 0x02000000 -e 0 -n "barebox image" -d ${B}/${BAREBOX_BIN} ${B}/${BAREBOX_BIN}.u-boot
}

do_install_append_rk3288 () {
	install ${B}/${BAREBOX_BIN}.u-boot ${D}${base_bootdir}/${BAREBOX_IMAGE_BASE_NAME}.bin.u-boot
	ln -sf ${BAREBOX_IMAGE_BASE_NAME}.bin.u-boot ${D}${base_bootdir}/${BAREBOX_BIN_SYMLINK}.u-boot
}

do_deploy_append_rk3288 () {
	install -m 644 ${B}/${BAREBOX_BIN}.u-boot ${DEPLOYDIR}/${BAREBOX_IMAGE_BASE_NAME}.bin.u-boot
	ln -sf ${BAREBOX_IMAGE_BASE_NAME}.bin.u-boot ${DEPLOYDIR}/${BAREBOX_BIN_SYMLINK}.u-boot
}

COMPATIBLE_MACHINE = "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-3"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-4"
COMPATIBLE_MACHINE .= "|phyboard-wega-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-am335x-6"
COMPATIBLE_MACHINE .= "|phycore-am335x-7"
COMPATIBLE_MACHINE .= "|phyboard-regor-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-rk3288-1"
COMPATIBLE_MACHINE .= "|phycore-rk3288-2"
