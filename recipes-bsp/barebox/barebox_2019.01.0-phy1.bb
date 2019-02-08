# Copyright (C) 2015 PHYTEC Messtechnik GmbH,

inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2


GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"
SRC_URI += "\
    ${@oe.utils.conditional('DEBUG_BUILD','1','file://debugging.cfg','',d)} \
"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "e8187482dba74fb5d52812bc2984a02eeb1db30d"

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

python do_env_append_mx6ul() {
    env_add(d, "boot/mmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/mnt/mmc/zImage"
global.bootm.oftree="/mnt/mmc/oftree"
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
    env_add(d, "nv/dev.eth0.mode", "static")
    env_add(d, "nv/dev.eth0.ipaddr", "192.168.3.11")
    env_add(d, "nv/dev.eth0.netmask", "255.255.255.0")
    env_add(d, "nv/net.gateway", "192.168.3.10")
    env_add(d, "nv/dev.eth0.serverip", "192.168.3.10")
    env_add(d, "nv/dhcp.vendor_id", "phytec")
}

python do_env_append_phyboard-segin-imx6ul() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6ul-phytec-segin-peb-eval-01
#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02
#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02-res

#use this expansion when peb-wlbt-01 adapter is connected
#. /env/expansions/imx6ul-phytec-peb-wlbt-01

# imx6ul-phytec-lcd: 7" display
#of_display_timings -S /soc/aips-bus@2100000/lcdif@21c8000/display@di0/display-timings/ETM0700G0EDH6

# imx6ul-phytec-lcd: 5.7" display
#of_display_timings -S /soc/aips-bus@2100000/lcdif@21c8000/display@di0/display-timings/ETMV570G2DHU

# imx6ul-phytec-lcd: 4.3" display
#of_display_timings -S /soc/aips-bus@2100000/lcdif@21c8000/display@di0/display-timings/ETM0430G0DH6

# imx6ul-phytec-lcd: 3.5" display
#of_display_timings -S /soc/aips-bus@2100000/lcdif@21c8000/display@di0/display-timings/ETM0350G0DH6

#use this bootarg when the VM010 Color is connected
#global linux.bootargs.mt9v022="mt9v022.sensor_type=color"
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-eval-01",
"""
of_fixup_status /gpio-keys
of_fixup_status /user_leds
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02",
"""
of_fixup_status /soc/aips-bus@2100000/lcdif@21c8000/
of_fixup_status /soc/aips-bus@2100000/lcdif@21c8000/display@di0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@2100000/i2c@21a0000/edt-ft5x06@38
of_fixup_status /soc/aips-bus@2000000/pwm@2088000/
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02-res",
"""
of_fixup_status /soc/aips-bus@2100000/lcdif@21c8000/
of_fixup_status /soc/aips-bus@2100000/lcdif@21c8000/display@di0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@2100000/i2c@21a0000/stmpe@44
of_fixup_status /soc/aips-bus@2000000/pwm@2088000/
""")
    env_add(d, "expansions/imx6ul-phytec-peb-wlbt-01",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@2100000/usdhc@2194000
of_fixup_status /regulators/regulator@7
of_fixup_status -d /soc/aips-bus@2100000/adc@2198000
""")
}

python do_env_append_phyboard-segin-imx6ul-5() {
    env_rm(d, "config-expansions")
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6ul-phytec-segin-peb-eval-01
#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02
#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02-res

#use this expansion when peb-wlbt-01 adapter is connected
. /env/expansions/imx6ul-phytec-peb-wlbt-01

# imx6ul-phytec-lcd: 7" display
#of_display_timings -S /soc/aips-bus@2100000/lcdif@21c8000/display@di0/display-timings/ETM0700G0EDH6

# imx6ul-phytec-lcd: 5.7" display
#of_display_timings -S /soc/aips-bus@2100000/lcdif@21c8000/display@di0/display-timings/ETMV570G2DHU

# imx6ul-phytec-lcd: 4.3" display
#of_display_timings -S /soc/aips-bus@2100000/lcdif@21c8000/display@di0/display-timings/ETM0430G0DH6

# imx6ul-phytec-lcd: 3.5" display
#of_display_timings -S /soc/aips-bus@2100000/lcdif@21c8000/display@di0/display-timings/ETM0350G0DH6

#use this bootarg when the VM010 Color is connected
#nv linux.bootargs.mt9v022="mt9v022.sensor_type=color"
""")
}

INTREE_DEFCONFIG = "imx_v7_defconfig"

COMPATIBLE_MACHINE = "phyboard-segin-imx6ul-2"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-3"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-4"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-5"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-6"
