inherit phygittag
inherit deploy

SUMMARY = "i.MX Optional Execution Image"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b66f32a90f9577a5a3255c21d79bc619"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "gcc-arm-none-eabi-native"
PROVIDES = "virtual/imx-oei"

SRC_URI = "${IMX_OEI_SRC};branch=${BRANCH}"
IMX_OEI_SRC ?= "git://github.com/phytec/imx-oei-phytec.git;protocol=https"
BRANCH = "master-phy"
BRANCH:use-nxp-bsp = "6.12.34-2.1.0-phy"
SRCREV = "${AUTOREV}"
SRCREV:use-nxp-bsp = "22e056d73e7d7787e6f48f42c5eb24321a66cfb9"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

OEI_CONFIGS ?= "UNDEFINED"
OEI_CORE    ?= "UNDEFINED"
OEI_SOC     ?= "UNDEFINED"
OEI_BOARD   ?= "UNDEFINED"

LDFLAGS[unexport] = "1"

EXTRA_OEMAKE = "\
    board=${OEI_BOARD} \
    R=${IMX_SOC_REV} \
    CROSS_COMPILE=arm-none-eabi-"

do_configure() {
    for oei_config in ${OEI_CONFIGS}; do
        oe_runmake clean oei=$oei_config
    done
}

do_compile() {
    for oei_config in ${OEI_CONFIGS}; do
        oe_runmake oei=$oei_config
    done
}

do_install() {
    install -d ${D}/firmware
    for oei_config in ${OEI_CONFIGS}; do
        install -m 0644 ${B}/build/${OEI_BOARD}/$oei_config/oei-*.bin ${D}/firmware
    done
}

addtask deploy after do_install
do_deploy() {
    install -m 0644 ${D}/firmware/* ${DEPLOYDIR}/
}

FILES:${PN} = "/firmware"
SYSROOT_DIRS += "/firmware"

COMPATIBLE_MACHINE = "^(mx95-generic-bsp)"
