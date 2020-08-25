SUMMARY = "Linux Firmware Loader Daemon"
DESCRIPTION = "The Linux Firmware Loader Daemon monitors the kernel for \
firmware requests and uploads the firmware blobs it has via the sysfs \
interface."
HOMEPAGE = "https://github.com/teg/firmwared"
PR = "r0"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-APACHE;md5=7b486c2338d225a1405d979ed2c15ce8 \
                    file://COPYING;md5=daa868b8e1ae17d03228a1145b4060da"

SRC_URI = "git://github.com/teg/firmwared.git \
           file://firmwared.service"

PV = "1+git${SRCPV}"
SRCREV = "2e6b5db43d63a5c0283a4cae9a6a20b7ad107a04"

S = "${WORKDIR}/git"

DEPENDS = "glib-2.0 systemd"

inherit pkgconfig autotools systemd

SYSTEMD_SERVICE_${PN} = "firmwared.service"

do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/firmwared.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@BINDIR@,${bindir},g' ${D}${systemd_unitdir}/system/firmwared.service
}
