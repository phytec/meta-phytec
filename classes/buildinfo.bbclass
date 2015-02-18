# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

# Documentation of BB Class buildinfo:
#
# The buildinfo class adds a new task do_buildinfo to the recipe. The task
# prints the necessary commands to clone and integrate an external repository
# with the exact same software version as in the package recipe. To see the
# output, the developer executes
#
#    $ bitbake <package> -c buildinfo
#
# A recipe which wants to use the buildinfo task has to provide some
# information. It either has to define GIT_URL and BRANCH or GIT_URL and TAG.
# Two example configurations are
#
#     inherit buildinfo
#     TAG = "v3.17.6-phy1"
#     GIT_URL = "git://git.phytec.de/${PN}"
#     SRCREV = "b866874890884ddf526b8225e706f46c5856c698"
#     SRC_URI = "${GIT_URL};nobranch=1"
#
# and
#
#     inherit buildinfo
#     BRANCH = "v3.17.6-phy"
#     GIT_URL = "git://git.phytec.de/${PN}"
#     SRCREV = "${AUTOREV}"
#     SRC_URI = "${GIT_URL};branch=${BRANCH}"
#     PV = "${@d.getVar('BRANCH').lstrip('v')}-git${SRCPV}"
#
# If a recipe doesn't fetch the source code from a git repository and uses
# another method, e.g. a tar.gz, the buildinfo task can be used, too.  Only
# GIT_URL, BRANCH and/or TAG must be set.  An example recipe may looks like
#
#     inherit buildinfo
#     TAG = "v3.17.6-phy1"
#     GIT_URL = "git://git.phytec.de/linux-mainline"
#     SRCREV = "b866874890884ddf526b8225e706f46c5856c698"
#     SRC_URI = "ftp://ftp.phytec.de/<dir>/linux-mainline-${PV}.tar.gz"
#
# The recipe author has to take care of the TAG and SRCREV variable. The
# revision in SRCREV must be the same as the revision behind the tag name in
# TAG. And both should match the version of the package in varibale PV.
#
#
# Further assumptions for the recipe:
#
# The example local.conf configuration, which is printed in the buildinfo
# task, assumes that the SRC_URI in the package recipe only contains the
# repository url, because it's overwritten with a local repo url.  All other
# resources which are needed in the build have to be in _append or _prepend
# variables. This is wrong:
#
#     SRC_URI = "git://git@git.phytec.de/barebox;protocol=ssh;branch=${BRANCH}"
#     SRC_URI += "file://no-default-env-compiled-in.cfg"
#
# But this is ok:
#
#     SRC_URI = "git://git@git.phytec.de/barebox;protocol=ssh;branch=${BRANCH}"
#     SRC_URI_append = " file://no-default-env-compiled-in.cfg"
#
# Some notes:
#
# TODO: If a developer changes the variables BRANCH or TAG in his local.conf,
# the buildinfo task's output maybe wrong and inconsistent. E.g. after the adds
# the purposed local.conf modifications below, the output of the task may
# change.
#
# It's important that the developer sets up his own branch in the local
# repository, when using AUTOREV in the SRCREV variable, because bitbake will
# fetch from a specific branch and not the current HEAD of the repository. E.g.
# bitbake will never fetch contents from a 'detached HEAD' state.
#
# Contents of the mini Howto:
#
# The developer clones the repository and creates a new branch. The branch name
# will be prefixed with the content of the BRANCH or TAG variable. The suffix
# is something like "-mystuff". This branch name is used in the local.conf to
# checkout the files to build the package,

python do_buildinfo() {
    # Check: Are variables defined in recipe?
    if (d.getVar("GIT_URL", True) is None or (d.getVar("BRANCH", True) is None
        and d.getVar("TAG", True) is None)):
        bb.fatal("Package recipe doesn't provide the variable GIT_URL, "
                 "TAG and/or BRANCH. "
                 "Task needs both to compile the build info. Fix the recipe!")

    # If both Variables TAG and BRANCH are definied in the recipe, we assume
    # that the TAG should be used. NOTE Later we may warn, if both variables
    # exists to avoid some misconfigurations.

    # Generate a meaningfull branch name for developers repository.
    if d.getVar("TAG", True) is not None:
        local_branch_name = "${TAG}-local_development"
    else:
        local_branch_name = "${BRANCH}-local_development"

    # Ugly: For the linux-mainline recipe, the user has to add a fix in his
    # file conf/local.conf.
    if "${PN}" == "linux-mainline":
        pv_value = "\x24{@d.getVar('BRANCH').lstrip('v')}+git\x24{SRCPV}"
    else:
        pv_value = "\x24{BRANCH}+git\x24{SRCPV}"

    # NOTE: If AUTOREV is used in variable SRCREV, it's not replaced with the
    # current HEAD of the branch at parsing time. It's content is the simple
    # string 'AUTOINC'.  So SRCREV cannot be use to get the actual revision
    # which is checkouted from the repository.
    # If BRANCH is used in the package recipe, the BRANCH value itself is used.
    # If TAG is used, the SRCREV value is used which contains a tag name or a
    # specific git revision.
    if d.getVar("TAG", True) is not None:
        checkout_rev = "${SRCREV}"
    else:
        checkout_rev = "${BRANCH}"

    # NOTE: bitbake will replace all ocurrency of the string "${BRANCH}" in
    # this file with the value of the variable BRANCH in the bitbake recipe. If
    # you want to include the text "${BRANCH}" in a string, you have to escape
    # it.  E.g. with "\x24{BRANCH}".
    # NOTE: In the following code some variables should be replaced with the
    # actual value at parsing time of the recipe and other should be included
    # as plain text.
    bb.plain("""
(mini) HOWTO: Use a local git repository to build ${PN}:

To get source code for this package and version (${PN}-${PV}), execute

$ mkdir -p ~/git
$ cd ~/git
$ git clone ${GIT_URL} ${PN}
$ cd ~/git/${PN}
$ git checkout -b %s %s
""" % (local_branch_name, checkout_rev))

    bb.plain("""You now have two possible workflows for your changes.

1. Work inside the git repository:
Copy and paste the following snippet to your 'local.conf':

BRANCH_pn-${PN} = \"%s\"
SRC_URI_pn-${PN} = "git:///\x24{HOME}/git/${PN};branch=\x24{BRANCH}\"
SRCREV_pn-${PN} = "\x24{AUTOREV}"
PV_pn-${PN} = "%s"

After that you can recompile and deploy the package with

$ bitbake ${PN} -c compile
$ bitbake ${PN} -c deploy

Note: You have to commit all your changes. Otherwise yocto doesn't pick them up!

2. Work and compile from the local working directory
To work and compile in an external source directoy we provide the
phyexternalsrc.bbclass. To use it copy and paste the following snippet to your
"local.conf":

INHERIT += "phyexternalsrc"
EXTERNALSRC_pn-${PN} = "\x24{HOME}/git/${PN}"
PV_pn-${PN} = "%s"

Note: All the compiling is done in the EXTERNALSRC directory. Everytime
you build an Image, the package will be recompiled and build.
""" % (local_branch_name, pv_value, local_branch_name))
}
do_buildinfo[nostamp] = "1"
addtask buildinfo
