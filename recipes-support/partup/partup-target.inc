DEPENDS = " \
    glib-2.0 \
    libyaml \
    parted \
    util-linux \
"
RDEPENDS:${PN} += " \
    dosfstools \
    e2fsprogs-mke2fs \
    e2fsprogs-resize2fs \
    squashfs-tools \
    tar \
    util-linux-sfdisk \
"
