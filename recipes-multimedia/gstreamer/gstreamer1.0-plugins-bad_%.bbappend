FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " \
       file://0001-force-modesetting-true.patch \
"
