# Copyright (C) 2014 - 2016 PHYTEC Messtechnik GmbH
# Jan weitzel <j.weitzel@phytec.de>
# Daniel Schultz <d.schultz@phytec.de>
# Wadim Egorov <w.egorov@phytec.de>
# based on meta-raspberrypi/classes/sdcard_image-rpi.bbclass
#
#
# Create an image that can by written onto a SD card using dd.
#
# The disk layout (sdcard) used is:
#
#    0                      -> IMAGE_ROOTFS_ALIGNMENT         - reserved for other data
#    IMAGE_ROOTFS_ALIGNMENT -> BOOT_SPACE                     - bootloader and kernel
#    BOOT_SPACE             -> SDIMG_SIZE                     - rootfs
#

#                                                     Default Free space = 1.3x
#                                                     Use IMAGE_OVERHEAD_FACTOR to add more space
#                                                     <--------->
#            4MiB              20MiB           SDIMG_ROOTFS
# <-----------------------> <----------> <---------------------->
#  ------------------------ ------------ ------------------------
# | IMAGE_ROOTFS_ALIGNMENT | BOOT_SPACE | ROOTFS_SIZE            |
#  ------------------------ ------------ ------------------------
# ^                        ^            ^                        ^
# |                        |            |                        |
# 0                      4MiB     4MiB + 20MiB       4MiB + 20Mib + SDIMG_ROOTFS

inherit image_types

# This image depends on the rootfs image
IMAGE_TYPEDEP_sdcard = "${SDIMG_ROOTFS_TYPE}"

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "boot"

# Root partition volume id
ROOT_VOLUME_ID ?= "root"

# Boot partition size [in KiB] (will be rounded up to IMAGE_ROOTFS_ALIGNMENT)
BOOT_SPACE ?= "20480"

# Set alignment to 4MB [in KiB]
IMAGE_ROOTFS_ALIGNMENT = "4096"

# Use an uncompressed ext4 by default as rootfs
SDIMG_ROOTFS_TYPE = "ext4"
SDIMG_ROOTFS = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.${SDIMG_ROOTFS_TYPE}"

IMAGE_DEPENDS_sdcard = " \
    parted-native \
    mtools-native \
    dosfstools-native \
    e2fsprogs-native \
    virtual/kernel:do_deploy \
    virtual/bootloader:do_deploy \
    virtual/prebootloader:do_deploy \
"
BOOTIMG_sdcard = "boot_sdcard.img"

# SD image names
SDIMG_sdcard = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.sdcard"

# Compression method to apply to SDIMG after it has been created. Supported
# compression formats are "gzip", "bzip2" or "xz". The original .sdcard file
# is kept and a new compressed file is created if one of these compression
# formats is chosen. If SDIMG_COMPRESSION is set to any other value it is
# silently ignored.
#SDIMG_COMPRESSION ?= ""

IMAGEDATESTAMP = "${@time.strftime('%Y.%m.%d',time.gmtime())}"

IMAGE_CMD_sdcard () {
	create_image ${SDIMG_sdcard}
	populate_boot_part ${SDIMG_sdcard} ${BOOTIMG_sdcard}
	finish_image ${SDIMG_sdcard} ${BOOTIMG_sdcard}
	populate_root_part ${SDIMG_sdcard}


	case "${PREFERRED_PROVIDER_virtual/bootloader}" in
	"barebox")
		BOOTLOADER=${DEPLOY_DIR_IMAGE}/barebox.bin.u-boot;;
	"u-boot")
		BOOTLOADER=${DEPLOY_DIR_IMAGE}/u-boot-dtb.img;;
	*)
		;;
	esac

	case "${PREFERRED_PROVIDER_virtual/prebootloader}" in
	"u-boot-spl")
		SPL=${DEPLOY_DIR_IMAGE}/u-boot-spl-dtb.bin.rksd;;
	*)
		;;
	esac
	flash_bootloader ${SDIMG_sdcard} ${SPL} ${BOOTLOADER}

	do_compression ${SDIMG_sdcard}
}

flash_bootloader () {

	SDIMG=$1
	SPL=$2
	BOOTLOADER=$3

	dd if=$SPL of=${SDIMG} conv=notrunc seek=64 bs=512
	dd if=$BOOTLOADER of=${SDIMG} conv=notrunc seek=256 bs=512
}

create_image () {

	SDIMG=$1

	# Align partitions
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE} + ${IMAGE_ROOTFS_ALIGNMENT} - 1)
	BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE_ALIGNED} - ${BOOT_SPACE_ALIGNED} % ${IMAGE_ROOTFS_ALIGNMENT})
	ROOTFS_SIZE=`du -bks ${SDIMG_ROOTFS} | awk '{print $1}'`

        # Round up RootFS size to the alignment size as well
	ROOTFS_SIZE_ALIGNED=$(expr ${ROOTFS_SIZE} + ${IMAGE_ROOTFS_ALIGNMENT} - 1)
	ROOTFS_SIZE_ALIGNED=$(expr ${ROOTFS_SIZE_ALIGNED} - ${ROOTFS_SIZE_ALIGNED} % ${IMAGE_ROOTFS_ALIGNMENT})
	SDIMG_SIZE=$(expr ${IMAGE_ROOTFS_ALIGNMENT} + ${BOOT_SPACE_ALIGNED} + ${ROOTFS_SIZE_ALIGNED})

	echo "Creating filesystem with Boot partition ${BOOT_SPACE_ALIGNED} KiB and RootFS ${ROOTFS_SIZE_ALIGNED} KiB"

	# Initialize sdcard image file
	dd if=/dev/zero of=${SDIMG} bs=1024 count=0 seek=${SDIMG_SIZE}

	# Create partition table
	parted -s ${SDIMG} mklabel msdos
	# Create boot partition and mark it as bootable
	parted -s ${SDIMG} unit KiB mkpart primary fat32 ${IMAGE_ROOTFS_ALIGNMENT} $(expr ${BOOT_SPACE_ALIGNED} \+ ${IMAGE_ROOTFS_ALIGNMENT})
	parted -s ${SDIMG} set 1 boot on
	# Create rootfs partition to the end of disk
	parted -s ${SDIMG} -- unit KiB mkpart primary ext2 $(expr ${BOOT_SPACE_ALIGNED} \+ ${IMAGE_ROOTFS_ALIGNMENT}) -1s
	parted ${SDIMG} print
}

finish_image () {

	SDIMG=$1
	BOOTIMG=$2

	# Burn Partitions
	dd if=${WORKDIR}/${BOOTIMG} of=${SDIMG} conv=notrunc,fsync seek=1 bs=$(expr ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)

}

# Copy all dtb files in KERNEL_DEVICETREE onto the sdcard image and use the
# first device tree in KERNEL_DEVICETREE as the 'oftree' file which will be
# used as the default device tree by the bootloader.
copy_kernel_device_trees () {
	BOOT_IMAGE=$1

	if test -n "${KERNEL_DEVICETREE}"; then
		DEVICETREE_DEFAULT=""
		for DTS_FILE in ${KERNEL_DEVICETREE}; do
			[ -n "${DEVICETREE_DEFAULT}"] && DEVICETREE_DEFAULT="${DTS_FILE}"
			mcopy -i ${BOOT_IMAGE} -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_FILE} ::${DTS_FILE}
		done

		mcopy -i ${BOOT_IMAGE} -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DEVICETREE_DEFAULT} ::oftree

		# Create README
		README=${WORKDIR}/README.sdcard.txt
		cat > ${README} <<EOF
This directory maybe contains multiple device tree files (suffix dtb).  So a
single sd-card image can be used on multiple board configurations.

By default the device tree in the file 'oftree' is loaded. The file is a plain
copy of the device tree '${DEVICETREE_DEFAULT}'.  If you want to use another
device tree, either rename the file to 'oftree' or change the variable
'global.bootm.oftree' in the barebox environment file '/env/boot/mmc' (Don't
forget to execute 'saveenv').

If you want to change the default device tree for the sd-card in the yocto
image creation process, place the default device tree at the beginning of the
variable KERNEL_DEVICETREE in the machine configuration.
EOF
		mcopy -i ${BOOT_IMAGE} -s ${README} ::/README.txt
	fi
}

populate_boot_part () {

	SDIMG=$1
	BOOTIMG=$2

	# Delete the boot image to avoid trouble with the build cache
	rm -f ${WORKDIR}/${BOOTIMG}

	# Create a vfat image with boot files
	BOOT_BLOCKS=$(LC_ALL=C parted -s ${SDIMG} unit b print | awk '/ 1 / { print substr($4, 1, length($4 -1)) / 512 /2 }')
	mkfs.vfat -n "${BOOTDD_VOLUME_ID}" -S 512 -C ${WORKDIR}/${BOOTIMG} $BOOT_BLOCKS
	mcopy -i ${WORKDIR}/${BOOTIMG} -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin ::linuximage

	copy_kernel_device_trees "${WORKDIR}/${BOOTIMG}"

	# Add stamp file
	echo "${IMAGE_NAME}-${IMAGEDATESTAMP}" > ${WORKDIR}/image-version-info
	mcopy -i ${WORKDIR}/${BOOTIMG} -v ${WORKDIR}//image-version-info ::
}

populate_root_part () {

	SDIMG=$1

	# If SDIMG_ROOTFS_TYPE is ext2/3/4 set root volume id
	if echo "${SDIMG_ROOTFS_TYPE}" | egrep "ext[2|3|4]"
	then
		tune2fs -L ${ROOT_VOLUME_ID} ${SDIMG_ROOTFS}
	fi

	# If SDIMG_ROOTFS_TYPE is a .xz file use xzcat
	if echo "${SDIMG_ROOTFS_TYPE}" | egrep -q "*\.xz"
	then
		xzcat ${SDIMG_ROOTFS} | dd of=${SDIMG} conv=notrunc,fsync seek=1 bs=$(expr 1024 \* ${BOOT_SPACE_ALIGNED} + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
	else
		dd if=${SDIMG_ROOTFS} of=${SDIMG} conv=notrunc,fsync seek=1 bs=$(expr 1024 \* ${BOOT_SPACE_ALIGNED} + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
	fi

}

do_compression () {

	SDIMG=$1

	# Optionally apply compression
	case "${SDIMG_COMPRESSION}" in
	"gzip")
		gzip -k9 "${SDIMG}"
		;;
	"bzip2")
		bzip2 -k9 "${SDIMG}"
		;;
	"xz")
		xz -k "${SDIMG}"
		;;
	esac

}
