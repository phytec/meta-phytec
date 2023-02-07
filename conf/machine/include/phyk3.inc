require conf/machine/include/k3.inc

SERIAL_CONSOLES = "115200;ttyS2"
SERIAL_CONSOLES_CHECK = "${SERIAL_CONSOLES}"

# Install modules in initramfs and roofs
PACKAGE_INSTALL_append = " kernel-modules"

INITRAMFS_FSTYPES = "wic.xz"

do_image_wic[mcdepends] = "mc::k3r5:ti-sci-fw:do_deploy"
do_image_tar[mcdepends] = "mc::k3r5:ti-sci-fw:do_deploy"

KERNEL_IMAGETYPE = "Image"
KERNEL_IMAGETYPES = "Image vmlinux.gz"
