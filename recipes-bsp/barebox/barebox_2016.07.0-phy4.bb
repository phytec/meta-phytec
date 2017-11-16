inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

PR = "${INC_PR}.1"

# NOTE: Keep version in filename in sync with commit id and barebox-ipl!
SRCREV = "47f9b2423809580eaf8a4fbcc3eff31d981a4cbc"

DEPENDS += "u-boot-mkimage-native"

do_compile_append_rk3288 () {
	mkimage -A arm -T firmware -C none -O u-boot -a 0x02000000 -e 0 -n "barebox image" -d ${B}/${BAREBOX_BIN} ${B}/${BAREBOX_BIN}.u-boot
}

do_install_append_rk3288 () {
	install ${B}/${BAREBOX_BIN}.u-boot ${D}${base_bootdir}/${BAREBOX_IMAGE_BASE_NAME}.bin.u-boot
	ln -sf ${BAREBOX_IMAGE_BASE_NAME}.bin.u-boot ${D}${base_bootdir}/${BAREBOX_BIN_SYMLINK}.u-boot
}

do_deploy_append_rk3288 () {
	install -m 644 ${B}/${BAREBOX_BIN}.u-boot ${DEPLOYDIR}/${BAREBOX_IMAGE_BASE_NAME}.bin.u-boot
	ln -sf ${BAREBOX_IMAGE_BASE_NAME}.bin.u-boot ${DEPLOYDIR}/${BAREBOX_BIN_SYMLINK}.u-boot
}

python do_env_append() {
    env_add(d, "nv/allow_color", "false\n")
    env_add(d, "nv/linux.bootargs.base", "consoleblank=0\n")
    env_add(d, "nv/linux.bootargs.rootfs", "rootwait ro fsck.repair=yes\n")
    env_add(d, "bin/far",
"""#!/bin/sh
# barebox script far (="Fetch And Reset"):
#
# The script is useful for a rapid compile and execute development cycle. If
# the deployment directory of yocto is the root directory of the tftp server
# (e.g. use a bind mount), you can fetch and execute a newly compiled barebox
# with this script.

cp /mnt/tftp/barebox.bin /dev/ram0
if [ $? != 0 ]; then
    echo "Error: Cannot fetch file \"barebox.bin\" from host!"
else
    go /dev/ram0
fi
""")
}

python do_env_append_rk3288() {
    env_add(d, "boot/emmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image=/mnt/emmc/linuximage
global.bootm.oftree=/mnt/emmc/oftree

global.linux.bootargs.dyn.root="root=/dev/mmcblk0p2 rootflags='data=journal'"
""")
    env_add(d, "boot/mmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image=/mnt/sdmmc/linuximage
global.bootm.oftree=/mnt/sdmmc/oftree

global.linux.bootargs.dyn.root="root=/dev/mmcblk1p2 rootflags='data=journal'"
""")
    env_add(d, "expansions/phytec-lcd-018-pcm-947",
"""of_fixup_status /lvds-panel
of_fixup_status /backlight
of_fixup_status /lvds@ff96c000/
of_fixup_status /i2c@ff660000/touchscreen@38/
""")
}

python do_env_append_phycore-rk3288-3() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#. /env/expansions/phytec-lcd-018-pcm-947

""")
}

INTREE_DEFCONFIG = "rk3288_defconfig"

COMPATIBLE_MACHINE = "phycore-rk3288-3"
