inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI += "\
    ${@base_conditional('DEBUG_BUILD','1','file://debugging.cfg','',d)} \
    file://disable_state.cfg \
"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id and barebox-ipl!
SRCREV = "223922f03670d489579d2dcb192af92f3543759b"

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

global.bootm.image=/mnt/mmc0.0/linuximage
global.bootm.oftree=/mnt/mmc0.0/oftree
global.linux.bootargs.dyn.root="root=/dev/mmcblk0p2 rootflags='data=journal'"
""")
    env_add(d, "boot/emmc",
"""#!/bin/sh

global.bootm.image=/mnt/mmc1.0/linuximage
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

INTREE_DEFCONFIG = "am335x_defconfig"

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
COMPATIBLE_MACHINE .= "|phycore-am335x-7"
COMPATIBLE_MACHINE .= "|phyboard-regor-am335x-1"
COMPATIBLE_MACHINE .= "|phyboard-regor-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-emmc-am335x-1"
