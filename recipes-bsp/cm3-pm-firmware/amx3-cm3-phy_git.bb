DESCRIPTION = "Cortex-M3 binary blob for suspend-resume"

LICENSE = "TI-TSPA"
LIC_FILES_CHKSUM = "file://License.txt;md5=7bdc54a749ab7a7dea999d25d99a41b8"

PROVIDES += "amx3-cm3"

PV = "1.9.2"
PR = "r0"

SRCREV = "fb484c5e54f2e31cf0a338d2927a06a2870bcc2c"
BRANCH ?= "ti-v4.1.y"

SRC_URI = "git://git.ti.com/processor-firmware/ti-amx3-cm3-pm-firmware.git;protocol=git;branch=${BRANCH}"
SRC_URI += "file://am335x-pcm060-scale-data.bin"

S = "${WORKDIR}/git"

do_compile() {
	make CROSS_COMPILE="${TARGET_PREFIX}" CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS} ${SECURITY_NOPIE_CFLAGS}"
}

do_install() {
	install -d ${D}${base_libdir}/firmware
	install -m 0644 bin/am335x-pm-firmware.elf ${D}${base_libdir}/firmware/
	install -m 0644 bin/*-scale-data.bin ${D}${base_libdir}/firmware/
	install -m 0644 ${WORKDIR}/am335x-pcm060-scale-data.bin ${D}${base_libdir}/firmware/
}

RPROVIDES_${PN} = "amx3-cm3"
FILES_${PN} += "${base_libdir}/firmware"
