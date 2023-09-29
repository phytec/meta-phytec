FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0001-v4l2-video-Allow-v4l2-ctl-p-to-set-the-FPS.patch;patchdir=../.. \
    file://0002-v4l2-video-Allow-the-framerate-to-be-set-from-gstrea.patch;patchdir=../.. \
    file://0003-v4l2-video-Fix-boundary-check-in-s_selection.patch;patchdir=../.. \
"

