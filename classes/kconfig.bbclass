inherit cml1
inherit kconfig-set

# these variables can be configured in the recipes inheriting kconfig
INTREE_DEFCONFIG ??= ""
PHYTEC_LOCALVERSION ??= ""
# This command is used when parsing a defconfig provided in the SRC_URI
# It will depend on the way you have created your defconfig
CONFIG_COMMAND ??= "olddefconfig"

# class variables
KBUILD_OUTPUT = "${B}"
KBUILD_OUTPUT[export] = "1"

# returns and .cfg filenames from SRC_URI
def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        base, ext = os.path.splitext(os.path.basename(s))
        if ext and ext in [".cfg"]:
            sources_list.append(os.path.join(d.getVar("WORKDIR"), (base + ext)))
    return sources_list

def get_absolut_defconfigs(d):
    import os.path
    defconfigs=d.getVar("INTREE_DEFCONFIG").split()
    arch = d.getVar('ARCH')
    absdefconfs=[]
    for defconfig in defconfigs:
        # Check if config comes from arch/$ARCH/configs/
        cfg = os.path.join(d.getVar("S"), "arch", arch, "configs", defconfig)
        if os.path.isfile(cfg):
            absdefconfs.append(cfg)

        # Check if config comes from kernel/configs/
        cfg = os.path.join(d.getVar("S"), "kernel/configs", defconfig)
        if os.path.isfile(cfg):
            absdefconfs.append(cfg)

    return absdefconfs

kconfig_do_configure() {
    # fixes extra + in /lib/modules/2.6.37+
    # $ scripts/setlocalversion . => +
    # $ make kernelversion => 2.6.37
    # $ make kernelrelease => 2.6.37+
    touch ${B}/.scmversion ${S}/.scmversion

    defconfig="${WORKDIR}/defconfig"
    config="${B}/.config"

    rm -rf $config
    set -e

    if test -f "$defconfig"; then
        bbnote "Using defconfig from SRC_URI"
        cp -f "$defconfig" "$config"
        oe_runmake -C ${S} ${CONFIG_COMMAND}
    elif [ ! -z "${INTREE_DEFCONFIG}" ]; then
        if [ 1 -eq ${@len(d.getVar("INTREE_DEFCONFIG").split())} ]; then
            bbnote "Using intree defconfig: ${INTREE_DEFCONFIG}"
            oe_runmake -C ${S} ${INTREE_DEFCONFIG}
        else
            bbnote "Merge intree defconfigs: ${INTREE_DEFCONFIG}"
            defcfgs="${@' '.join(get_absolut_defconfigs(d))}"
            ${S}/scripts/kconfig/merge_config.sh -m -O "${B}" $defcfgs
            oe_runmake -C ${S} ${CONFIG_COMMAND}
        fi
    else
        bbwarn "No defconfig provided. This will propably lead to errors."
    fi

    fragments="${@' '.join(find_cfgs(d))}"
    if test ! -z "$fragments"; then
        bbnote "combining kconfig fragments into .config"
        ${S}/scripts/kconfig/merge_config.sh -m -O "${B}" "$config" $fragments
    fi

    if [ ! -z "${PHYTEC_LOCALVERSION}" ]; then
        kconfig_set LOCALVERSION \"${PHYTEC_LOCALVERSION}\"
    fi

    # Disable auto version globally. There are some bad behaving recipes
    # which modify the source tree of the kernel at some point in time.
    # building several kernel modules leads to a change in the kernel version,
    # because of the race. This should be fixed per design in the ill behaving
    # recipes, so that yocto behaves more predictable. For now we can live
    # with this workaround.
    kconfig_set LOCALVERSION_AUTO n

    oe_runmake -C ${S} ${CONFIG_COMMAND}
}
EXPORT_FUNCTIONS do_configure
addtask configure after do_unpack do_patch before do_compile

# Python implementation of function oe_runmake_call and oe_runmake from
# meta/classes/base.bbclass.
def py_oe_runmake(d, cmd):
    import subprocess
    statement = "%s %s %s" % (d.getVar("MAKE") or '',
                d.getVar("EXTRA_OEMAKE") or '',
                cmd)
    bb.note(statement)
    exitcode = subprocess.call(statement, shell=True)
    if exitcode != 0:
        bb.fatal("oe_runmake failed")

# This is a python implementation, because the output of bbnote, bbplain, .. is
# not shown on the bitbake console, it's only saved to a log file. See Bug ????
# (... I cannot find the bug currently)
# The function provides the same workflow as for the task 'diffconfig'. The
# user can simply copy&paste the path and move the generated defconfig file
# into his/her layer.
python do_savedefconfig() {
    import shutil
    workdir = d.getVar("WORKDIR")
    workdir_defconfig = os.path.join(workdir, "defconfig.temp")
    B = d.getVar("B")

    bb.plain("Saving defconfig to %s" % (workdir_defconfig,))
    py_oe_runmake(d, "savedefconfig")

    shutil.copyfile(os.path.join(B, "defconfig"), workdir_defconfig)
}
addtask savedefconfig after do_configure
do_savedefconfig[dirs] = "${B}"
do_savedefconfig[nostamp] = "1"

# Define which command will run when calling bitbake -c menuconfig
# KCONFIG_CONFIG_COMMAND ?= "MENUCONFIG_COLOR=mono menuconfig"
KCONFIG_CONFIG_COMMAND ??= "menuconfig"
python do_menuconfig() {
    import shutil
    shutil.copy(".config", ".config.orig")

    # FIXME: We have to taint in that case as there is no way in
    # tracking changes in the tempdirectoy but outside of the sstate.
    # to fix this we should integrate menuconfig in the devtool
    # tainting is really not a viable solution as users dont want to
    # rebuild the package all the time after calling menuconfig
    pn = d.getVar("PN")
    bb.plain("You called menuconfig and tainted %s. If your change should be" % pn)
    bb.plain("applied in future, call:")
    bb.plain("    bitbake -c diffconfig %s" % pn)
    bb.plain("The generated .cfg file needs to be added to the SRC_URI in your layer.")
    bb.plain("To untaint the compile")
    bb.plain("    bitbake -c clean %s" % pn)
    bb.build.write_taint("do_compile", d)
    oe_terminal("/bin/sh -c \"TERM=\"xterm-256color\";make %s; if [ \$? -ne 0 ]; then echo 'Command failed.'; printf 'Press any key to continue... '; read r; fi\"" % d.getVar('KCONFIG_CONFIG_COMMAND'),
        pn + ' Configuration', d)
}
do_menuconfig[dirs] = "${B}"
