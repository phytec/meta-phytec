# Copyright (C) 2016 PHYTEC Messtechnik GmbH,
# Author: Jan Remmet  <j.remmet@phytec.de>

# the fist picture found in SRC_URI, will be  convert and used as bootlogo
# the picture size should match the lcd resolution

DEPENDS += "imagemagick-native"

do_patch_append () {
    for s in src_patches(d, True):
        if s.endswith(('.bmp', '.ppm', '.png')):
            os.system('convert.im6 -colors 224 -format ppm -compress none \
                %(pic)s %(dest)s' %
                { 'pic': s,
                  'dest': '${S}/drivers/video/logo/logo_linux_clut224.ppm'})
            break
}
