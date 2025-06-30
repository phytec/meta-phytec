inherit phygittag
inherit buildinfo
require barebox.inc
inherit barebox-environment-2
include barebox-boot-scripts.inc

GIT_URL = "git://git.phytec.de/barebox"
SRC_URI = "${GIT_URL};branch=${BRANCH}"


PR = "${INC_PR}.0"

# NOTE: Keep version in filename in sync with commit id and barebox-ipl!
SRCREV = "c0855b884a1078c448120cb2fcf01147971a625b"

python do_env:append() {
    env_add(d, "nv/allow_color", "false\n")
    env_add(d, "nv/linux.bootargs.base", "consoleblank=0\n")
    env_add(d, "nv/linux.bootargs.console", "console=ttyS0,115200n8\n")
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

    env_add(d, "expansions/dt-overlays",
"""#!/bin/sh

path="$global.overlays.path"

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
    kernelname = d.getVar("KERNEL_IMAGETYPE")
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
}

python do_env:append:phyboard-wega-am335x-1() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-wega-peb-av-01.dtbo")
}
python do_env:append:phyboard-wega-am335x-2() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-wega-peb-av-02.dtbo")
}
python do_env:append:phyboard-wega-am335x-3() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-wega-peb-av-01.dtbo am335x-spi-nor.dtbo")
}
python do_env:append:phyboard-wega-r2-am335x-1() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-wega-peb-av-01.dtbo")
}
python do_env:append:phycore-am335x-1() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-am335x-2() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-am335x-3() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-no-rtc.dtbo am335x-no-spi-nor.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-am335x-4() {
    env_add(d, "nv/overlays.select", "am335x-no-eeprom.dtbo am335x-no-rtc.dtbo am335x-no-spi-nor.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-am335x-5() {
    env_add(d, "nv/overlays.select", "am335x-no-spi-nor.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-r2-am335x-1() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-i2c-temp.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-r2-am335x-2() {
    env_add(d, "nv/overlays.select", "am335x-no-rtc.dtbo am335x-no-spi-nor.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-r2-am335x-3() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-r2-am335x-4() {
    env_add(d, "nv/overlays.select", "am335x-no-spi-nor.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-r2-am335x-5() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-r2-am335x-6() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-no-spi-nor.dtbo am335x-pcm953-lcd.dtbo")
}
python do_env:append:phycore-emmc-am335x-1() {
    env_add(d, "nv/overlays.select", "am33xx-gpu.dtbo am335x-pcm953-lcd.dtbo")
}

INTREE_DEFCONFIG = "omap_defconfig"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "phyboard-regor-am335x-1"
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
COMPATIBLE_MACHINE .= "|phycore-emmc-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-1"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-2"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-3"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-4"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-5"
COMPATIBLE_MACHINE .= "|phycore-r2-am335x-6"
COMPATIBLE_MACHINE .= ")$"
