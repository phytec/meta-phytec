FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
           file://0001-boot-src-cmd-custom-scan_overlays-cmd.patch \
"

S = "${WORKDIR}"

