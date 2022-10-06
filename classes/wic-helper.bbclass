BAREBOX_BINARY ??= "barebox.bin"
WKS_BOOTIMAGESIZE ??= "20"

WICVARS:append = " BAREBOX_BINARY BOOTLOADER_SEEK WKS_BOOTIMAGESIZE"

do_image_wic[depends] += "\
    dosfstools-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    virtual/kernel:do_deploy \
    virtual/bootloader:do_deploy \
"
