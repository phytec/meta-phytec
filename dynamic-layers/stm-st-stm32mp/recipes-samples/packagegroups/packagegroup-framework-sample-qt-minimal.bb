SUMMARY = "Framework qt minimal components"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup distro_features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PROVIDES = "${PACKAGES}"
PACKAGES = "\
            packagegroup-framework-sample-qt-minimal    \
            packagegroup-framework-sample-qt-minimal-examples   \
            "

RDEPENDS_packagegroup-framework-sample-qt-minimal = "\
    qtbase                          \
    liberation-fonts                \
    \
    qtdeclarative                   \
    openstlinux-qt-eglfs            \
    "

SUMMARY_packagegroup-framework-sample-qt-minimal-examples = "Framework qt components for examples"
RDEPENDS_packagegroup-framework-sample-qt-minimal-examples = "\
    qtbase-examples         \
    \
    qtdeclarative-examples  \
"
