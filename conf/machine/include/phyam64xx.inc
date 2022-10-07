# Install modules in initramfs and roofs
PACKAGE_INSTALL_append = " kernel-modules"

BBMULTICONFIG = "k3r5"

do_image_wic[mcdepends] = "mc::k3r5:ti-sci-fw:do_deploy"
do_image_tar[mcdepends] = "mc::k3r5:ti-sci-fw:do_deploy"
