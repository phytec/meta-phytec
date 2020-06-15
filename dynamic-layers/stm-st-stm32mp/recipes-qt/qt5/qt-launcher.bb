DESCRIPTION = "QT Application Launcher"
HOMEPAGE = ""
LICENSE = "GPLv2 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://res-touchscreen.rules \
            file://qtLauncher \
            file://eglfs_kms.config \
"

do_install_append () {
	install -d ${D}${nonarch_base_libdir}/udev/rules.d
	install -m 0644 ${WORKDIR}/res-touchscreen.rules ${D}${nonarch_base_libdir}/udev/rules.d/

	install -d ${D}/${sysconfdir}
	install -m 0644 ${WORKDIR}/eglfs_kms.config ${D}/${sysconfdir}/eglfs_kms.config

	install -d ${D}/${bindir}
	install -m 0755 ${WORKDIR}/qtLauncher ${D}/${bindir}/qtLauncher
	sed -i 's,@QT_QPA_PLATFORM@,${QT_QPA_PLATFORM},g' ${D}/${bindir}/qtLauncher
	sed -i 's,@QT_QPA_FB_DRM@,${QT_QPA_FB_DRM},g' ${D}/${bindir}/qtLauncher
	sed -i 's,@QT_QPA_EGLFS_KMS_CONFIG@,${sysconfdir}/eglfs_kms.config,g' ${D}/${bindir}/qtLauncher
}

