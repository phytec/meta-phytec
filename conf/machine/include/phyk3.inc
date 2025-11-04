# TI K3 Aarch64 profile for Cortex-A53/A72 cores

SOC_FAMILY = "ti-soc"
require conf/machine/include/soc-family.inc
SOC_FAMILY:append = ":k3"

require conf/machine/include/arm/arch-arm64.inc

require phytec-machine-common.inc

KERNEL_CLASSES += "kernel-fit-extra-artifacts"

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
UBOOT_BINARY = "u-boot.${UBOOT_SUFFIX}"

EXTRA_IMAGEDEPENDS += "virtual/bootloader"

TFA_PLATFORM = "k3"

do_image_wic[depends] += "ti-boot-container:do_deploy"
do_image_partup[depends] += "ti-boot-container:do_deploy"
do_image_partup[depends] += "phytec-bootenv:do_deploy"

SERIAL_CONSOLES = "115200;ttyS2"

# Install modules in initramfs and roofs
MACHINE_EXTRA_RRECOMMENDS += "kernel-modules"
INITRAMFS_FSTYPES = "cpio.xz"

PREFERRED_PROVIDER_virtual/kernel ??= "linux-phytec-ti"
PREFERRED_PROVIDER_virtual/bootloader ??= "u-boot-phytec-ti"
PREFERRED_PROVIDER_u-boot ??= "u-boot-phytec-ti"
PREFERRED_PROVIDER_ti-sci-fw ??= "ti-sci-fw"
PREFERRED_PROVIDER_ti-dm-fw ??= "ti-dm-fw"

MACHINE_FEATURES = "apm usbgadget usbhost vfat ext2 alsa rtc optee efi"

# dont install kernelimage to rootfs
RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

KERNEL_IMAGETYPE ?= "Image"
KERNEL_IMAGETYPES ?= "Image"

KERNEL_MODULE_AUTOLOAD += "cryptodev"

WKS_FILE = "${@bb.utils.contains("MACHINE_FEATURES", "efi", "k3-sdimage-efi.wks.in", "k3-sdimage.wks", d)}"
EMMC_DEV = "0"
# eMMC boot partition bootloader image offset
BOOTLOADER_SEEK_EMMC = "0"
# combined bootloader image for RAUC bootloader update
IMAGE_BOOTLOADER = "ti-boot-container"

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

K3_BOOTLOADER_FILES = "${SPL_BINARYNAME} u-boot.${UBOOT_SUFFIX} tiboot3.bin ti-boot-container.img"

IMAGE_BOOT_FILES = " \
    ${K3_BOOTLOADER_FILES} \
    ${KERNEL_IMAGETYPE} \
    ${@make_dtb_boot_files(d)} oftree \
    overlays.txt \
"
IMAGE_BOOT_FILES:secureboot = " \
    ${K3_BOOTLOADER_FILES} \
    fitImage \
"
IMAGE_EFI_BOOT_FILES ?= "${IMAGE_BOOT_FILES}"

EFI_PROVIDER ?= "grub-efi"

IMAGE_CLASSES += "image-types-partup wic-bootenv-helper"
include partup-layout-config.inc

IMAGE_FSTYPES = "tar.xz wic.xz wic.bmap partup"
IMAGE_FSTYPES:update = "partup"

# OSPI Flash Layout for initramfs
# 0x700000: FDT (max 1 MiB)
# 0x800000: Image (max 26 MiB) - linux-mainline uses defconfig
KERNEL_IMAGE_MAXSIZE ??= "${@oe.utils.conditional("PREFERRED_PROVIDER_virtual/kernel", "linux-mainline", "", "27262", d)}"
# 0x2200000: Initramfs (max 30 MiB)

# initramfs max size is 256 MiB
INITRAMFS_MAXSIZE ?= "262144"
