FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-config:"

SRC_URI_append = " file://defconfig \
                   file://envtool-target.cfg"
COMPATIBLE_MACHINE = "am335x"
