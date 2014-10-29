# Copyright (C) 2014 Stefan Müller-Klieser <s.mueller-klieser@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Hardware development tools used on Phytec boards"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS_${PN} = " \
    usbutils \
    ethtool \
    i2c-tools \
    alsa-utils \
    devmem2 \
    iw \
    bc \
    tslib-conf \
    tslib-calibrate \
    tslib-tests \
    fb-test \
    memedit \
    mtd-utils \
    mtd-utils-ubifs \
    mtd-utils-misc \
"

