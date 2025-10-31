inherit native

DEPENDS = " \
    glib-2.0-native \
    libyaml-native \
    parted-native \
    util-linux-native \
"
RDEPENDS:${PN} += " \
    dosfstools-native \
    e2fsprogs-mke2fs-native \
    e2fsprogs-resize2fs-native \
    e2fsprogs-tune2fs-native \
    mtd-utils-native \
    squashfs-tools-native \
    tar-native \
    util-linux-sfdisk-native \
"
