inherit deploy

DESCRIPTION = "i.MX System Manager Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b66f32a90f9577a5a3255c21d79bc619"

SRC_URI = "${IMX_SM_SRC};branch=${BRANCH}"
IMX_SM_SRC = "git://github.com/phytec/imx-sm-phytec.git;protocol=https"

BRANCH = "master-phy"
BRANCH:use-nxp-bsp = "6.6.52-2.2.0-phy"
SRCREV = "${AUTOREV}"
SRCREV:use-nxp-bsp = "5e39ce5d178219871cb48de9bf84cd22459279e0"


# Set generic compiler for system manager core
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "gcc-arm-none-eabi-native"
PROVIDES += "virtual/imx-system-manager"

PACKAGE_ARCH = "${MACHINE_ARCH}"

# Set monitor mode for none, one, or two
PACKAGECONFIG[m0] = "M=0,,,,,m1 m2"
PACKAGECONFIG[m1] = ",,,,,m0 m2"
PACKAGECONFIG[m2] = "M=2,,,,,m0 m1"
# m2 sets the MONITOR_MODE to 2
PACKAGECONFIG ??= "m2"

LDFLAGS[unexport] = "1"

EXTRA_OEMAKE = " \
    V=y \
    CROSS_COMPILE=arm-none-eabi- \
    ${PACKAGECONFIG_CONFARGS} \
"

do_configure() {
    oe_runmake config=${SYSTEM_MANAGER_CONFIG} clean
    oe_runmake config=${SYSTEM_MANAGER_CONFIG} cfg
}

do_compile() {
    oe_runmake config=${SYSTEM_MANAGER_CONFIG}
}

do_install[noexec] = "1"

addtask deploy after do_compile
do_deploy() {
    install -D -p -m 0644 \
        ${B}/build/${SYSTEM_MANAGER_CONFIG}/${SYSTEM_MANAGER_FIRMWARE_BASENAME}.bin \
        ${DEPLOYDIR}/${SYSTEM_MANAGER_FIRMWARE_BASENAME}.bin
}

COMPATIBLE_MACHINE = "(mx95-generic-bsp|imx95-libra-fpsc-1)"
