LICENSE = "TI-TFL"
LIC_FILES_CHKSUM = "file://LICENSE.ti;md5=b5aebf0668bdf95621259288c4a46d76"

PV = "11.02.11"
PR = "r0"

SRC_URI = "git://git.ti.com/git/processor-firmware/ti-linux-firmware.git;protocol=https;branch=ti-linux-firmware"
SRCREV = "8ec0c42b8ccf2c9e8bebfd7c4e03d93fed555442"

PACKAGE_ARCH = "${MACHINE_ARCH}"
CLEANBROKEN = "1"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP += "arch"

do_compile[noexec] = "1"
do_configure[noexec] = "1"
