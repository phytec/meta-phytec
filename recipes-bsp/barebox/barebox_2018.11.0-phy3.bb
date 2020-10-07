inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2

LIC_FILES_CHKSUM = "file://COPYING;md5=057bf9e50e1ca857d0eb97bfe4ba8e5d"

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id and barebox-ipl!
SRCREV = "164a092e574a6fec7a00332d2f16a407aa7d7d8e"

python do_env_append() {
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
    env_add(d, "nv/dev.eth0.mode", """static""")
    env_add(d, "nv/dev.eth0.ipaddr", """192.168.3.11""")
    env_add(d, "nv/dev.eth0.netmask", """255.255.255.0""")
    env_add(d, "nv/net.server", """192.168.3.10""")
    env_add(d, "nv/net.gateway", """192.168.3.10""")
}

python do_env_append_phycore-am335x() {
    env_add(d, "init/config-expansions",
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

#3.5" display
#of_display_timings -S /panel/display-timings/ETM0350G0DH6
""")
}

python do_env_append_phyboard-wega-am335x-2() {
    env_add(d, "init/config-expansions",
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
of_display_timings -S /panel/display-timings/ETM0700G0EDH6_WEGA
""")
}

python do_env_append_phycore-am335x-7() {
    env_add(d, "init/config-expansions",
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
    env_add(d, "init/config-expansions",
"""#!/bin/sh

. /env/expansions/am335x-wega-peb-av-01
. /env/expansions/am335x-wega-peb-eval-01

#. /env/expansions/am335x-wlan

global linux.bootargs.video="video=HDMI-A-1:1024x768-32@60"
""")
}

python do_env_append_phyboard-wega-am335x-3() {
    env_add(d, "init/config-expansions",
"""#!/bin/sh

. /env/expansions/am335x-wega-peb-av-01
. /env/expansions/am335x-wega-peb-eval-01

#. /env/expansions/am335x-wlan

global linux.bootargs.video="video=HDMI-A-1:1024x768-32@60"
""")
}

python do_env_append_phyboard-wega-r2-am335x-1() {
    env_add(d, "init/config-expansions",
"""#!/bin/sh

. /env/expansions/am335x-wega-peb-av-01
. /env/expansions/am335x-wega-peb-eval-01

#. /env/expansions/am335x-wlan

global linux.bootargs.video="video=HDMI-A-1:1024x768-32@60"
""")
}

python do_env_append_phycore-emmc-am335x-1() {
    env_add(d, "boot/spi",
"""#!/bin/sh

global.bootm.image="/dev/m25p0.kernel"
global.bootm.oftree="/dev/m25p0.oftree"

# Use rootfs from eMMC
global.linux.bootargs.dyn.root="root=/dev/mmcblk1p2 rootflags='data=journal'"
""")
}

python do_env_append_beagleboneblack-1() {
    env_add(d, "boot/mmc",
"""#!/bin/sh

global.bootm.image=/mnt/mmc0.0/zImage
global.bootm.oftree=/mnt/mmc0.0/oftree
global.linux.bootargs.dyn.root="root=/dev/mmcblk0p2 rootflags='data=journal'"
""")
    env_add(d, "boot/emmc",
"""#!/bin/sh

global.bootm.image=/mnt/mmc1.0/zImage
global.bootm.oftree=/mnt/mmc1.0/oftree
global.linux.bootargs.dyn.root="root=/dev/mmcblk1p2 rootflags='data=journal'"
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
fi
""")
}

#Enviroment changes for RAUC
python do_env_append_phycore-r2-am335x-1() {
    env_add(d, "boot/nand2",
"""#!/bin/sh

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
echo "Create NAND partitions using rauc with 2nd system"
ubiformat -q -y /dev/nand0.root
ubiattach /dev/nand0.root
#Hold the following order or change the /dev/ubi0_X enumeration in /etc/rauc/system.conf
ubimkvol -t static /dev/nand0.root.ubi kernel 8M
ubimkvol -t static /dev/nand0.root.ubi oftree 1M
#For 512MB NANDs (otherwise other partition sizes)
ubimkvol -t dynamic /dev/nand0.root.ubi root 230M
ubimkvol -t static /dev/nand0.root.ubi kernel2 8M
ubimkvol -t static /dev/nand0.root.ubi oftree2 1M
ubimkvol -t dynamic /dev/nand0.root.ubi root2 230M
ubidetach /dev/nand0.root
""")
    env_add(d, "bin/init_flash_from_mmc",
"""#!/bin/sh
echo "Initialize NAND flash for rauc from MMC"
barebox_update -t MLO.nand /mnt/mmc0.0/MLO -y
barebox_update -t nand /mnt/mmc0.0/barebox.bin -y
[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root
ubiupdatevol /dev/nand0.root.ubi.kernel  /mnt/mmc0.0/zImage
ubiupdatevol /dev/nand0.root.ubi.kernel2 /mnt/mmc0.0/zImage
ubiupdatevol /dev/nand0.root.ubi.oftree  /mnt/mmc0.0/oftree
ubiupdatevol /dev/nand0.root.ubi.oftree2 /mnt/mmc0.0/oftree
# Update rootfs image name as needed
cp /mnt/mmc0.0/root.ubifs /dev/nand0.root.ubi.root
cp /mnt/mmc0.0/root.ubifs /dev/nand0.root.ubi.root2
ubidetach /dev/nand0.root
""")
    env_add(d, "bin/init_flash_from_tftp",
"""#!/bin/sh
echo "Initialize NAND flash for rauc from TFTP"
[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root
ubiupdatevol /dev/nand0.root.ubi.kernel  /mnt/tftp/zImage
ubiupdatevol /dev/nand0.root.ubi.kernel2 /mnt/tftp/zImage
ubiupdatevol /dev/nand0.root.ubi.oftree  /mnt/tftp/oftree
ubiupdatevol /dev/nand0.root.ubi.oftree2 /mnt/tftp/oftree
# Update rootfs image name as needed
cp /mnt/tftp/root.ubifs /dev/nand0.root.ubi.root
cp /mnt/tftp/root.ubifs /dev/nand0.root.ubi.root2
ubidetach /dev/nand0.root
""")
}
python do_env_append_phycore-emmc-am335x-1() {
    env_add(d, "boot/emmc2",
"""#!/bin/sh

global.bootm.image=/mnt/mmc1.2/zImage
global.bootm.oftree=/mnt/mmc1.2/oftree
global.linux.bootargs.dyn.root="root=/dev/mmcblk1p4 rootflags='data=journal'"
""")
    env_add(d, "nv/bootchooser.state_prefix", """state""")
    env_add(d, "nv/bootchooser.targets", """system0 system1""")
    env_add(d, "nv/bootchooser.system0.boot", """emmc""")
    env_add(d, "nv/bootchooser.system1.boot", """emmc2""")
    env_add(d, "nv/bootchooser.state_prefix", """state.bootstate""")
}
#No update and init scriptes needed, because the wic scripts create a correct sdcard image

INTREE_DEFCONFIG = "am335x_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "beagleboneblack-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-2"
COMPATIBLE_MACHINE .= "|phyboard-wega-am335x-3"
COMPATIBLE_MACHINE .= "|phyboard-wega-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-am335x-7"
COMPATIBLE_MACHINE .= "|phyboard-regor-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-6"
COMPATIBLE_MACHINE .= "|phycore-emmc-am335x-1"
COMPATIBLE_MACHINE .= ")$"
