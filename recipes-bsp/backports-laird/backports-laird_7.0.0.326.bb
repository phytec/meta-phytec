SUMMARY = "Laird Connectivity Wi-Fi Backports for LWB"

BACKPORTS_FILE = "${PN}-${PV}.tar.bz2"

LRD_LWB_URI_BASE = "https://github.com/LairdCP/Sterling-LWB-and-LWB5-Release-Packages/releases/download/LRD-REL-${PV}"

SRC_URI += "${LRD_LWB_URI_BASE}/${BACKPORTS_FILE}"
SRC_URI += "file://0001-backports-suppress-attribute-cold-warnings.patch;striplevel=2"

SRC_URI[md5sum] = "3fb119ea9efa0e190023e4db5f022467"
SRC_URI[sha256sum] = "5f2c9c7c82859a21bfb8ae3af7b71da49b132d144b97b70858aa0daddd4d8642"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

inherit module

DEPENDS += "coreutils-native bison-native flex-native"
RDEPENDS_${PN} = "packagegroup-bluetooth"

BACKPORTS_CONFIG = "defconfig-lwb"

S = "${WORKDIR}/laird-backport-${PV}"

EXTRA_OEMAKE = "\
	KLIB_BUILD=${STAGING_KERNEL_DIR} \
	KLIB=${D} \
	DESTDIR=${D} \
	KERNEL_CONFIG=${STAGING_KERNEL_BUILDDIR}/.config \
	BACKPORT_DIR=${S} \
	M=${S} \
	"

do_compile_prepend() {
	rm -f ${S}/.kernel_config_md5
	oe_runmake CC=${BUILD_CC} ${BACKPORTS_CONFIG}
}

do_install_prepend () {
	cd ${STAGING_KERNEL_DIR}
}
