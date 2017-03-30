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
    image_name = d.getVar('IMAGE_NAME', True)
    old_link = os.path.join(deploy_dir, link_name + ".wic")
    new_link = os.path.join(deploy_dir, link_name + ".sdcard")
    old_file = os.path.join(deploy_dir, image_name + ".rootfs.wic")

    if os.path.exists(old_file):
        new_file = old_file.replace("wic", "sdcard")
        if os.path.exists(old_link):
            os.remove(old_link)
        os.rename(old_file, new_file)
        if os.path.exists(new_file):
            new_file_name = image_name + ".rootfs.sdcard"
            if os.path.exists(new_link):
                os.remove(new_link)
            os.symlink(new_file_name, new_link)
        else:
            bb.error("unable to create symlink from %s to %s" % (new_file, new_link))
}
addtask do_rename_wic after do_image_wic before do_image_complete
