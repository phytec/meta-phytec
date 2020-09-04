#!/bin/sh -
# Use the linuxfb plugin
export QT_QPA_PLATFORM=linuxfb

# Use the DRM dumb buffer (rendering is set up via the DRM APIs)
export QT_QPA_FB_DRM=1
