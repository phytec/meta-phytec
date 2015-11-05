# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>
# Released under the MIT license (see COPYING.MIT for the terms)


# Python implementation of function oe_runmake_call and oe_runmake from
# meta/classes/base.bbclass.
def py_oe_runmake(d, cmd):
    import subprocess
    statement = "%s %s %s" % (d.getVar("MAKE", True) or '',
                d.getVar("EXTRA_OEMAKE", True) or '',
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
    workdir = d.getVar("WORKDIR", True)
    workdir_defconfig = os.path.join(workdir, "defconfig.temp")
    B = d.getVar("B", True)

    bb.plain("Saving defconfig to %s" % (workdir_defconfig,))
    py_oe_runmake(d, "savedefconfig")

    shutil.copyfile(os.path.join(B, "defconfig"), workdir_defconfig)

}
addtask savedefconfig after do_configure
do_savedefconfig[nostamp] = "1"
