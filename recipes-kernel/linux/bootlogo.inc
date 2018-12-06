# Copyright (C) 2018 PHYTEC Messtechnik GmbH,
# Author: Jan Remmet  <j.remmet@phytec.de>

# the fist picture found in SRC_URI, will be  convert and used as bootlogo
# the picture size should match the lcd resolution

python do_add_logo () {
    logo = os.path.join(d.getVar('STAGING_KERNEL_DIR', True),
                        "drivers/video/logo/logo_linux_clut224.ppm")
    for s in src_patches(d, True):
        if s.endswith(('.bmp', '.ppm', '.png')):
            if os.system('convert.im7 -colors 224 -format ppm -compress none \
                %(pic)s %(dest)s' %
                { 'pic': s, 'dest': logo}):
                bb.error("imagemagick convert failed")
            break
}

do_add_logo[depends] += "imagemagick-native:do_populate_sysroot"
addtask add_logo after do_patch before do_compile

