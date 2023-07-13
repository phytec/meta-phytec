# Copyright (C) 2018 PHYTEC Messtechnik GmbH,
# Author: Stefan Riedmueller <s.riedmueller@phytec.de>

inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2

include barebox-secureboot.inc
include barebox-protectionshield.inc
include barebox-boot-scripts.inc

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "f9db4e968adc223b52a75f975fd8d1fe4cbbe774"

python do_env:append() {
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

    if "secureboot" not in d.getVar("DISTRO_FEATURES"):
        env_add(d, "expansions/dt-overlays",
"""#!/bin/sh

path="$global.overlays.path"

if [ -e /env/physelect ] ; then
    /env/physelect
fi

if [ -e ${path}/select ] ; then
    readf ${path}/select global.overlays.select
fi

for o in $global.overlays.select ; do
    if [ -e ${path}/${o} ] ; then
        echo "Add ${path}/${o} overlay"
        of_overlay ${path}/${o}
    fi
done
""")
        env_add(d, "nv/overlays.select", "")
}

python do_env:append:mx6-generic-bsp() {
    kernelname = d.getVar("KERNEL_IMAGETYPE")
    if "secureboot" in d.getVar("DISTRO_FEATURES"):
        kernelname = "fitImage"
    sdid = "2"
    emmcid = None
    dhcp_vendor = "phyFLEX-i.MX6"

    if "phyboard" in d.getVar("SOC_FAMILY"):
        sdid = "0"
        emmcid = "3"
        dhcp_vendor = "phyCORE-i.MX6"

    if "phycard" in d.getVar("SOC_FAMILY"):
        dhcp_vendor = "phyCARD-i.MX6"

    env_add_boot_scripts(d, kernelname, sdid, emmcid)
    env_add_bootchooser(d)

    env_add(d, "expansions/imx6qdl-mira-enable-lvds",
"""of_fixup_status /ldb/lvds-channel@0
of_fixup_status /soc/bus@2100000/i2c@21a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-nunki-enable-lvds",
"""of_fixup_status /ldb/lvds-channel@0
of_fixup_status /soc/bus@2100000/i2c@21a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-mira-peb-eval-01",
"""of_fixup_status /gpio-keys
of_fixup_status /user-leds
of_property -s -f -e $global.bootm.oftree /soc/bus@2100000/serial@21ec000 pinctrl-0 </soc/bus@2000000/pinctrl@20e0000/uart3grp>
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd",
"""#!/bin/sh
of_fixup_status /panel-lcd
of_fixup_status /ldb/lvds-channel@0
of_fixup_status /soc/bus@2100000/i2c@21a4000/polytouch@38
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-res",
"""#!/bin/sh
of_fixup_status /panel-lcd
of_fixup_status /ldb/lvds-channel@0
of_fixup_status /soc/bus@2100000/i2c@21a4000/touchctrl@41
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-018-peb-av-02",
"""of_fixup_status /panel-lcd
of_fixup_status /display@di0
of_fixup_status /soc/bus@2100000/i2c@21a0000/polytouch@38
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-018-peb-av-02-res",
"""of_fixup_status /panel-lcd
of_fixup_status /display@di0
of_fixup_status /soc/bus@2100000/i2c@21a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-phytec-peb-wlbt-05",
"""#!/bin/sh
of_fixup_status /soc/bus@2100000/mmc@2198000
of_fixup_status /regulator-wl-en
of_fixup_status -d /gpio-keys
of_fixup_status /soc/bus@2100000/serial@21ec000/bluetooth
of_fixup_status -d /user-leds
of_property -s -f -e $global.bootm.oftree /soc/bus@2100000/serial@21ec000 pinctrl-0 </soc/bus@2000000/pinctrl@20e0000/uart3grp_bt>
""")
    env_add(d, "nv/dev.eth0.mode", "static")
    env_add(d, "nv/dev.eth0.ipaddr", "192.168.3.11")
    env_add(d, "nv/dev.eth0.netmask", "255.255.255.0")
    env_add(d, "nv/net.gateway", "192.168.3.10")
    env_add(d, "nv/dev.eth0.serverip", "192.168.3.10")
    env_add(d, "nv/dev.eth0.linux.devname", "eth0")
    env_add(d, "nv/dhcp.vendor_id", "barebox-{}".format(dhcp_vendor))

    if ("phyboard-mira-imx6" in d.getVar("SOC_FAMILY")) or \
    ("phyboard-nunki-imx6" in d.getVar("SOC_FAMILY")):
        if "secureboot" not in d.getVar("DISTRO_FEATURES"):
            env_add(d, "physelect",
"""#!/bin/sh

path="$global.overlays.path"

let PHY_ID=${mdio0-phy03.phy_id}

let KSZ9031_ID=0x00221620
let KSZ9131_ID=0x00221640
let KSZ_MASK=0x00fffff0
let ADIN1300_ID=0x0283bc30
let ADIN_MASK=0x0fffffff

let "KSZ9031=KSZ9031_ID==(PHY_ID&KSZ_MASK)"
let "KSZ9131=KSZ9131_ID==(PHY_ID&KSZ_MASK)"
let "ADIN=ADIN1300_ID==(PHY_ID&ADIN_MASK)"

if [ $KSZ9031 -eq 1 ]; then
    exit 0
elif [ $KSZ9131 -eq 1 ]; then
    of_overlay ${path}/imx6-phy-ksz9131.dtbo
elif [ $ADIN -eq 1 ]; then
    of_overlay ${path}/imx6-phy-adin1300.dtbo
else
    echo "No PHY found!"
    exit 1
fi
""")
        else:
            env_add(d, "physelect-secureboot",
"""#!/bin/sh

image="$global.bootm.image"

let PHY_ID=${mdio0-phy03.phy_id}

let KSZ9031_ID=0x00221620
let KSZ_MASK=0x00fffff0
let ADIN1300_ID=0x0283bc30
let ADIN_MASK=0x0fffffff

let "KSZ=KSZ9031_ID==(PHY_ID&KSZ_MASK)"
let "ADIN=ADIN1300_ID==(PHY_ID&ADIN_MASK)"

if [ $ADIN -eq 1 ]; then
    global.bootm.image=${image}@phyboard-imx6-phy-adin1300.dtb
fi
""")
}

python do_env:append:phyflex-imx6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#use this expansion when a capacitive touchscreen is connected
. /env/expansions/imx6qdl-phytec-lcd

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"
""")
    # Enable 32 bit color depth for framebuffer emulation on phyFLEX-CarrierBoard
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env:append:phyboard-mira-imx6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
#. /env/expansions/imx6qdl-phytec-peb-wlbt-05

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"
""")
    # Enable 32 bit color depth for framebuffer emulation on phyBOARD-Mira
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env:append:phyboard-nunki-imx6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#. /env/expansions/imx6qdl-nunki-enable-lvds

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"
""")
    if "secureboot" not in d.getVar("DISTRO_FEATURES"):
        # Enable VM-016 by default on phyBOARD-Nunki
        env_add(d, "nv/overlays.select", "imx6-vm010-bw-0.dtbo\n");
        # Enable 32 bit color depth for framebuffer emulation on phyBOARD-Nunki
        env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env:append:phyboard-mira-imx6-6() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
. /env/expansions/imx6qdl-phytec-peb-wlbt-05

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"
""")
}

python do_env:append:phyboard-mira-imx6-10() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
. /env/expansions/imx6qdl-phytec-peb-wlbt-05

#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"
""")
}

python do_env:append:phyboard-mira-imx6-11() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6qdl-mira-peb-eval-01
#. /env/expansions/imx6qdl-mira-enable-lvds
#. /env/expansions/imx6qdl-phytec-peb-wlbt-05

#use this expansion when a capacitive touchscreen is connected
. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"
""")
}

python do_env:append:phyboard-mira-imx6-13() {
    env_add(d, "config-expansions",
"""#!/bin/sh

#. /env/expansions/imx6qdl-mira-enable-lvds
#. /env/expansions/imx6qdl-phytec-peb-wlbt-05

#use this expansion when a capacitive touchscreen is connected
. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02

#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6qdl-phytec-lcd-018-peb-av-02-res

# imx6qdl-phytec-lcd: 7" display (AC158 / AC138)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"
""")
}

#Enviroment changes for RAUC
python do_env:append:phyboard-mira-imx6-3() {
    env_add_rauc_nand_boot_scripts(d)
}

python do_env:append:phyboard-mira-imx6-13() {
    env_add_rauc_nand_boot_scripts(d)
}

do_compile:append() {
    if [ "${PN}" = "barebox" ] ; then
        bbnote "Adding CRC32 checksum to barebox Image Metadata"
        ${B}/scripts/bareboximd -c ${B}/${BAREBOX_BIN}
    fi
}

#Reduce CMA size on phyboard-mira-imx6 machines with 256MiB of RAM
python do_env:append:phyboard-mira-imx6() {
    # Mira machine types 4 and 15 have only 256MiB of RAM, set CMA to 64MiB
    if d.getVar("MACHINE") in ("phyboard-mira-imx6-4", "phyboard-mira-imx6-15"):
        env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
}

#Reduce CMA size on phyflex-imx6 machines with less than 512MiB of RAM
python do_env:append:phyflex-imx6() {
    # phyFLEX machine types 5 and 8 have only 512MiB of RAM, set CMA to 128MiB
    if d.getVar("MACHINE") in ("phyflex-imx6-5", "phyflex-imx6-8"):
        env_add(d, "nv/linux.bootargs.cma", "cma=128M\n")
    # phyFLEX machine type 9 has only 256MiB of RAM, set CMA to 64MiB
    if "phyflex-imx6-9" in d.getVar("MACHINE"):
        env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
    # phyFLEX machine type 10 has only 128MiB of RAM, set CMA to 32MiB
    if "phyflex-imx6-10" in d.getVar("MACHINE"):
        env_add(d, "nv/linux.bootargs.cma", "cma=32M\n")
}

INTREE_DEFCONFIG = "imx_v7_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyflex-imx6-1"
COMPATIBLE_MACHINE .= "|phyflex-imx6-2"
COMPATIBLE_MACHINE .= "|phyflex-imx6-3"
COMPATIBLE_MACHINE .= "|phyflex-imx6-4"
COMPATIBLE_MACHINE .= "|phyflex-imx6-5"
COMPATIBLE_MACHINE .= "|phyflex-imx6-6"
COMPATIBLE_MACHINE .= "|phyflex-imx6-7"
COMPATIBLE_MACHINE .= "|phyflex-imx6-8"
COMPATIBLE_MACHINE .= "|phyflex-imx6-9"
COMPATIBLE_MACHINE .= "|phyflex-imx6-10"
COMPATIBLE_MACHINE .= "|phyflex-imx6-11"

COMPATIBLE_MACHINE .= "|phycard-imx6-2"

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
COMPATIBLE_MACHINE .= ")$"
