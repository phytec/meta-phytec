# Copyright (C) 2015 PHYTEC Messtechnik GmbH,

inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2

include barebox-boot-scripts.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=f5125d13e000b9ca1f0d3364286c4192"

_XTRA_SETUP = "${@bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'secureboot', 'none', d)}"
include ${THISDIR}/barebox-${_XTRA_SETUP}.inc

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"

S = "${WORKDIR}/git"

PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id!
SRCREV = "630e5953e31a32545117c0ea046f4216c483b274"

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
    kernelname = d.getVar("KERNEL_IMAGETYPE", True)
    sdid = "0"
    emmcid = "1"

    env_add_boot_scripts(d, kernelname, sdid, emmcid)

    env_add(d, "nv/dev.eth0.mode", "static")
    env_add(d, "nv/dev.eth0.ipaddr", "192.168.3.11")
    env_add(d, "nv/dev.eth0.netmask", "255.255.255.0")
    env_add(d, "nv/net.gateway", "192.168.3.10")
    env_add(d, "nv/dev.eth0.serverip", "192.168.3.10")
    env_add(d, "nv/dev.eth0.linux.devname", "eth0")
    env_add(d, "nv/dhcp.vendor_id", "phytec")

    env_add(d, "nv/bootchooser.targets", """system0 system1""")
    env_add(d, "nv/bootchooser.system0.boot", """system0""")
    env_add(d, "nv/bootchooser.system1.boot", """system1""")
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

#use this expansion when peb-wlbt-05 adapter is connected
#. /env/expansions/imx6ul-phytec-peb-wlbt-05

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
of_fixup_status /soc/bus@2100000/lcdif@21c8000/
of_fixup_status /panel-lcd
of_fixup_status /backlight
of_fixup_status /soc/bus@2100000/i2c@21a0000/edt-ft5x06@38
of_fixup_status /soc/bus@2000000/pwm@2088000/
""")
    env_add(d, "expansions/imx6ul-phytec-segin-peb-av-02-res",
"""
of_fixup_status /soc/bus@2100000/lcdif@21c8000/
of_fixup_status /panel-lcd
of_fixup_status /backlight
of_fixup_status /soc/bus@2100000/i2c@21a0000/touchscreen@44
of_fixup_status /soc/bus@2000000/pwm@2088000/
""")
    env_add(d, "expansions/imx6ul-phytec-peb-wlbt-01",
"""#!/bin/sh
of_fixup_status /soc/bus@2100000/usdhc@2194000
of_fixup_status /regulator-wlan-en
of_fixup_status -d /soc/bus@2100000/adc@2198000
""")
    env_add(d, "expansions/imx6ul-phytec-peb-wlbt-05",
"""#!/bin/sh
of_fixup_status /soc/bus@2100000/serial@21e8000
of_fixup_status /soc/bus@2100000/usdhc@2194000
of_fixup_status /regulator-bt-en
of_fixup_status /regulator-wlan-en
of_fixup_status -d /soc/bus@2100000/adc@2198000
""")
    env_add(d, "expansions/imx6ul-phytec-vm010-col",
"""#!/bin/sh
CAM_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "aptina,mt9v024"
of_property -f -d ${CAM_PATH} assigned-clocks
of_property -f -d ${CAM_PATH} assigned-clock-parents
of_property -f -d ${CAM_PATH} assigned-clock-rates

of_fixup_status ${CAM_PATH}
""")
    env_add(d, "expansions/imx6ul-phytec-vm010-bw",
"""#!/bin/sh
CAM_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "aptina,mt9v024m"
of_property -f -d ${CAM_PATH} assigned-clocks
of_property -f -d ${CAM_PATH} assigned-clock-parents
of_property -f -d ${CAM_PATH} assigned-clock-rates

of_fixup_status ${CAM_PATH}
""")
    env_add(d, "expansions/imx6ul-phytec-vm011-col",
"""#!/bin/sh
CAM_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "aptina,mt9p006"
of_property -f -d ${ENDPOINT_PATH} link-frequencies
of_property -f -s ${ENDPOINT_PATH} input-clock-frequency <50000000>
of_property -f -s ${ENDPOINT_PATH} pixel-clock-frequency <50000000>
of_property -f -s ${ENDPOINT_PATH} pclk-sample <0>

of_fixup_status ${CAM_PATH}
""")
    env_add(d, "expansions/imx6ul-phytec-vm011-bw",
"""#!/bin/sh
CAM_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

of_property -f -s ${CAM_PATH} compatible "aptina,mt9p006m"
of_property -f -d ${ENDPOINT_PATH} link-frequencies
of_property -f -s ${ENDPOINT_PATH} input-clock-frequency <50000000>
of_property -f -s ${ENDPOINT_PATH} pixel-clock-frequency <50000000>
of_property -f -s ${ENDPOINT_PATH} pclk-sample <0>

of_fixup_status ${CAM_PATH}
""")
    env_add(d, "expansions/imx6ul-phytec-vm009",
"""#!/bin/sh
CAM_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48"
ENDPOINT_PATH="/soc/bus@2100000/i2c@21a0000/cam0@48/port/endpoint"

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
ubimkvol -t static /dev/nand0.root.ubi oftree0 1M
ubimkvol /dev/nand0.root.ubi root0 244M
ubimkvol -t static /dev/nand0.root.ubi kernel1 8M
ubimkvol -t static /dev/nand0.root.ubi oftree1 1M
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
    env_rm(d, "boot/system0")
    env_rm(d, "boot/system1")
    env_rm(d, "nv/bootchooser.targets")
    env_rm(d, "nv/bootchooser.system0.boot")
    env_rm(d, "nv/bootchooser.system1.boot")
    env_rm(d, "nv/bootchooser.state_prefix")
    env_rm(d, "bin/rauc_flash_nand_from_mmc")
    env_rm(d, "bin/rauc_flash_nand_from_tftp")

    #Default CMA size (128 MB) is too big for the 256 MB RAM so it has to be
    #reduced to 64 MB.
    env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
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

#use this expansion when peb-wlbt-05 adapter is connected
#. /env/expansions/imx6ul-phytec-peb-wlbt-05

#use this bootarg when the VM010 Color is connected
#nv linux.bootargs.mt9v022="mt9v022.sensor_type=color"
""")
}

python do_env_append_phyboard-segin-imx6ul-7() {
    env_rm(d, "bin/rauc_init_nand")
    env_rm(d, "bin/rauc_flash_nand_from_mmc")
    env_rm(d, "bin/rauc_flash_nand_from_tftp")
}

python do_env_append_phyboard-segin-imx6ul-8() {
    env_rm(d, "bin/rauc_init_nand")
    env_rm(d, "bin/rauc_flash_nand_from_mmc")
    env_rm(d, "bin/rauc_flash_nand_from_tftp")
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
COMPATIBLE_MACHINE .= ")$"
