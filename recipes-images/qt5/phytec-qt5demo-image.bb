# Copyright (C) 2014 Stefan Müller-Klieser <s.mueller-klieser@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)
DESCRIPTION = "This image is demoing qt5 on phytecs imx6 boards"

LICENSE = "MIT"

inherit core-image

#unnacessary:
#    gcc g++ binutils libgcc libgcc-dev libstdc++ libstdc++-dev libstdc++-staticdev \
#    autoconf automake ccache chkconfig glib-networking glibmm \
#    boost cmake zlib glib-2.0 \
#    packagegroup-fsl-tools-testapps 
#    git glive \
#    packagegroup-core-buildessential pkgconfig  \
#    ruby \
#    cairo pango fontconfig freetype pulseaudio dbus \
#    alsa-lib alsa-tools alsa-state alsa-utils-alsaconf
#
#
#  builderror with 3.17 kernel
#   packagegroup-fsl-gstreamer \
#    gst-fsl-plugin \
#   fsl-alsa-plugins \
#

IMAGE_INSTALL = " \
    packagegroup-core-boot ${ROOTFS_PKGMANAGE_BOOTSTRAP} ${CORE_IMAGE_EXTRA_INSTALL} \
    packagegroup-fsl-tools-gpu \
    ${@base_contains('DISTRO_FEATURES', 'directfb', 'packagegroup-core-directfb', '', d)} \
    ${@base_contains('DISTRO_FEATURES', 'wayland', 'weston weston-init weston-examples \
                      gtk+3-demo clutter-1.0-examples', '', d)} \
    cpufrequtils \
    nano \
    gdb \
    gstreamer \
    gst-meta-video \
    gst-plugins-base-app \
    gst-plugins-base \
    gst-plugins-good \
    gst-plugins-good-rtsp \
    gst-plugins-good-udp \
    gst-plugins-good-rtpmanager \
    gst-plugins-good-rtp \
    gst-plugins-good-video4linux2 \
    openssh-sftp-server \
    packagegroup-fsl-tools-benchmark \
    imx-vpu \
    qtbase-plugins \
    qtbase-tools \
    qtbase-examples \
    qtdeclarative \
    qtdeclarative-plugins \
    qtdeclarative-tools \
    qtdeclarative-examples \
    qtdeclarative-qmlplugins \
    qtmultimedia \
    qtmultimedia-plugins \
    qtmultimedia-examples \
    qtmultimedia-qmlplugins \
    qtsvg \
    qtsvg-plugins \
    qtsensors \
    qtimageformats-plugins \
    qtsystems \
    qtsystems-tools \
    qtsystems-examples \
    qtsystems-qmlplugins \
    qtscript \
    qt3d \
    qt3d-examples \
    qt3d-qmlplugins \
    qt3d-tools \
    qtwebkit \
    qtwebkit-examples-examples \
    qtwebkit-qmlplugins \
    qtgraphicaleffects-qmlplugins \
    qtconnectivity-qmlplugins \
    qtlocation-plugins \
    qtlocation-qmlplugins \
    cinematicexperience \
    i2c-tools \
"
