FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"


SRC_URI += " \
	file://0003-phycore-update-r1.1.0.patch \
        file://0004-add-phycore-stm32mp1-4-machine-r1.1.0.patch \
        file://0005-add-phycore-stm32mp1-5-machine-r1.1.0.patch \
        file://0006-add-phycore-stm32mp1-6-machine-r1.1.0.patch \
        file://0007-add-phycore-stm32mp1-7-machine-r1.1.0.patch \
        file://0008-update-DTS-for-800MHz-CPU-version.patch \
"

SRC_URI_class-devupstream = "git://git@git.phytec.de/tf-a-stm32mp;protocol=ssh;branch=v2.0-phy"
SRCREV_class-devupstream = "70f33a30f81ee9cffe6c3f28a9a01efa46d83004"
SRCREV_FORMAT_class-devupstream = "tfa"
PV_class-devupstream = "${TF_VERSION}+git+${SRCPV}"
# ---------------------------------
# Configure default preference to manage dynamic selection between tarball and git repository
# ---------------------------------
STM32MP_SOURCE_SELECTION ?= "tarball"

DEFAULT_PREFERENCE = "${@bb.utils.contains('STM32MP_SOURCE_SELECTION', 'git.phytec', '-1', '1', d)}"

