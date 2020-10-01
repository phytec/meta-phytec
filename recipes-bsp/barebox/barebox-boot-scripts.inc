inherit barebox-environment-2
inherit buildinfo
inherit phygittag

def env_add_boot_scripts(d, kernelname, mmcid, emmcid):
    import textwrap

    mmcboot = textwrap.dedent("""\
    #!/bin/sh

    detect mmc{mmcid}

    [ -e /env/config-expansions ] && /env/config-expansions

    global.bootm.appendroot=1
    global.bootm.root_dev=/dev/mmc{mmcid}.{rootid}
    global.bootm.image="/mnt/mmc{mmcid}.{partid}/{kernel}"
    global.bootm.oftree="/mnt/mmc{mmcid}.{partid}/oftree"
    global.linux.bootargs.dyn.root="rootflags='data=journal'"
    """)
    nandboot = textwrap.dedent("""\
    #!/bin/sh

    [ -e /env/config-expansions ] && /env/config-expansions

    [ ! -e /dev/nand0.root.ubi ] && ubiattach /dev/nand0.root

    global.bootm.image="/dev/nand0.root.ubi.kernel{partid}"
    global.bootm.oftree="/dev/nand0.root.ubi.oftree{partid}"
    global.linux.bootargs.dyn.root="root=ubi0:root{partid} ubi.mtd=root rootfstype=ubifs"
    """)
    netboot = textwrap.dedent("""\
    #!/bin/sh

    [ -e /env/config-expansions ] && /env/config-expansions

    path="/mnt/tftp"

    global.bootm.image="${path}/${global.user}-linux-${global.hostname}"

    oftree="${path}/${global.user}-oftree-${global.hostname}"
    if [ -f "${oftree}" ]; then
        global.bootm.oftree="$oftree"
    fi

    nfsroot="/nfsroot/${global.hostname}"
    ip_route_get -b ${global.net.server} global.linux.bootargs.dyn.ip
    global.linux.bootargs.dyn.root="root=/dev/nfs nfsroot=$nfsroot,vers=3,tcp"
    """)
    spiboot = textwrap.dedent("""\
    #!/bin/sh

    [ -e /env/config-expansions ] && /env/config-expansions

    global.bootm.image="/dev/m25p0.kernel"
    global.bootm.oftree="/dev/m25p0.oftree"
    global.linux.bootargs.dyn.root="{root}"
    """)

    if bb.utils.contains("MACHINE_FEATURES", "emmc", True, False, d):
        env_add(d, "boot/emmc", mmcboot.format(kernel=kernelname,
                                               mmcid=emmcid, partid=0,
                                               rootid=1))
        env_add(d, "boot/system0", mmcboot.format(kernel=kernelname,
                                                  mmcid=emmcid, partid=0,
                                                  rootid=1))
        env_add(d, "boot/system1", mmcboot.format(kernel=kernelname,
                                                  mmcid=emmcid, partid=2,
                                                  rootid=3))
        spiroot = "root=/dev/mmcblk{}p2 rootflags='data=journal'".format(emmcid)
    else:
        env_add(d, "boot/nand", nandboot.format(partid=""))
        env_add(d, "boot/system0", nandboot.format(partid=0))
        env_add(d, "boot/system1", nandboot.format(partid=1))
        spiroot = "root=ubi0:root ubi.mtd=root rootfstype=ubifs"

    env_add(d, "boot/mmc", mmcboot.format(kernel=kernelname,
                                          mmcid=mmcid, partid=0,
                                          rootid=1))
    env_add(d, "boot/net", netboot)
    env_add(d, "boot/spi", spiboot.format(root = spiroot))
