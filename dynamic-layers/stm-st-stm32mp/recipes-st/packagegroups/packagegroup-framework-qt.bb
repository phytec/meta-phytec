SUMMARY = "Framework qt components"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup distro_features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PROVIDES = "${PACKAGES}"
PACKAGES = "\
            packagegroup-framework-qt            \
            packagegroup-framework-qt-examples   \
            "

RDEPENDS_packagegroup-framework-qt = "\
    qtbase                          \
    liberation-fonts                \
    \
    qtdeclarative                   \
    qt-launcher                     \
    "

SUMMARY_packagegroup-framework-qt-examples = "Framework qt components for examples"
RDEPENDS_packagegroup-framework-qt-examples = "\
    qtbase-examples         \
    \
    qtdeclarative-examples  \
"
