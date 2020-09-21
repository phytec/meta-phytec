part --source bootimg-partition --fstype=vfat --label boot0 --ondisk mmc --active --align 4096 --size 20
part / --source rootfs --fstype=ext4 --label root0 --ondisk mmc --use-uuid
part --source bootimg-partition --fstype=vfat --label boot1 --ondisk mmc --active --size 20
part / --source rootfs --fstype=ext4 --label root1 --ondisk mmc --use-uuid
