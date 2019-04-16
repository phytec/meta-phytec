FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " \
       file://0001-force-modesetting-true.patch \
       file://0001-opencv-Updated-to-use-new-header-path.patch \
"

DEPENDS += "zbar"

PACKAGECONFIG += " opencv"

EXTRA_OECONF_remove = "--disable-qt"
EXTRA_OECONF_remove = "--disable-zbar"
EXTRA_OECONF += "--enable-zbar "
