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
SRCREV = "81971ebb28a9b30d3eb7bb4e951a6b2ce4d8a044"

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
    env_add(d, "boot/emmc",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

global.bootm.image="/mnt/emmc/zImage"
global.bootm.oftree="/mnt/emmc/oftree"
global.linux.bootargs.dyn.root="root=/dev/mmcblk1p2 rootflags='discard,data=journal'"
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

    #NAND boot scripts for RAUC
    env_add(d, "boot/nand0",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root

global.bootm.image="/dev/nand0.root.ubi.kernel0"
global.bootm.oftree="/dev/nand0.root.ubi.oftree0"

global.linux.bootargs.dyn.root="root=ubi0:root0 ubi.mtd=root rootfstype=ubifs"
""")
    env_add(d, "boot/nand1",
"""#!/bin/sh

[ -e /env/config-expansions ] && /env/config-expansions

[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root

global.bootm.image="/dev/nand0.root.ubi.kernel1"
global.bootm.oftree="/dev/nand0.root.ubi.oftree1"

global.linux.bootargs.dyn.root="root=ubi0:root1 ubi.mtd=root rootfstype=ubifs"
""")
    env_add(d, "nv/bootchooser.targets", """system0 system1""")
    env_add(d, "nv/bootchooser.system0.boot", """nand0""")
    env_add(d, "nv/bootchooser.system1.boot", """nand1""")
    env_add(d, "nv/bootchooser.state_prefix", """state.bootstate""")
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

#use this bootarg if the VM010 Color is connected
#. /env/expansions/imx6ul-phytec-vm010-col

#use this bootarg if the VM010 Monochrome is connected
#. /env/expansions/imx6ul-phytec-vm010-bw

#use this bootarg if the VM011 Color is connected
#. /env/expansions/imx6ul-phytec-vm011-col

#use this bootarg if the VM011 Monochrome is connected
#. /env/expansions/imx6ul-phytec-vm011-bw

#use this bootarg if the VM009 is connected
#. /env/expansions/imx6ul-phytec-vm009
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-eval-01",
"""
of_fixup_status /gpio-keys
of_fixup_status /user-leds
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
of_fixup_status /regulator-wlan-en
of_fixup_status -d /soc/aips-bus@2100000/adc@2198000
""")
    env_add(d, "expansions/imx6ul-phytec-vm010-col",
"""#!/bin/sh
CAM_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "aptina,mt9v024"
of_property -f -d ${CAM_PATH} assigned-clocks
of_property -f -d ${CAM_PATH} assigned-clock-parents
of_property -f -d ${CAM_PATH} assigned-clock-rates

of_fixup_status ${CAM_PATH}
""")
    env_add(d, "expansions/imx6ul-phytec-vm010-bw",
"""#!/bin/sh
CAM_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "aptina,mt9v024m"
of_property -f -d ${CAM_PATH} assigned-clocks
of_property -f -d ${CAM_PATH} assigned-clock-parents
of_property -f -d ${CAM_PATH} assigned-clock-rates

of_fixup_status ${CAM_PATH}
""")
    env_add(d, "expansions/imx6ul-phytec-vm011-col",
"""#!/bin/sh
CAM_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "aptina,mt9p006"
of_property -f -d ${ENDPOINT_PATH} link-frequencies
of_property -f -s ${ENDPOINT_PATH} input-clock-frequency <50000000>
of_property -f -s ${ENDPOINT_PATH} pixel-clock-frequency <50000000>
of_property -f -s ${ENDPOINT_PATH} pclk-sample <0>

of_fixup_status ${CAM_PATH}
""")
    env_add(d, "expansions/imx6ul-phytec-vm011-bw",
"""#!/bin/sh
CAM_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "aptina,mt9p006m"
of_property -f -d ${ENDPOINT_PATH} link-frequencies
of_property -f -s ${ENDPOINT_PATH} input-clock-frequency <50000000>
of_property -f -s ${ENDPOINT_PATH} pixel-clock-frequency <50000000>
of_property -f -s ${ENDPOINT_PATH} pclk-sample <0>

of_fixup_status ${CAM_PATH}
""")
    env_add(d, "expansions/imx6ul-phytec-vm009",
"""#!/bin/sh
CAM_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/aips-bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "micron,mt9m111"
of_property -f -s ${CAM_PATH} clock-names "mclk"
of_property -f -s ${CAM_PATH} phytec,invert-pixclk
of_property -f -s ${CAM_PATH} phytec,allow-10bit
of_property -f -d ${ENDPOINT_PATH} link-frequencies

of_fixup_status ${CAM_PATH}
""")

    #RAUC init scripts for NAND
    env_add(d, "bin/rauc_init_nand",
"""#!/bin/sh
echo "Format /dev/nand0.root"

ubiformat -q /dev/nand0.root
ubiattach /dev/nand0.root
ubimkvol -t static /dev/nand0.root.ubi kernel0 8M
ubimkvol -t static /dev/nand0.root.ubi kernel1 8M
ubimkvol -t static /dev/nand0.root.ubi oftree0 1M
ubimkvol -t static /dev/nand0.root.ubi oftree1 1M
ubimkvol /dev/nand0.root.ubi root0 244M
ubimkvol /dev/nand0.root.ubi root1 0

ubidetach 0
""")
    env_add(d, "bin/rauc_flash_nand_from_mmc",
"""#!/bin/sh
echo "Initialize NAND flash from MMC"
[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root
ubiupdatevol /dev/nand0.root.ubi.kernel0 /mnt/mmc0.0/zImage
ubiupdatevol /dev/nand0.root.ubi.kernel1 /mnt/mmc0.0/zImage
ubiupdatevol /dev/nand0.root.ubi.oftree0 /mnt/mmc0.0/oftree
ubiupdatevol /dev/nand0.root.ubi.oftree1 /mnt/mmc0.0/oftree
cp /mnt/mmc0.0/root.ubifs /dev/nand0.root.ubi.root0
cp /mnt/mmc0.0/root.ubifs /dev/nand0.root.ubi.root1
""")
    env_add(d, "bin/rauc_flash_nand_from_tftp",
"""#!/bin/sh
echo "Initialize NAND flash from TFTP"
[ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root
ubiupdatevol /dev/nand0.root.ubi.kernel0 /mnt/tftp/zImage
ubiupdatevol /dev/nand0.root.ubi.kernel1 /mnt/tftp/zImage
ubiupdatevol /dev/nand0.root.ubi.oftree0 /mnt/tftp/oftree
ubiupdatevol /dev/nand0.root.ubi.oftree1 /mnt/tftp/oftree
cp /mnt/tftp/root.ubifs /dev/nand0.root.ubi.root0
cp /mnt/tftp/root.ubifs /dev/nand0.root.ubi.root1
""")
}

#No RAUC support for the low-cost Segin due to small NAND
python do_env_append_phyboard-segin-imx6ul-3() {
    env_rm(d, "boot/nand0")
    env_rm(d, "boot/nand1")
    env_rm(d, "nv/bootchooser.targets")
    env_rm(d, "nv/bootchooser.system0.boot")
    env_rm(d, "nv/bootchooser.system1.boot")
    env_rm(d, "nv/bootchooser.state_prefix")
    env_rm(d, "bin/rauc_flash_nand_from_mmc")
    env_rm(d, "bin/rauc_flash_nand_from_tftp")
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

#Currently there is no rauc support for eMMC
python do_env_append_phyboard-segin-imx6ul-7() {
    env_rm(d, "boot/nand0")
    env_rm(d, "boot/nand1")
    env_rm(d, "nv/bootchooser.targets")
    env_rm(d, "nv/bootchooser.system0.boot")
    env_rm(d, "nv/bootchooser.system1.boot")
    env_rm(d, "nv/bootchooser.state_prefix")
    env_rm(d, "bin/rauc_flash_nand_from_mmc")
    env_rm(d, "bin/rauc_flash_nand_from_tftp")
}

INTREE_DEFCONFIG = "imx_v7_defconfig"

COMPATIBLE_MACHINE = "phyboard-segin-imx6ul-2"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-3"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-4"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-5"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-6"
COMPATIBLE_MACHINE .= "|phyboard-segin-imx6ul-7"
