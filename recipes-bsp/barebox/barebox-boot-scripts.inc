inherit barebox-environment-2
inherit buildinfo
inherit phygittag

def env_add_boot_scripts(d, kernelname, mmcid, emmcid):
    import textwrap

    mmcboot = textwrap.dedent("""\
    #!/bin/sh

    detect mmc{mmcid}

    global.bootm.image="/mnt/mmc{mmcid}.{partid}/{kernel}"
    global.bootm.oftree="/mnt/mmc{mmcid}.{partid}/oftree"
    global.linux.bootargs.dyn.root="root=/dev/mmcblk{mmcid}p{rootid} rootflags='data=journal'"

    [ -e /env/config-expansions ] && /env/config-expansions

    if [ -e /env/expansions/dt-overlays ] ; then
        global overlays.path="/mnt/mmc{mmcid}.{bbrootid}/overlays"
        /env/expansions/dt-overlays
    fi
    """)
    nandboot = textwrap.dedent("""\
    #!/bin/sh

    [ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root

    global.bootm.image="/dev/nand0.root.ubi.kernel{partid}"
    global.bootm.oftree="/dev/nand0.root.ubi.oftree{partid}"
    global.linux.bootargs.dyn.root="root=ubi0:root{partid} ubi.mtd=root rootfstype=ubifs"

    [ -e /env/config-expansions ] && /env/config-expansions

    if [ -e /env/expansions/dt-overlays ] ; then
        mkdir /mnt/rootfs
        mount -t ubifs /dev/nand0.root.ubi.root{partid} /mnt/rootfs
        global overlays.path="/mnt/rootfs/overlays"
        /env/expansions/dt-overlays
    fi
    """)
    netboot = textwrap.dedent("""\
    #!/bin/sh

    path="/mnt/tftp"

    global.bootm.image="${path}/${global.user}-linux-${global.hostname}"

    oftree="${path}/${global.user}-oftree-${global.hostname}"
    if [ -f "${oftree}" ]; then
        global.bootm.oftree="$oftree"
    fi

    nfsroot="/home/${global.user}/nfsroot/${global.hostname}"
    ip_route_get -b ${global.net.server} global.linux.bootargs.dyn.ip
    global.linux.bootargs.dyn.root="root=/dev/nfs nfsroot=$nfsroot,vers=3,tcp"

    [ -e /env/config-expansions ] && /env/config-expansions

    if [ -e /env/expansions/dt-overlays ] ; then
        global overlays.path="/mnt/nfs/overlays"
        /env/expansions/dt-overlays
    fi
    """)
    spiboot = textwrap.dedent("""\
    #!/bin/sh

    global.bootm.image="/dev/m25p0.kernel"
    global.bootm.oftree="/dev/m25p0.oftree"
    global.linux.bootargs.dyn.root="{root}"

    [ -e /env/config-expansions ] && /env/config-expansions
    """)

    if bb.utils.contains("MACHINE_FEATURES", "emmc", True, False, d):
        env_add(d, "boot/emmc", mmcboot.format(kernel=kernelname,
                                               mmcid=emmcid, partid=0,
                                               rootid=2, bbrootid=1))
        env_add(d, "boot/system0", mmcboot.format(kernel=kernelname,
                                                  mmcid=emmcid, partid=0,
                                                  rootid=5, bbrootid=4))
        env_add(d, "boot/system1", mmcboot.format(kernel=kernelname,
                                                  mmcid=emmcid, partid=1,
                                                  rootid=6, bbrootid=5))
        spiroot = "root=/dev/mmcblk{}p2 rootflags='data=journal'".format(emmcid)
    else:
        env_add(d, "boot/nand", nandboot.format(partid=""))
        env_add(d, "boot/system0", nandboot.format(partid=0))
        env_add(d, "boot/system1", nandboot.format(partid=1))
        spiroot = "root=ubi0:root ubi.mtd=root rootfstype=ubifs"

    env_add(d, "boot/mmc", mmcboot.format(kernel=kernelname, mmcid=mmcid,
                                          partid=0, rootid=2, bbrootid=1))
    env_add(d, "boot/net", netboot)
    env_add(d, "boot/spi", spiboot.format(root=spiroot))

def env_add_rauc_nand_boot_scripts(d, nandflashsize=1024, kernelsize=16, dtbsize=1):
    import textwrap

    rauc_init_nand = textwrap.dedent("""\
    #!/bin/sh
    ROOTFSSIZE={rootfssize}M
    KERNELSIZE={kernelsize}M
    DTBSIZE={dtbsize}M
    echo "WARNING! This script is deprecated. Please use "rauc-flash-nand" in a Linux environment!"
    echo "Formatting /dev/nand0.root"

    ubiformat -q /dev/nand0.root
    ubiattach /dev/nand0.root
    ubimkvol -t static /dev/nand0.root.ubi kernel0 ${{KERNELSIZE}}
    ubimkvol -t static /dev/nand0.root.ubi oftree0 ${{DTBSIZE}}
    ubimkvol /dev/nand0.root.ubi root0 ${{ROOTFSSIZE}}
    ubimkvol -t static /dev/nand0.root.ubi kernel1 ${{KERNELSIZE}}
    ubimkvol -t static /dev/nand0.root.ubi oftree1 ${{DTBSIZE}}
    ubimkvol /dev/nand0.root.ubi root1 ${{ROOTFSSIZE}}

    ubidetach 0
    """)
    rauc_flash_nand_from_mmc = textwrap.dedent("""\
    #!/bin/sh
    echo "WARNING! This script is deprecated. Please use "rauc-flash-nand" in a Linux environment!"
    echo "Initializing NAND flash from MMC"
    [ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root
    ubiupdatevol /dev/nand0.root.ubi.kernel0 /mnt/mmc0.0/zImage
    ubiupdatevol /dev/nand0.root.ubi.kernel1 /mnt/mmc0.0/zImage
    ubiupdatevol /dev/nand0.root.ubi.oftree0 /mnt/mmc0.0/oftree
    ubiupdatevol /dev/nand0.root.ubi.oftree1 /mnt/mmc0.0/oftree
    cp /mnt/mmc0.0/root.ubifs /dev/nand0.root.ubi.root0
    cp /mnt/mmc0.0/root.ubifs /dev/nand0.root.ubi.root1
    """)
    rauc_flash_nand_from_tftp = textwrap.dedent("""\
    #!/bin/sh
    echo "WARNING! This script is deprecated. Please use "rauc-flash-nand" in a Linux environment!"
    echo "Initializing NAND flash from TFTP"
    [ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root
    ubiupdatevol /dev/nand0.root.ubi.kernel0 /mnt/tftp/zImage
    ubiupdatevol /dev/nand0.root.ubi.kernel1 /mnt/tftp/zImage
    ubiupdatevol /dev/nand0.root.ubi.oftree0 /mnt/tftp/oftree
    ubiupdatevol /dev/nand0.root.ubi.oftree1 /mnt/tftp/oftree
    cp /mnt/tftp/root.ubifs /dev/nand0.root.ubi.root0
    cp /mnt/tftp/root.ubifs /dev/nand0.root.ubi.root1
    """)
    env_add(d, "bin/rauc_init_nand", rauc_init_nand.format(
            rootfssize=nandflashsize // 2 - kernelsize - dtbsize - 16,
            kernelsize=kernelsize, dtbsize=dtbsize))
    env_add(d, "bin/rauc_flash_nand_from_mmc", rauc_flash_nand_from_mmc)
    env_add(d, "bin/rauc_flash_nand_from_tftp", rauc_flash_nand_from_tftp)

def env_rm_rauc_nand_boot_scripts(d):
    env_rm(d, "bin/rauc_init_nand")
    env_rm(d, "bin/rauc_flash_nand_from_mmc")
    env_rm(d, "bin/rauc_flash_nand_from_tftp")

def env_add_bootchooser(d):
    env_add(d, "nv/bootchooser.targets", """system0 system1""")
    env_add(d, "nv/bootchooser.system0.boot", """system0""")
    env_add(d, "nv/bootchooser.system1.boot", """system1""")
    env_add(d, "nv/bootchooser.state_prefix", """state.bootstate""")
    if "rauc" in d.getVar("DISTRO_FEATURES").split():
        bb.note("Setting default boot target to \"bootchooser\"")
        env_add(d, "nv/boot.default", "bootchooser")

def env_rm_bootchooser(d):
    env_rm(d, "nv/bootchooser.targets")
    env_rm(d, "nv/bootchooser.system0.boot")
    env_rm(d, "nv/bootchooser.system1.boot")
    env_rm(d, "nv/bootchooser.state_prefix")
    if "rauc" in d.getVar("DISTRO_FEATURES").split():
        env_rm(d, "nv/boot.default")
