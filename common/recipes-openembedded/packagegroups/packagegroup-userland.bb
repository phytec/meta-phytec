# Copyright (C) 2014 Stefan Müller-Klieser <s.mueller-klieser@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Userland softwareservices found in all Phytec BSPs"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS_${PN} = " \
    gdbserver \
    perf \
    openssh \
    openssh-sftp-server \
    gstreamer \
    gst-plugins-base-videoscale \
    gst-plugins-base-ffmpegcolorspace \
    gst-plugins-good-video4linux2 \
    gst-plugins-bad-bayer \
    gst-plugins-bad-fbdevsink \
"

