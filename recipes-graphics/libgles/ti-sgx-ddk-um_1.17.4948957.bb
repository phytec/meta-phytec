DESCRIPTION = "Userspace libraries for PowerVR SGX chipset on TI SoCs"
HOMEPAGE = "https://git.ti.com/graphics/omap5-sgx-ddk-um-linux"
LICENSE = "TI-TSPA"
LIC_FILES_CHKSUM = "file://TI-Linux-Graphics-DDK-UM-Manifest.doc;md5=b17390502bc89535c86cfbbae961a2a8"

inherit features_check

REQUIRED_MACHINE_FEATURES = "gpu"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "ti33x"

PR = "r37"

BRANCH = "ti-img-sgx/dunfell/${PV}"

SRC_URI = "\
    git://git.ti.com/git/graphics/omap5-sgx-ddk-um-linux.git;protocol=https;branch=${BRANCH} \
    file://0001-rc.pvr-add-proper-error-return-code.patch \
    file://rc.pvr.service \
"
SRCREV = "742cf38aba13e1ba1a910cf1f036a1a212c263b6"

TARGET_PRODUCT:ti33x = "ti335x"

SYSTEMD_SERVICE:${PN} = "rc.pvr.service"

INITSCRIPT_NAME = "rc.pvr"
INITSCRIPT_PARAMS = "defaults 8"

inherit update-rc.d

PROVIDES += "virtual/egl virtual/libgles1 virtual/libgles2 virtual/libgbm"

DEPENDS += "libdrm udev wayland wayland-protocols libffi expat"
DEPENDS:append:libc-musl = " gcompat"
RDEPENDS:${PN} += "libdrm libdrm-omap udev wayland wayland-protocols libffi expat"

RPROVIDES:${PN} = "libegl libgles1 libgles2 libgbm"
RPROVIDES:${PN}-dev = "libegl-dev libgles1-dev libgles2-dev libgbm-dev"
RPROVIDES:${PN}-dbg = "libegl-dbg libgles1-dbg libgles2-dbg libgbm-dbg"

RREPLACES:${PN} = "libegl libgles1 libgles2 libgbm"
RREPLACES:${PN}-dev = "libegl-dev libgles1-dev libgles2-dev libgbm-dev"
RREPLACES:${PN}-dbg = "libegl-dbg libgles1-dbg libgles2-dbg libgbm-dbg"

RCONFLICTS:${PN} = "libegl libgles1 libgles2 libgbm"
RCONFLICTS:${PN}-dev = "libegl-dev libgles1-dev libgles2-dev libgbm-dev"
RCONFLICTS:${PN}-dbg = "libegl-dbg libgles1-dbg libgles2-dbg libgbm-dbg"

# The actual SONAME is libGLESv2.so.2, so need to explicitly specify RPROVIDES for .so.1 here
RPROVIDES:${PN} += "libGLESv2.so.1"

RRECOMMENDS:${PN} += "ti-sgx-ddk-km"

S = "${WORKDIR}/git"

do_install () {
    oe_runmake install DESTDIR=${D} TARGET_PRODUCT=${TARGET_PRODUCT}
    ln -sf libGLESv2.so.2 ${D}${libdir}/libGLESv2.so.1

    chown -R root:root ${D}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/rc.pvr.service \ 
        ${D}$systemd_system_unitdir
}

FILES:${PN} =  " \
    ${bindir}/* \
    ${libdir}/* \
    ${includedir}/* \
    ${sysconfdir}/* \
    ${systemd_system_unitdir}/* \
"

INSANE_SKIP:${PN} += "dev-so ldflags useless-rpaths"
INSANE_SKIP:${PN} += "already-stripped dev-deps"

CLEANBROKEN = "1"
