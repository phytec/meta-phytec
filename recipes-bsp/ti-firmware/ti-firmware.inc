LICENSE = "TI-TFL"
LIC_FILES_CHKSUM = "file://LICENSE.ti;md5=b5aebf0668bdf95621259288c4a46d76"

PV = "11.01.05"
PR = "r0"

SRC_URI = "git://git.ti.com/git/processor-firmware/ti-linux-firmware.git;protocol=https;branch=ti-linux-firmware"
SRCREV = "92a31c8e9ecb5671371cc3a48eb5a4b8340221a4"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
CLEANBROKEN = "1"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP += "arch"

do_compile[noexec] = "1"
do_configure[noexec] = "1"
