# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>
#
# phyexternalsrc.bbclass faciliates development of linux kernel and
# barebox with Yocto.
#
# This is a fork of the externalsrc.bbclass of poky. It behaves quite
# differently than the poky class. It will execute all tasks, but filters
# all remote sources from the SRC_URI. Any patches found locally will
# still apply.
# To use this class, you have to check out the sourcecode of the kernel
# or barebox manually, see the buildinfo class for more details. After
# having the source code in a local directory, add the following lines
# to your local.conf, e.g. for the linux-ti kernel:
#
# INHERIT += "phyexternalsrc"
# EXTERNALSRC_pn-linux-ti = "${HOME}/yocto/sources/linux-ti"
# PV_pn-linux-ti = "3.12.24-phy1-externalmod"
#
# The last line sets the version of the package manually, which is
# advisable, as any kind of version auto detection will fail.
#
# Note:
# S and B are the same per default, so if you want to use this class for
# userspace multilib packages, you need to set EXTERNALSRC_BUILD to
# something under $WORKDIR,  e.g. the default from poky:
#
# EXTERNALSRC_BUILD_pn-linux-ti = "${WORKDIR}/${BPN}-${PV}/"

python () {
    import re
    externalsrc = d.getVar('EXTERNALSRC', True)
    if externalsrc:
        d.setVar('S', externalsrc)

        # We don't separate B and S per default
        externalsrcbuild = d.getVar('EXTERNALSRC_BUILD', True)
        if externalsrcbuild:
            d.setVar('B', externalsrcbuild)
        else:
            d.setVar('B', externalsrc)

        # Filter all remote access URIs
        filtered_SRC_URI = ''
        for uri in d.getVar('SRC_URI', True).split():
            m = re.compile('(?P<type>[^:]*)').match(uri)
            if not m:
                    raise MalformedUrl(uri)
            elif m.group('type') in ('http', 'https', 'ftp', 'cvs', 'svn', 'git', 'ssh'):
                    pass
            elif m.group('type') in ('file'):
                filtered_SRC_URI += uri + ' '
        d.setVar('SRC_URI', filtered_SRC_URI.strip())

        # Ensure compilation happens every time
        d.setVarFlag('do_compile', 'nostamp', '1')

        # sstate is never going to work for external source trees, disable it
        tasks = filter(lambda k: d.getVarFlag(k, "task"), d.keys())
        for task in tasks:
            if task.endswith("_setscene"):
                bb.build.deltask(task, d)
}
