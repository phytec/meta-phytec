require conf/machine/include/k3.inc

SERIAL_CONSOLES = "115200;ttyS2"
SERIAL_CONSOLES_CHECK = "${SERIAL_CONSOLES}"

# Install modules in initramfs and roofs
PACKAGE_INSTALL:append = " kernel-modules"
INITRAMFS_FSTYPES = "cpio.xz"

UBOOT_SIGN_ENABLE = "0"

PREFERRED_PROVIDER_virtual/kernel = "linux-ti"
PREFERRED_PROVIDER_virtual/bootloader = "u-boot-ti"
PREFERRED_PROVIDER_u-boot = "u-boot-ti"

MACHINE_FEATURES += "optee efi"

# dont install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""
MACHINE_ESSENTIAL_EXTRA_RDEPENDS:remove = "kernel-image-image kernel-devicetree"

KERNEL_IMAGETYPE = "Image"
KERNEL_IMAGETYPES = "Image vmlinux.gz"

WKS_FILE = "${@bb.utils.contains("MACHINE_FEATURES", "efi", "k3-sdimage-efi.wks.in", "k3-sdimage.wks", d)}"
EMMC_DEV = "0"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC = "0"
# combined bootloader image for RAUC bootloader update
IMAGE_BOOTLOADER = "ti-boot-rauc"

def make_dtb_boot_files(d):
    # Generate IMAGE_BOOT_FILES entries for device tree files listed in
    # KERNEL_DEVICETREE.
    # Use only the basename for dtb files:
    alldtbs = d.getVar('KERNEL_DEVICETREE')


    # DTBs may be built out of kernel with devicetree.bbclass
    if not alldtbs:
        return ''

    def transform(dtb):
        if not (dtb.endswith('dtb') or dtb.endswith('dtbo')):
            # eg: whatever/bcm2708-rpi-b.dtb has:
            #     DEPLOYDIR file: bcm2708-rpi-b.dtb
            #     destination: bcm2708-rpi-b.dtb
            bb.error("KERNEL_DEVICETREE entry %s is not a .dtb or .dtbo file." % (dtb) )
        return os.path.basename(dtb)

    return ' '.join([transform(dtb) for dtb in alldtbs.split() if dtb])

SPL_BINARY = "tispl.bin"
SPL_BINARYNAME = "tispl.bin"
UBOOT_SUFFIX = "img"
K3_BOOTLOADER_FILES = "${SPL_BINARYNAME} u-boot.${UBOOT_SUFFIX} tiboot3.bin"

IMAGE_BOOT_FILES = " \
    ${K3_BOOTLOADER_FILES} \
    ${KERNEL_IMAGETYPE} \
    ${@make_dtb_boot_files(d)} oftree \
"
IMAGE_EFI_BOOT_FILES ?= "${IMAGE_BOOT_FILES}"

IMAGE_BOOT_FILES:remove:update = "${K3_BOOTLOADER_FILES}"

IMAGE_CLASSES += "image-types-partup"
include partup-layout-config.inc
IMAGE_FSTYPES += "partup"

IMAGE_FSTYPES:remove:update = "wic.xz wic.bmap"
# rootfs alignment required for RAUC adaptive update
IMAGE_ROOTFS_ALIGNMENT:update = "4"
