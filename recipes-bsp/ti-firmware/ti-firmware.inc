LICENSE = "TI-TFL"
LIC_FILES_CHKSUM = "file://LICENSE.ti;md5=b5aebf0668bdf95621259288c4a46d76"

PV = "12.00.00.07"
PR = "r0"

SRC_URI = "git://git.ti.com/git/processor-firmware/ti-linux-firmware.git;protocol=https;branch=ti-linux-firmware"
SRCREV = "9e9d50ff563f83db86d36b72cd7fb4f487d6b414"

PACKAGE_ARCH = "${MACHINE_ARCH}"
CLEANBROKEN = "1"

INHIBIT_DEFAULT_DEPS = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "arch"

do_compile[noexec] = "1"
do_configure[noexec] = "1"
