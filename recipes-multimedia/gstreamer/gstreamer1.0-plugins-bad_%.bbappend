# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

CFLAGS_append_mx6 = " -DLINUX -DEGL_API_FB"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " \
       file://0001-force-modesetting-true.patch \
"
