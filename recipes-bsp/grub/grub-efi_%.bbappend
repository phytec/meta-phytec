# Don't pull in the grub-bootconf generator. We are using WIC's bootimg-efi
# plugin that already generates a grub.cfg for us.
RDEPENDS:${PN}:remove = "virtual-grub-bootconf"

# Also, don't install EFI into rootfs
do_install:append() {
    rm -rf ${D}/${EFI_PREFIX}
}
