# TI K3 Aarch64 profile for Cortex-A53/A72 cores

SOC_FAMILY = "ti-soc"
require conf/machine/include/soc-family.inc
SOC_FAMILY:append = ":k3"

require conf/machine/include/arm/arch-arm64.inc

BBMULTICONFIG += "k3r5"

require phytec-machine-common.inc

KERNEL_CLASSES += "kernel-fitimage"

UBOOT_ARCH = "arm"
UBOOT_ENTRYPOINT = "0x81000000"
UBOOT_LOADADDRESS = "0x81000000"
UBOOT_RD_LOADADDRESS = "0x84000000"
UBOOT_RD_ENTRYPOINT = "0x84000000"
UBOOT_DTB_LOADADDRESS = "0x83000000"
UBOOT_DTBO_LOADADDRESS = "0x83080000"
UBOOT_DTBO_OFFSET = "0x00010000"

SPL_BINARY = "tispl.bin"
SPL_BINARYNAME = "tispl.bin"
UBOOT_SUFFIX = "img"

UBOOT_MKIMAGE_DTCOPTS = "-I dts -O dtb"
UBOOT_SIGN_KEYNAME ?= "custMpk"
UBOOT_SIGN_KEYDIR ?= "${TI_SECURE_DEV_PKG}/keys"
FIT_HASH_ALG ?= "sha512"
FIT_SIGN_ALG ?= "rsa4096"

EXTRA_IMAGEDEPENDS += "virtual/bootloader"

TFA_PLATFORM = "k3"

do_image_wic[depends] += "virtual/bootloader:do_deploy"
do_image[mcdepends] += "mc::k3r5:virtual/bootloader:do_deploy"

SERIAL_CONSOLES = "115200;ttyS2"

# Install modules in initramfs and roofs
PACKAGE_INSTALL:append = " kernel-modules"
INITRAMFS_FSTYPES = "cpio.xz"

UBOOT_SIGN_ENABLE = "0"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-phytec-ti"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-phytec-ti"
PREFERRED_PROVIDER_u-boot ?= "u-boot-phytec-ti"

MACHINE_FEATURES = "apm usbgadget usbhost vfat ext2 alsa pci optee efi"

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

K3_BOOTLOADER_FILES = "${SPL_BINARYNAME} u-boot.${UBOOT_SUFFIX} tiboot3.bin"

IMAGE_BOOT_FILES = " \
    ${K3_BOOTLOADER_FILES} \
    ${KERNEL_IMAGETYPE} \
    ${@make_dtb_boot_files(d)} oftree \
"
IMAGE_BOOT_FILES:secureboot = " \
    ${K3_BOOTLOADER_FILES} \
    fitImage \
"
PARTUP_BOOT_FILES = " \
    ${KERNEL_IMAGETYPE} \
    ${@make_dtb_boot_files(d)} oftree \
"
PARTUP_BOOT_FILES:secureboot = "fitImage"
IMAGE_EFI_BOOT_FILES ?= "${IMAGE_BOOT_FILES}"

EFI_PROVIDER ?= "grub-efi"

IMAGE_CLASSES += "image-types-partup"
include partup-layout-config.inc

IMAGE_FSTYPES = "tar.gz wic.xz wic.bmap partup"
IMAGE_FSTYPES:update = "partup"

# rootfs alignment required for RAUC adaptive update
IMAGE_ROOTFS_ALIGNMENT:update = "4"
