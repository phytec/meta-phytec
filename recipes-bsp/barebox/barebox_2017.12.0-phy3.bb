# Copyright (C) 2018 PHYTEC Messtechnik GmbH,
# Author: Stefan Riedmueller <s.riedmueller@phytec.de>

inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2


GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "1899a64a38a19e31537b1c1d155c0935773df0fd"
SRC_URI += "\
    ${@oe.utils.conditional('DEBUG_BUILD','1','file://debugging.cfg','',d)} \
"

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

python do_env_append_mx6() {
    env_add(d, "boot/mmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/mnt/mmc/zImage"
global.bootm.oftree="/mnt/mmc/oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk2p2 rootflags='data=journal'"
""")
    env_add(d, "boot/nand",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root

global.bootm.image="/dev/nand0.root.ubi.kernel"
global.bootm.oftree="/dev/nand0.root.ubi.oftree"
global.linux.bootargs.dyn.root="root=ubi0:root ubi.mtd=root rootfstype=ubifs"
""")
    env_add(d, "boot/net",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

path="/mnt/tftp"

global.bootm.image="${path}/${global.user}-linux-${global.hostname}"

oftree="${path}/${global.user}-oftree-${global.hostname}"
if [ -f "${oftree}" ]; then
    global.bootm.oftree="$oftree"
fi

nfsroot="/nfsroot/${global.hostname}"
bootargs-ip
global.linux.bootargs.dyn.root="root=/dev/nfs nfsroot=$nfsroot,vers=3,udp"
""")
    env_add(d, "boot/spi",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/dev/m25p0.kernel"
global.bootm.oftree="/dev/m25p0.oftree"
global.linux.bootargs.dyn.root="root=ubi0:root ubi.mtd=root rootfstype=ubifs"
""")
    env_add(d, "expansions/imx6qdl-mira-enable-lvds",
"""of_fixup_status /soc/aips-bus@02000000/ldb/lvds-channel@0
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-nunki-enable-lvds",
"""of_fixup_status /soc/aips-bus@02000000/ldb/lvds-channel@0
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-mira-peb-eval-01",
"""of_fixup_status /gpio-keys
of_fixup_status /user-leds
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@02000000/ldb/lvds-channel@0
of_fixup_status /soc/aips-bus@02100000/i2c@021a4000/polytouch@38
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-018-peb-av-02",
"""of_fixup_status /display@di0
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/polytouch@38
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-018-peb-av-02-res",
"""of_fixup_status /display@di0
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-phytec-peb-wlbt-01",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@02100000/usdhc@02198000
of_fixup_status /regulator-wlan-en
""")
    env_add(d, "network/eth0",
"""#!/bin/sh

# ip setting (static/dhcp)
ip=static
global.dhcp.vendor_id=barebox-${global.hostname}

# static setup used if ip=static
ipaddr=192.168.3.11
netmask=255.255.255.0
gateway=192.168.3.10
serverip=192.168.3.10
""")
}

python do_env_append_phyflex-imx6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#use this expansion when a capacitive touchscreen is connected
. /env/expansions/imx6qdl-phytec-lcd

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
of_display_timings -P "/panel-lcd" -c "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_display_timings -P "/panel-lcd" -c "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_display_timings -P "/panel-lcd" -c "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_display_timings -P "/panel-lcd" -c "edt,etm0350g0dh6"


#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-P VM-011-COL

#Enable VM-XXX on CSI1
#of_camera_selection -a 0x48 -p 1 -b phyCAM-P VM-011-COL
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-res",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@02000000/ldb/lvds-channel@0
of_fixup_status /soc/aips-bus@02100000/i2c@021a4000/touchctrl@41
""")
    # Enable 32 bit color depth for framebuffer emulation on phyFLEX-CarrierBoard
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env_append_phyboard-mira-imx6() {
    env_add(d, "boot/emmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/mnt/emmc/zImage"
global.bootm.oftree="/mnt/emmc/oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk3p2 rootflags='data=journal'"
""")
    env_add(d, "boot/mmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/mnt/mmc/zImage"
global.bootm.oftree="/mnt/mmc/oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk0p2 rootflags='data=journal'"
""")
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
#. /env/expansions/imx6qdl-phytec-peb-wlbt-01

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_display_timings -P "/panel-lcd" -c "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_display_timings -P "/panel-lcd" -c "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_display_timings -P "/panel-lcd" -c "edt,etm0350g0dh6"

#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
    # Enable 32 bit color depth for framebuffer emulation on phyBOARD-Mira
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env_append_phyboard-nunki-imx6() {
    env_add(d, "boot/emmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/mnt/emmc/zImage"
global.bootm.oftree="/mnt/emmc/oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk3p2 rootflags='data=journal'"
""")
    env_add(d, "boot/mmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/mnt/mmc/zImage"
global.bootm.oftree="/mnt/mmc/oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk0p2 rootflags='data=journal'"
""")
    env_add(d, "config-expansions",
"""#!/bin/sh

#. /env/expansions/imx6qdl-nunki-enable-lvds

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_display_timings -P "/panel-lcd" -c "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_display_timings -P "/panel-lcd" -c "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_display_timings -P "/panel-lcd" -c "edt,etm0350g0dh6"

#Enable VM-XXX on CSI0
of_camera_selection -a 0x48 -p 0 -b phyCAM-P VM-010-BW

#Enable VM-XXX on CSI1
#of_camera_selection -a 0x48 -p 1 -b phyCAM-P VM-011-COL
""")
    # Enable 32 bit color depth for framebuffer emulation on phyBOARD-Nunki
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env_append_phyflex-imx6-3() {
    env_add(d, "nv/linux.bootargs.cma", "cma=256M@1G\n")
}

python do_env_append_phyflex-imx6-4() {
    env_add(d, "nv/linux.bootargs.cma", "cma=256M@1G\n")
}

python do_env_append_phyboard-mira-imx6-4() {
    env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
}

python do_env_append_phyboard-mira-imx6-5() {
    env_add(d, "boot/spi",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/dev/m25p0.kernel"
global.bootm.oftree="/dev/m25p0.oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk3p2 rootflags='data=journal'"
""")
    env_add(d, "nv/linux.bootargs.cma", "cma=256M@1G\n")
}

python do_env_append_phyboard-mira-imx6-6() {
    env_add(d, "boot/spi",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/dev/m25p0.kernel"
global.bootm.oftree="/dev/m25p0.oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk3p2 rootflags='data=journal'"
""")
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
. /env/expansions/imx6qdl-phytec-peb-wlbt-01

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_display_timings -P "/panel-lcd" -c "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_display_timings -P "/panel-lcd" -c "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_display_timings -P "/panel-lcd" -c "edt,etm0350g0dh6"

#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
}

python do_env_append_phyboard-mira-imx6-7() {
    env_add(d, "boot/spi",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/dev/m25p0.kernel"
global.bootm.oftree="/dev/m25p0.oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk3p2 rootflags='data=journal'"
""")
}

python do_env_append_phyboard-mira-imx6-8() {
    env_add(d, "boot/spi",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/dev/m25p0.kernel"
global.bootm.oftree="/dev/m25p0.oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk3p2 rootflags='data=journal'"
""")
}

python do_env_append_phyboard-mira-imx6-10() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
. /env/expansions/imx6qdl-phytec-peb-wlbt-01

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_display_timings -P "/panel-lcd" -c "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_display_timings -P "/panel-lcd" -c "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_display_timings -P "/panel-lcd" -c "edt,etm0350g0dh6"

#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
}

python do_env_append_phyboard-mira-imx6-11() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
#. /env/expansions/imx6qdl-phytec-peb-wlbt-01

#use this expansion when a capacitive touchscreen is connected
. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
of_display_timings -P "/panel-lcd" -c "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_display_timings -P "/panel-lcd" -c "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_display_timings -P "/panel-lcd" -c "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_display_timings -P "/panel-lcd" -c "edt,etm0350g0dh6"

#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
}

python do_env_append_phyboard-mira-imx6-13() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#. /env/expansions/imx6qdl-mira-enable-lvds
#. /env/expansions/imx6qdl-phytec-peb-wlbt-01

#use this expansion when a capacitive touchscreen is connected
. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_display_timings -P "/panel-lcd" -c "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
of_display_timings -P "/panel-lcd" -c "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_display_timings -P "/panel-lcd" -c "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_display_timings -P "/panel-lcd" -c "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_display_timings -P "/panel-lcd" -c "edt,etm0350g0dh6"

#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
}

python do_env_append_phyboard-mira-imx6-14() {
    env_add(d, "boot/spi",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/dev/m25p0.kernel"
global.bootm.oftree="/dev/m25p0.oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk3p2 rootflags='data=journal'"
""")
}

python do_env_append_phyboard-mira-imx6-15() {
    env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
}

#Enviroment changes for RAUC
python do_env_append_phyboard-mira-imx6-3() {
    env_add(d, "boot/nand2",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root

global.bootm.image="/dev/nand0.root.ubi.kernel2"
global.bootm.oftree="/dev/nand0.root.ubi.oftree2"

global.linux.bootargs.dyn.root="root=ubi0:root2 ubi.mtd=root rootfstype=ubifs"
""")
    env_add(d, "nv/bootchooser.state_prefix", """state""")
    env_add(d, "nv/bootchooser.targets", """system0 system1""")
    env_add(d, "nv/bootchooser.system0.boot", """nand""")
    env_add(d, "nv/bootchooser.system1.boot", """nand2""")
    env_add(d, "nv/bootchooser.state_prefix", """state.bootstate""")
    env_add(d, "bin/create_nand_partitions",
"""#!/bin/sh
echo "Create NAND partitions"
ubiformat -q /dev/nand0.root
ubiattach /dev/nand0.root
#Hold the following order or change the /dev/ubi0_X enumeration in /etc/rauc/system.conf
ubimkvol -t static /dev/nand0.root.ubi kernel 8M
ubimkvol -t static /dev/nand0.root.ubi oftree 1M
#For 1GB NANDs (otherwise other partition sizes)
ubimkvol -t dynamic /dev/nand0.root.ubi root 450M
ubimkvol -t static /dev/nand0.root.ubi kernel2 8M
ubimkvol -t static /dev/nand0.root.ubi oftree2 1M
ubimkvol -t dynamic /dev/nand0.root.ubi root2 450M
""")
    env_add(d, "bin/init_flash_from_mmc",
"""#!/bin/sh
echo "Initialize NAND flash from MMC"
barebox_update -t MLO.nand /mnt/mmc0.0/MLO -y
barebox_update -t nand /mnt/mmc0.0/barebox.bin -y
[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root
ubiupdatevol /dev/nand0.root.ubi.kernel  /mnt/mmc0.0/zImage
ubiupdatevol /dev/nand0.root.ubi.kernel2 /mnt/mmc0.0/zImage
ubiupdatevol /dev/nand0.root.ubi.oftree  /mnt/mmc0.0/oftree
ubiupdatevol /dev/nand0.root.ubi.oftree2 /mnt/mmc0.0/oftree
cp /mnt/mmc0.0/*.ubifs /dev/nand0.root.ubi.root
cp /mnt/mmc0.0/*.ubifs /dev/nand0.root.ubi.root2
""")
    env_add(d, "bin/init_flash_from_tftp",
"""#!/bin/sh
echo "Initialize NAND flash from TFTP"
[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root
ubiupdatevol /dev/nand0.root.ubi.kernel  /mnt/tftp/zImage
ubiupdatevol /dev/nand0.root.ubi.kernel2 /mnt/tftp/zImage
ubiupdatevol /dev/nand0.root.ubi.oftree  /mnt/tftp/oftree
ubiupdatevol /dev/nand0.root.ubi.oftree2 /mnt/tftp/oftree
cp /mnt/tftp/*.ubifs /dev/nand0.root.ubi.root
cp /mnt/tftp/*.ubifs /dev/nand0.root.ubi.root2
""")
}

INTREE_DEFCONFIG = "imx_v7_defconfig"

COMPATIBLE_MACHINE  =  "phyflex-imx6-1"
COMPATIBLE_MACHINE .= "|phyflex-imx6-2"
COMPATIBLE_MACHINE .= "|phyflex-imx6-3"
COMPATIBLE_MACHINE .= "|phyflex-imx6-4"
COMPATIBLE_MACHINE .= "|phyflex-imx6-5"
COMPATIBLE_MACHINE .= "|phyflex-imx6-6"
COMPATIBLE_MACHINE .= "|phyflex-imx6-7"
COMPATIBLE_MACHINE .= "|phyflex-imx6-8"
COMPATIBLE_MACHINE .= "|phyflex-imx6-9"
COMPATIBLE_MACHINE .= "|phyflex-imx6-10"

COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-3"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-4"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-5"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-6"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-7"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-8"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-9"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-10"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-11"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-12"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-13"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-14"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-15"

COMPATIBLE_MACHINE .= "|phyboard-nunki-imx6-1"
