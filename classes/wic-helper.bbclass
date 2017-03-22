# (C) Copyright 2017 Phytec Messtechnik GmbH
# Daniel Schultz <d.schultz@phytec.de>
#
#This file is a global helper class for the WIC tool to create Phytec BSPs.

def parse_dtbs(d):
    kdt=d.getVar('KERNEL_DEVICETREE', True)
    dtbs=""
    dtbcount=1
    for DTB in kdt.split():
        if dtbcount == 1:
            dtbs += "zImage-"+DTB+";oftree"
        dtbs += " zImage-"+DTB
        dtbcount += 1
    return dtbs
IMAGE_DEPENDS_wic_append = " \
    dosfstools-native \
    mtools-native \
    virtual/kernel:do_deploy \
    virtual/bootloader:do_deploy \
"

python do_rename_wic () {
    deploy_dir = d.getVar('IMGDEPLOYDIR', True)
    link_name = d.getVar('IMAGE_LINK_NAME', True)
    old = os.path.join(deploy_dir, link_name + ".wic")
    new = os.path.join(deploy_dir, link_name + ".sdcard")
    if os.path.exists(old):
        os.rename(old, new)
        bb.note("renamed %s to %s" % (old, new))
}
addtask do_rename_wic after do_image_wic before do_image_complete
