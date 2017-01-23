# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2


GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "5e936732882e16603ddb73bbd8265080967a14ba"

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
"""of_fixup_status /soc/aips-bus@02000000/ldb@020e0008/
of_fixup_status /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0
of_fixup_status /soc/aips-bus@02000000/pwm@02080000
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/stmpe@44
of_fixup_status /backlight
""")
    env_add(d, "expansions/imx6qdl-mira-peb-eval-01",
"""of_fixup_status /soc/aips-bus@02100000/serial@021e8000
of_fixup_status /gpio-keys
of_fixup_status /user_leds
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@02000000/ldb@020e0008/
of_fixup_status /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@02100000/i2c@021a4000/edt-ft5x06@38
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-018-peb-av-02",
"""of_fixup_status /display@di0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/edt-ft5x06@38
of_fixup_status /soc/aips-bus@02000000/pwm@02080000
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-018-peb-av-02-res",
"""of_fixup_status /display@di0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/stmpe@44
of_fixup_status /soc/aips-bus@02000000/pwm@02080000
""")
    env_add(d, "expansions/imx6qdl-phytec-peb-wlbt-01",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@02100000/usdhc@02198000
of_fixup_status /regulators/regulator@7
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

python do_env_append_phycard-imx6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#use this expansion when a capacitive touchscreen is connected
. /env/expansions/imx6qdl-phytec-lcd

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-res

# imx6qdl-phytec-lcd: 7" display
of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0700G0DH6

# imx6qdl-phytec-lcd: 5.7" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETMV570G2DHU

# imx6qdl-phytec-lcd: 4.3" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0430G0DH6

# imx6qdl-phytec-lcd: 3.5" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0350G0DH6


#Enable VM-010-BW-LVDS
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-res",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@02000000/ldb@020e0008/
of_fixup_status /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@02100000/i2c@021a4000/stmpe@44
""")
}

python do_env_append_phyflex-imx6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#use this expansion when a capacitive touchscreen is connected
. /env/expansions/imx6qdl-phytec-lcd

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-res

# imx6qdl-phytec-lcd: 7" display
of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0700G0DH6

# imx6qdl-phytec-lcd: 5.7" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETMV570G2DHU

# imx6qdl-phytec-lcd: 4.3" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0430G0DH6

# imx6qdl-phytec-lcd: 3.5" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0350G0DH6


#Enable VM-011-COL on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-P VM-011-COL

#Enable VM-011-COL on CSI1
#of_camera_selection -a 0x48 -p 1 -b phyCAM-P VM-011-COL
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-res",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@02000000/ldb@020e0008/
of_fixup_status /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@02100000/i2c@021a4000/stmpe@41
""")
    # Fix QtWebkit rendering issue for LVDS and DVI output on phyFLEX-CarrierBoard
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env_append_phyboard-alcor-imx6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#. /env/expansions/imx6qdl-phytec-lcd

# imx6qdl-phytec-lcd: 7" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0700G0DH6

# imx6qdl-phytec-lcd: 5.7" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETMV570G2DHU

# imx6qdl-phytec-lcd: 4.3" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0430G0DH6

# imx6qdl-phytec-lcd: 3.5" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0350G0DH6
""")
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

# imx6qdl-phytec-lcd: 7" display (AC138 and AC156)
#of_display_timings -S /display@di0/display-timings/ETM0700G0EDH6

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -S /display@di0/display-timings/ETM0700G0DH6

# imx6qdl-phytec-lcd: 5.7" display
#of_display_timings -S /display@di0/display-timings/ETMV570G2DHU

# imx6qdl-phytec-lcd: 4.3" display
#of_display_timings -S /display@di0/display-timings/ETM0430G0DH6

# imx6qdl-phytec-lcd: 3.5" display
#of_display_timings -S /display@di0/display-timings/ETM0350G0DH6

#Enable VM-010-BW-LVDS
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
}

python do_env_append_phyboard-subra-imx6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#. /env/expansions/imx6qdl-phytec-lcd

# imx6qdl-phytec-lcd: 7" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0700G0DH6

# imx6qdl-phytec-lcd: 5.7" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETMV570G2DHU

# imx6qdl-phytec-lcd: 4.3" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0430G0DH6

# imx6qdl-phytec-lcd: 3.5" display
#of_display_timings -S /soc/aips-bus@02000000/ldb@020e0008/lvds-channel@0/display-timings/ETM0350G0DH6


#Enable VM-010-BW-LVDS
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW

#of_camera_selection -a 0x48 -p 1 -b phyCAM-S+ VM-010-BW
""")
}

python do_env_append_phyflex-imx6-3() {
    env_add(d, "nv/linux.bootargs.cma", "cma=265M@1G\n")
}

python do_env_append_phyflex-imx6-4() {
    env_add(d, "nv/linux.bootargs.cma", "cma=265M@1G\n")
}

python do_env_append_phyboard-mira-imx6-4() {
    env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
}

python do_env_append_phyboard-mira-imx6-5() {
    env_add(d, "nv/linux.bootargs.cma", "cma=265M@1G\n")
}

python do_env_append_phyboard-mira-imx6-6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
. /env/expansions/imx6qdl-phytec-peb-wlbt-01

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC138 and AC156)
#of_display_timings -S /display@di0/display-timings/ETM0700G0EDH6

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -S /display@di0/display-timings/ETM0700G0DH6

# imx6qdl-phytec-lcd: 5.7" display
#of_display_timings -S /display@di0/display-timings/ETMV570G2DHU

# imx6qdl-phytec-lcd: 4.3" display
#of_display_timings -S /display@di0/display-timings/ETM0430G0DH6

# imx6qdl-phytec-lcd: 3.5" display
#of_display_timings -S /display@di0/display-timings/ETM0350G0DH6

#Enable VM-010-BW-LVDS
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
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

# imx6qdl-phytec-lcd: 7" display (AC138 and AC156)
#of_display_timings -S /display@di0/display-timings/ETM0700G0EDH6

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -S /display@di0/display-timings/ETM0700G0DH6

# imx6qdl-phytec-lcd: 5.7" display
#of_display_timings -S /display@di0/display-timings/ETMV570G2DHU

# imx6qdl-phytec-lcd: 4.3" display
#of_display_timings -S /display@di0/display-timings/ETM0430G0DH6

# imx6qdl-phytec-lcd: 3.5" display
#of_display_timings -S /display@di0/display-timings/ETM0350G0DH6

#Enable VM-010-BW-LVDS
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

# imx6qdl-phytec-lcd: 7" display (AC138 and AC156)
of_display_timings -S /display@di0/display-timings/ETM0700G0EDH6

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_display_timings -S /display@di0/display-timings/ETM0700G0DH6

# imx6qdl-phytec-lcd: 5.7" display
#of_display_timings -S /display@di0/display-timings/ETMV570G2DHU

# imx6qdl-phytec-lcd: 4.3" display
#of_display_timings -S /display@di0/display-timings/ETM0430G0DH6

# imx6qdl-phytec-lcd: 3.5" display
#of_display_timings -S /display@di0/display-timings/ETM0350G0DH6

#Enable VM-010-BW-LVDS
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
}

python do_env_append_phyboard-segin-imx6ul-1() {
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

global.linux.bootargs.dyn.root="root=ubi0:root ubi.mtd=root rootfstype=ubifs rw"
""")
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6ul-phytec-segin-peb-eval-01
#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02
#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02-res

# imx6ul-phytec-lcd: 7" display
#of_display_timings -S /soc/aips-bus@02100000/lcdif@021c8000/display@di0/display-timings/ETM0700G0EDH6

# imx6ul-phytec-lcd: 5.7" display
#of_display_timings -S /soc/aips-bus@02100000/lcdif@021c8000/display@di0/display-timings/ETMV570G2DHU

# imx6ul-phytec-lcd: 4.3" display
#of_display_timings -S /soc/aips-bus@02100000/lcdif@021c8000/display@di0/display-timings/ETM0430G0DH6

# imx6ul-phytec-lcd: 3.5" display
#of_display_timings -S /soc/aips-bus@02100000/lcdif@021c8000/display@di0/display-timings/ETM0350G0DH6
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-eval-01",
"""
of_fixup_status /gpio-keys
of_fixup_status /user_leds
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02",
"""
of_fixup_status /soc/aips-bus@02100000/lcdif@021c8000/
of_fixup_status /soc/aips-bus@02100000/lcdif@021c8000/display@di0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/edt-ft5x06@38
of_fixup_status /soc/aips-bus@02000000/pwm@02088000/
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02-res",
"""
of_fixup_status /soc/aips-bus@02100000/lcdif@021c8000/
of_fixup_status /soc/aips-bus@02100000/lcdif@021c8000/display@di0
of_fixup_status /backlight
of_fixup_status /soc/aips-bus@02100000/i2c@021a0000/stmpe@44
of_fixup_status /soc/aips-bus@02000000/pwm@02088000/
""")
}

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

COMPATIBLE_MACHINE .= "|phycard-imx6-1"
COMPATIBLE_MACHINE .= "|phycard-imx6-2"

COMPATIBLE_MACHINE .= "|phyboard-alcor-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-subra-imx6-1"
COMPATIBLE_MACHINE .= "|phyboard-subra-imx6-2"

COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-3"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-4"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-5"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-6"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-7"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-9"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-10"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-11"
COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-12"

COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-1"
