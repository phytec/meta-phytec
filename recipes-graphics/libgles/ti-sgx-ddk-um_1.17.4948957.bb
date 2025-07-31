DESCRIPTION = "Userspace libraries for PowerVR SGX chipset on TI SoCs"
HOMEPAGE = "https://git.ti.com/graphics/omap5-sgx-ddk-um-linux"
LICENSE = "TI-TSPA"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7232b98c1c58f99e3baa03de5207e76f"

inherit features_check systemd

REQUIRED_MACHINE_FEATURES = "gpu"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "ti33x"

PR = "r38"

BRANCH = "${PV}/mesa/glibc-2.35"

SRC_URI = "\
    git://git.ti.com/git/graphics/omap5-sgx-ddk-um-linux.git;protocol=https;branch=${BRANCH} \
"
SRCREV = "84a396a4fb379f10931421e489ac8a199d6a9f2c"

TARGET_PRODUCT:ti33x = "ti335x_linux"

SYSTEMD_SERVICE:${PN} = "pvrsrvctl.service"

RDEPENDS:${PN} += "libdrm"

RRECOMMENDS:${PN} += "ti-sgx-ddk-km"

S = "${WORKDIR}/git"

do_install () {
    oe_runmake install DESTDIR=${D} TARGET_PRODUCT=${TARGET_PRODUCT} SYSTEMD=true
}

FILES:${PN} =  " \
    ${bindir}/* \
    ${libdir}/* \
    ${systemd_system_unitdir}/* \
"

# No debug or dev packages for this recipe
PACKAGES = "${PN}"

INSANE_SKIP:${PN} += "ldflags"
INSANE_SKIP:${PN} += "already-stripped dev-so"
