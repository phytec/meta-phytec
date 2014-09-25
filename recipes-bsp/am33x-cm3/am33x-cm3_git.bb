# taken from meta-ti 9c2ae7b791a08ad95f31413dcce594a7ca1c9601
# bugfix added from mailinglist
DESCRIPTION = "Cortex-M3 binary blob for suspend-resume"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://License.txt;md5=7bdc54a749ab7a7dea999d25d99a41b8"

PV = "1.8.7"
PR = "r1"
PE = "1"

SRCREV = "fffeab4236d4129ab046bb7081a8ac244134ad89"
BRANCH ?= "next"

SRC_URI = "git://git.ti.com/ti-cm3-pm-firmware/amx3-cm3.git;protocol=git;branch=${BRANCH}"

S = "${WORKDIR}/git"

FLOATABI = "${@base_contains("TUNE_FEATURES", "vfp", base_contains("TUNE_FEATURES", "callconvention-hard", " -mfloat-abi=hard", " -mfloat-abi=softfp", d), "" ,d)}"

do_compile() {
	make CROSS_COMPILE="${TARGET_PREFIX}" CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS} ${FLOATABI}"
}

do_install() {
	install -d ${D}${base_libdir}/firmware
	install -m 0644 bin/am335x-pm-firmware.elf ${D}${base_libdir}/firmware/am335x-pm-firmware.bin
	install -m 0644 bin/*-scale-data.bin ${D}${base_libdir}/firmware/
}

FILES_${PN} += "${base_libdir}/firmware"
