FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-config:"

SRC_URI += "file://defconfig"
SRC_URI += "file://envtool-target.cfg"

COMPATIBLE_MACHINE = "am335x"
