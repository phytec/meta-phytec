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
SRCREV = "7fe12e65d770f7e657e683849681f339a996418b"

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

do_compile:append() {
    if [ "${PN}" = "barebox" ] ; then
        bbnote "Adding CRC32 checksum to barebox Image Metadata"
        ${B}/scripts/bareboximd -c ${B}/${BAREBOX_BIN}
    fi
}

python do_env:append:mx6ul-generic-bsp() {
    kernelname = d.getVar("KERNEL_IMAGETYPE")
    if "secureboot" in d.getVar("DISTRO_FEATURES"):
        kernelname = "fitImage"
    sdid = "0"
    emmcid = "1"

    env_add_boot_scripts(d, kernelname, sdid, emmcid)
    env_add_bootchooser(d)

    env_add(d, "nv/dev.eth0.mode", "static")
    env_add(d, "nv/dev.eth0.ipaddr", "192.168.3.11")
    env_add(d, "nv/dev.eth0.netmask", "255.255.255.0")
    env_add(d, "nv/net.gateway", "192.168.3.10")
    env_add(d, "nv/dev.eth0.serverip", "192.168.3.10")
    env_add(d, "nv/dev.eth0.linux.devname", "eth0")
    env_add(d, "nv/dhcp.vendor_id", "phytec")

    env_add(d, "nv/boot.watchdog_timeout", "60s");

    # Disable the cpuidle function by default
    env_add(d, "nv/linux.bootargs.cpuidle", "cpuidle.off=1");
}

python do_env:append:phyboard-segin-imx6ul() {
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6ul-phytec-segin-peb-eval-01
#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02
#use this expansion when a resisitive touchscreen with stmpe touchcontroller is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02-res-stmpe
#use this expansion when a resisitive touchscreen with tsc2004 touchcontroller is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02-res-tsc2004

#use this expansion when peb-wlbt-05 adapter is connected
#. /env/expansions/imx6ul-phytec-peb-wlbt-05
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-eval-01",
"""
of_fixup_status /gpio-keys
of_fixup_status /user-leds
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02",
"""
of_fixup_status /soc/bus@2100000/lcdif@21c8000/
of_fixup_status /panel-lcd
of_fixup_status /backlight
of_fixup_status /regulator-backlight-en
of_fixup_status /soc/bus@2100000/i2c@21a0000/edt-ft5x06@38
of_fixup_status /soc/bus@2000000/pwm@2088000/
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02-res",
"""
of_fixup_status /soc/bus@2100000/lcdif@21c8000/
of_fixup_status /panel-lcd
of_fixup_status /backlight
of_fixup_status /regulator-backlight-en
of_fixup_status /soc/bus@2000000/pwm@2088000/
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02-res-tsc2004",
"""
. /env/expansions/imx6ul-phytec-segin-peb-av-02-res
of_fixup_status -d /soc/bus@2100000/i2c@21a0000/touchscreen@44
of_fixup_status /soc/bus@2100000/i2c@21a0000/touchscreen@49
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02-res-stmpe",
"""
. /env/expansions/imx6ul-phytec-segin-peb-av-02-res
of_fixup_status -d /soc/bus@2100000/i2c@21a0000/touchscreen@49
of_fixup_status /soc/bus@2100000/i2c@21a0000/touchscreen@44
""")
    env_add(d, "expansions/imx6ul-phytec-peb-wlbt-05",
"""#!/bin/sh
of_fixup_status /soc/bus@2100000/mmc@2194000
of_fixup_status -d /soc/bus@2100000/adc@2198000
of_fixup_status /regulator-wl-en
of_fixup_status /soc/bus@2100000/serial@21e8000
of_fixup_status -d /soc/bus@2000000/spba-bus@2000000/spi@2010000
of_fixup_status -d /user-leds
""")

    env_add_rauc_nand_boot_scripts(d, nandflashsize=512)
}

#No RAUC support for the low-cost Segin due to small NAND
python do_env:append:phyboard-segin-imx6ul-3() {
    env_rm(d, "boot/system0")
    env_rm(d, "boot/system1")
    env_rm_bootchooser(d)
    env_rm_rauc_nand_boot_scripts(d)

    #Default CMA size (128 MB) is too big for the 256 MB RAM so it has to be
    #reduced to 64 MB.
    env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
}

python do_env:append:phyboard-segin-imx6ul-5() {
    env_rm(d, "config-expansions")
    env_add(d, "config-expansions",
"""#!/bin/sh

. /env/expansions/imx6ul-phytec-segin-peb-eval-01
#use this expansion when a capacitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02
#use this expansion when a resisitive touchscreen is connected
#. /env/expansions/imx6ul-phytec-segin-peb-av-02-res

#use this expansion when peb-wlbt-05 adapter is connected
#. /env/expansions/imx6ul-phytec-peb-wlbt-05
""")
}

python do_env:append:phyboard-segin-imx6ul-6() {
    env_add(d, "nv/linux.bootargs.cma", "cma=128M\n")
}

python do_env:append:phyboard-segin-imx6ul-7() {
    env_rm_rauc_nand_boot_scripts(d)
}

python do_env:append:phyboard-segin-imx6ul-8() {
    env_rm_rauc_nand_boot_scripts(d)
    env_add(d, "nv/linux.bootargs.cma", "cma=128M\n")
}

INTREE_DEFCONFIG = "imx_v7_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .=  "phyboard-segin-imx6ul-2"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-3"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-4"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-5"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-6"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-7"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-8"

COMPATIBLE_MACHINE .= "|phygate-tauri-s-imx6ul-1"
COMPATIBLE_MACHINE .= ")$"
