FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://0001-Makefiles-Fix-arch-assignement.patch \
"
