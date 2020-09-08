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
SRCREV = "ca15efa9b9ac98d59da0aaeeb812ea12f353cd3b"
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
    kernelname = d.getVar("KERNEL_IMAGETYPE", True)
    if "secureboot" in d.getVar("DISTRO_FEATURES", True):
        kernelname = "fitImage.fitimg"
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

    env_add(d, "expansions/imx6qdl-mira-enable-lvds",
"""of_fixup_status /ldb/lvds-channel@0
of_fixup_status /soc/aips-bus@2100000/i2c@21a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-nunki-enable-lvds",
"""of_fixup_status /ldb/lvds-channel@0
of_fixup_status /soc/aips-bus@2100000/i2c@21a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-mira-peb-eval-01",
"""of_fixup_status /gpio-keys
of_fixup_status /user-leds
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd",
"""#!/bin/sh
of_fixup_status /panel-lcd
of_fixup_status /ldb/lvds-channel@0
of_fixup_status /soc/aips-bus@2100000/i2c@21a4000/polytouch@38
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-res",
"""#!/bin/sh
of_fixup_status /panel-lcd
of_fixup_status /ldb/lvds-channel@0
of_fixup_status /soc/aips-bus@2100000/i2c@21a4000/touchctrl@41
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-018-peb-av-02",
"""of_fixup_status /panel-lcd
of_fixup_status /display@di0
of_fixup_status /soc/aips-bus@2100000/i2c@21a0000/polytouch@38
""")
    env_add(d, "expansions/imx6qdl-phytec-lcd-018-peb-av-02-res",
"""of_fixup_status /panel-lcd
of_fixup_status /display@di0
of_fixup_status /soc/aips-bus@2100000/i2c@21a0000/touchctrl@44
""")
    env_add(d, "expansions/imx6qdl-phytec-peb-wlbt-01",
"""#!/bin/sh
of_fixup_status /soc/aips-bus@2100000/usdhc@2198000
of_fixup_status /regulator-wlan-en
""")
    env_add(d, "nv/dev.eth0.mode", "static")
    env_add(d, "nv/dev.eth0.ipaddr", "192.168.3.11")
    env_add(d, "nv/dev.eth0.netmask", "255.255.255.0")
    env_add(d, "nv/net.gateway", "192.168.3.10")
    env_add(d, "nv/dev.eth0.serverip", "192.168.3.10")
    env_add(d, "nv/dev.eth0.linux.devname", "eth0")
    env_add(d, "nv/dhcp.vendor_id", "barebox-{}".format(dhcp_vendor))
}

python do_env_append_phyflex-imx6() {
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


#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-P VM-011-COL

#Enable VM-XXX on CSI1
#of_camera_selection -a 0x48 -p 1 -b phyCAM-P VM-011-COL
""")
    # Enable 32 bit color depth for framebuffer emulation on phyFLEX-CarrierBoard
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env_append_phyboard-mira-imx6() {
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
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"

#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
    # Enable 32 bit color depth for framebuffer emulation on phyBOARD-Mira
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env_append_phyboard-nunki-imx6() {
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

#Enable VM-XXX on CSI0
of_camera_selection -a 0x48 -p 0 -b phyCAM-P VM-010-BW

#Enable VM-XXX on CSI1
#of_camera_selection -a 0x48 -p 1 -b phyCAM-P VM-011-COL
""")
    # Enable 32 bit color depth for framebuffer emulation on phyBOARD-Nunki
    env_add(d, "nv/linux.bootargs.fb", "imxdrm.legacyfb_depth=32\n");
}

python do_env_append_phyflex-imx6-4() {
    env_add(d, "nv/linux.bootargs.cma", "cma=256M\n")
}

python do_env_append_phyboard-mira-imx6-4() {
    env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
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

#Enable VM-XXX on CSI0
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
of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"

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
#of_property -s -f "/panel-lcd" compatible "edt,etm0700g0edh6"

# imx6qdl-phytec-lcd: 7" display (AC104)
of_property -s -f "/panel-lcd" compatible "edt,etm0700g0dh6"

# imx6qdl-phytec-lcd: 5.7" display (AC103)
#of_property -s -f "/panel-lcd" compatible "edt,etmv570g2dhu"

# imx6qdl-phytec-lcd: 4.3" display (AC102)
#of_property -s -f "/panel-lcd" compatible "edt,etm0430g0dh6"

# imx6qdl-phytec-lcd: 3.5" display (AC167 / AC101)
#of_property -s -f "/panel-lcd" compatible "edt,etm0350g0dh6"

#Enable VM-XXX on CSI0
#of_camera_selection -a 0x48 -p 0 -b phyCAM-S+ VM-010-BW
""")
}

python do_env_append_phyboard-mira-imx6-15() {
    env_add(d, "nv/linux.bootargs.cma", "cma=64M\n")
}

#Enviroment changes for RAUC
python do_env_append_phyboard-mira-imx6-3() {
    env_add(d, "nv/bootchooser.state_prefix", """state""")
    env_add(d, "nv/bootchooser.targets", """system0 system1""")
    env_add(d, "nv/bootchooser.system0.boot", """system0""")
    env_add(d, "nv/bootchooser.system1.boot", """system1""")
    env_add(d, "nv/bootchooser.state_prefix", """state.bootstate""")
    env_add(d, "bin/rauc_init_nand",
"""#!/bin/sh
#For 1GB NANDs (otherwise other partition sizes)
ROOTFSSIZE=450M
echo "Format /dev/nand0.root"

ubiformat -q /dev/nand0.root
ubiattach /dev/nand0.root
ubimkvol -t static /dev/nand0.root.ubi kernel0 16M
ubimkvol -t static /dev/nand0.root.ubi oftree0 1M
ubimkvol /dev/nand0.root.ubi root0 ${ROOTFSSIZE}
ubimkvol -t static /dev/nand0.root.ubi kernel1 16M
ubimkvol -t static /dev/nand0.root.ubi oftree1 1M
ubimkvol /dev/nand0.root.ubi root1 ${ROOTFSSIZE}

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
