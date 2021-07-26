part --source bootimg-partition --fstype=vfat --label boot0 --ondisk mmc --active --align 4096 --size ${WKS_BOOTIMAGESIZE}
part / --source rootfs --fstype=ext4 --label root0 --ondisk mmc --use-uuid
part --source bootimg-partition --fstype=vfat --label boot1 --ondisk mmc --active --size ${WKS_BOOTIMAGESIZE}
part / --source rootfs --fstype=ext4 --label root1 --ondisk mmc --use-uuid
