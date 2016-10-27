inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

PR = "${INC_PR}.1"

# NOTE: Keep version in filename in sync with commit id and barebox-ipl!
SRCREV = "9f6e06139826c875ff3a3a892558fdd8b018b1a0"

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

python do_env_append_ti33x() {
    env_add(d, "boot/mmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image=/boot/linuximage
global.bootm.oftree=/boot/oftree
global.linux.bootargs.dyn.root="root=/dev/mmcblk0p2 rootflags='data=journal'"
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

path="/mnt/tftp"

global.bootm.image="${path}/${global.user}-linux-${global.hostname}"

oftree="${path}/${global.user}-oftree-${global.hostname}"
if [ -f "${oftree}" ]; then
    global.bootm.oftree="$oftree"
fi

nfsroot="/nfsroot/${global.hostname}"
bootargs-ip

[ -e /env/config-expansions ] && /env/config-expansions

global.linux.bootargs.dyn.root="root=/dev/nfs nfsroot=$nfsroot,vers=3,udp"
""")
    env_add(d, "boot/spi",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/dev/m25p0.kernel"
global.bootm.oftree="/dev/m25p0.oftree"
# Use rootfs from NAND
global.linux.bootargs.dyn.root="root=ubi0:root ubi.mtd=root rootfstype=ubifs"
""")
    env_add(d, "expansions/am335x-maia-peb-c-004", "of_fixup_status /ocp/mmc@47810000\n")
    env_add(d, "expansions/am335x-phytec-lcd-018-pcm-953",
"""of_fixup_status /panel
of_fixup_status /backlight
of_fixup_status /ocp/lcdc@4830e000/
of_fixup_status /ocp/epwmss@48300000/
of_fixup_status /ocp/epwmss@48300000/ecap@48300100/
of_fixup_status /ocp/i2c@44e0b000/touchscreen@38/
""")
    env_add(d, "expansions/am335x-phytec-lcd-018-pcm-953-res",
"""of_fixup_status /panel
of_fixup_status /backlight
of_fixup_status /ocp/lcdc@4830e000/
of_fixup_status /ocp/epwmss@48300000/
of_fixup_status /ocp/epwmss@48300000/ecap@48300100/
of_fixup_status /ocp/tscadc@44e0d000/
""")
    env_add(d, "expansions/am335x-phytec-lcd-018-peb-av-02",
"""of_fixup_status /panel
of_fixup_status /backlight
of_fixup_status /ocp/lcdc@4830e000/
of_fixup_status /ocp/epwmss@48304000/
of_fixup_status /ocp/epwmss@48304000/ecap@48304100/
of_fixup_status /ocp/i2c@44e0b000/touchscreen@38/
""")
    env_add(d, "expansions/am335x-phytec-lcd-018-peb-av-02-res",
"""of_fixup_status /panel
of_fixup_status /backlight
of_fixup_status /ocp/lcdc@4830e000/
of_fixup_status /ocp/epwmss@48304000/
of_fixup_status /ocp/epwmss@48304000/ecap@48304100/
of_fixup_status /ocp/tscadc@44e0d000/
""")
    env_add(d, "expansions/am335x-wega-peb-av-01",
"""of_fixup_status /ocp/lcdc@4830e000
of_fixup_status /ocp/i2c@44e0b000/tda19988@70
""")
    env_add(d, "expansions/am335x-wega-peb-eval-01",
"""of_fixup_status /user_leds
of_fixup_status /user_buttons
""")
    env_add(d, "expansions/am335x-wlan",
"""of_fixup_status /fixedregulator@2
of_fixup_status /ocp/mmc@47810000
of_fixup_status /ocp/mmc@47810000/wlcore@2
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
    env_add(d, "nv/boot.watchdog_timeout", "60\n")
}

python do_env_append_rk3288() {
    env_add(d, "boot/emmc",
"""#!/bin/sh

global.bootm.image=/mnt/emmc/linuximage
global.bootm.oftree=/mnt/emmc/oftree

global.linux.bootargs.dyn.root="root=/dev/mmcblk0p2 rootflags='data=journal'"
""")
    env_add(d, "boot/mmc",
"""#!/bin/sh

global.bootm.image=/mnt/sdmmc/linuximage
global.bootm.oftree=/mnt/sdmmc/oftree

global.linux.bootargs.dyn.root="root=/dev/mmcblk1p2 rootflags='data=journal'"
""")
}

python do_env_append_phycore-am335x() {
    env_add(d, "config-expansions",
"""#!/bin/sh
#use this expansion when a capacitive touchscreen is connected
. /env/expansions/am335x-phytec-lcd-018-pcm-953

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/am335x-phytec-lcd-018-pcm-953-res

#7" display
#of_display_timings -S /panel/display-timings/ETM0700G0DH6

#5.7" display
#of_display_timings -S /panel/display-timings/ETMV570G2DHU

#4.3" display
#of_display_timings -S /panel/display-timings/ETM0430G0DH6
""")
}

python do_env_append_beagleboneblack-1() {
    env_add(d, "boot/emmc",
"""#!/bin/sh

mkdir -p /mnt/emmc
mount /dev/mmc1.0 /mnt/emmc
global.bootm.image=/mnt/emmc/linuximage
global.bootm.oftree=/mnt/emmc/oftree
global.linux.bootargs.dyn.root="root=/dev/mmcblk1p2"
""")
    env_add(d, "boot/mmc",
"""#!/bin/sh

mkdir -p /mnt/mmc
mount /dev/mmc0.0 /mnt/mmc/
global.bootm.image=/mnt/mmc/linuximage
global.bootm.oftree=/mnt/mmc/oftree
global.linux.bootargs.dyn.root="root=/dev/mmcblk0p2"
""")
    env_add(d, "boot/net",
"""#!/bin/sh

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
    env_add(d, "config-expansions",
"""#!/bin/sh

# Beaglebone has capes. Does not need this.
""")
    env_add(d, "init/bootsource",
"""#!/bin/sh

if [ -n "$nv.boot.default" ]; then
    exit
fi

if [ $bootsource = mmc -a $bootsource_instance = 1 ]; then
    global.boot.default="emmc mmc net"
elif [ $bootsource = mmc -a $bootsource_instance = 0 ]; then
    global.boot.default="mmc emmc net"
elif [ $bootsource = net ]; then
    global.boot.default="net emmc mmc"
fi
""")
}

python do_env_append_phyboard-wega-am335x-2() {
    env_add(d, "config-expansions",
"""#!/bin/sh
#use this expansion when a capacitive touchscreen is connected
. /env/expansions/am335x-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/am335x-phytec-lcd-018-peb-av-02-res

. /env/expansions/am335x-wega-peb-eval-01

#. /env/expansions/am335x-wlan

#7" display
#of_display_timings -S /panel/display-timings/ETM0700G0DH6

#5.7" display
#of_display_timings -S /panel/display-timings/ETMV570G2DHU

#4.3" display
#of_display_timings -S /panel/display-timings/ETM0430G0DH6

#7" display AC158
#of_display_timings -S /panel/display-timings/ETM0700G0EDH6_WEGA
""")
}

python do_env_append_phycore-am335x-7() {
    env_add(d, "config-expansions",
"""#!/bin/sh
#use this expansion when a capacitive touchscreen is connected
. /env/expansions/am335x-phytec-lcd-018-pcm-953
. /env/expansions/am335x-wlan
#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/am335x-phytec-lcd-018-pcm-953-res

#7" display
#of_display_timings -S /panel/display-timings/ETM0700G0DH6

#5.7" display
#of_display_timings -S /panel/display-timings/ETMV570G2DHU

#4.3" display
#of_display_timings -S /panel/display-timings/ETM0430G0DH6
""")
}

python do_env_append_phyboard-wega-am335x-1() {
    env_add(d, "config-expansions",
"""#!/bin/sh

global linux.bootargs.dyn.video

. /env/expansions/am335x-wega-peb-av-01
. /env/expansions/am335x-wega-peb-eval-01

#. /env/expansions/am335x-wlan

global.linux.bootargs.dyn.video="video=HDMI-A-1:1024x768-32@60"
""")
}

python do_env_append_phyboard-wega-am335x-3() {
    env_add(d, "config-expansions",
"""#!/bin/sh

global linux.bootargs.dyn.video

. /env/expansions/am335x-wega-peb-av-01
. /env/expansions/am335x-wega-peb-eval-01

#. /env/expansions/am335x-wlan

global.linux.bootargs.dyn.video="video=HDMI-A-1:1024x768-32@60"
""")
}

python do_env_append_phyboard-wega-r2-am335x-1() {
    env_add(d, "config-expansions",
"""#!/bin/sh

global linux.bootargs.dyn.video

. /env/expansions/am335x-wega-peb-av-01
. /env/expansions/am335x-wega-peb-eval-01

#. /env/expansions/am335x-wlan

global.linux.bootargs.dyn.video="video=HDMI-A-1:1024x768-32@60"
""")
}

COMPATIBLE_MACHINE = "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-3"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-4"
COMPATIBLE_MACHINE .= "|phyboard-wega-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-am335x-6"
COMPATIBLE_MACHINE .= "|phycore-am335x-7"
COMPATIBLE_MACHINE .= "|phyboard-regor-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-rk3288-1"
COMPATIBLE_MACHINE .= "|phycore-rk3288-2"
