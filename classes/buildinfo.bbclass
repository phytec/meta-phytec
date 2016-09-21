# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>

# Documentation of BB Class buildinfo:
#
# The buildinfo class adds a new task do_buildinfo to the recipe. The task
# prints the necessary commands to clone and integrate an external repository
# with the exact same software version as in the package recipe. To see the
# output, the developer executes
#
#    $ bitbake <recipe> -c buildinfo
#
# A recipe that wants to use the buildinfo task has to provide some
# information. It has to define GIT_URL and GIT_TAG. One example
# configurations is
#
#     inherit buildinfo
#     require barebox.inc
#
#     FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/[...]"
#
#     GIT_TAG = "v2014.11.0-phy2"
#     GIT_URL = "git://git.phytec.de/${PN}"
#     SRC_URI = "${GIT_URL};branch=${BRANCH}"
#     SRC_URI_append = " \
#         file://commonenv \
#         file://environment \
#     "
#     SRCREV = "57b87aedbf0b6ae0eb0b858dd0c83411097c777a"
#     [...]
#
# and another example configuration, which uses the bbclass phygittag, is
#
#     inherit phygittag
#     inherit buildinfo
#     require barebox.inc
#
#     FILESEXTRAPATHS_prepend := "${THISDIR}/defconfigs:${THISDIR}/[...]"
#
#     GIT_URL = "git://git.phytec.de/${PN}"
#     SRC_URI = "${GIT_URL};branch=${BRANCH}"
#     SRC_URI_append = " \
#         file://commonenv \
#         file://environment \
#     "
#     SRCREV = "57b87aedbf0b6ae0eb0b858dd0c83411097c777a"
#     [...]
#
# If a recipe doesn't fetch the source code from a git repository and uses
# another method, e.g. a tar.gz, the buildinfo task can be used, too.  Only
# GIT_URL, GIT_TAG and SRCREV must be set.  An example recipe may looks like
#
#     inherit buildinfo
#     GIT_TAG = "v3.17.6-phy1"
#     GIT_URL = "git://git.phytec.de/linux-mainline"
#     SRCREV = "b866874890884ddf526b8225e706f46c5856c698"
#     SRC_URI = "ftp://ftp.phytec.de/<dir>/linux-mainline-3.17.6-phy1.tar.gz"
#     [...]
#
# The recipe author has to take care of the GIT_TAG and SRCREV variable. The
# revision in SRCREV must be the same as the revision behind the tag name in
# GIT_TAG. And both should match the version of the package in variable PV.
#
#
# Further assumptions for the recipe:
#
# We assume that the original package includes the variable SRCPV in the
# variable PV. Otherwise the package version is not unique when ${AUTOREV} is
# used in SRCREV. A recipe can do this by using the bbclass phygittag.
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
#
# Some notes:
#
# It's important that the developer sets up his own branch in the local
# repository, when using ${AUTOREV} in the SRCREV variable, because bitbake
# will fetch from a specific branch and not the current HEAD of the repository.
#
#
# Contents of the mini Howto:
#
# The developer clones the repository and creates a new branch. The branch name
# will be prefixed with the content of the GIT_TAG variable. The suffix is
# something like "-mystuff". This branch name is used in the local.conf to
# checkout the files to build the package,

python do_buildinfo() {
    # Check: Are variables defined in recipe?
    if d.getVar("GIT_URL", True) is None or d.getVar("GIT_TAG", True) is None or \
        d.getVar("SRCREV", True) is None:
        bb.fatal("Package recipe doesn't provide the variable GIT_URL, GIT_TAG and "
                 "SRCREV."
                 "Task needs both to compile the build info. Fix the recipe!")

    # Generate a meaningful branch name for developer's repository.
    branch_name = d.getVar("GIT_TAG", True) + "-local-development"

    # SRCREV which contains a tag name or a specific commit id is used to
    # checkout the git branch.
    checkout_rev = d.getVar("SRCREV", True)

    # Some more variables for buildinfo text.
    git_url = d.getVar("GIT_URL", True)
    pv = d.getVar("PV", True)
    pn = d.getVar("PN", True)

    bb.plain("""
(mini) HOWTO: Use a local git repository to build {PN}:

To get source code for this package and version ({PN}-{PV}), execute

$ mkdir -p ~/git
$ cd ~/git
$ git clone {GIT_URL} {PN}
$ cd ~/git/{PN}
$ git checkout -b {branchname} {checkoutrev}

You now have two possible workflows for your changes:

1. Work inside the git repository:
Copy and paste the following snippet to your "local.conf":

SRC_URI_pn-{PN} = "git:///${{HOME}}/git/{PN};branch=${{BRANCH}}\"
SRCREV_pn-{PN} = "${{AUTOREV}}"
BRANCH_pn-{PN} = \"{branchname}\"

After that you can recompile and deploy the package with

$ bitbake {PN} -c compile
$ bitbake {PN} -c deploy

Note: You have to commit all your changes. Otherwise yocto doesn't pick them up!

2. Work and compile from the local working directory
To work and compile in an external source directoy we provide the
externalsrc.bbclass. To use it copy and paste the following snippet to your
"local.conf":

INHERIT += "externalsrc"
EXTERNALSRC_pn-{PN} = "${{HOME}}/git/{PN}"
EXTERNALSRC_BUILD_pn-{PN} = "${{HOME}}/git/{PN}"

Note: All the compiling is done in the EXTERNALSRC directory. Everytime
you build an Image, the package will be recompiled and build.
""".format(PN=pn, PV=pv, GIT_URL=git_url, branchname=branch_name,
        checkoutrev=checkout_rev))
}
do_buildinfo[nostamp] = "1"
addtask buildinfo
