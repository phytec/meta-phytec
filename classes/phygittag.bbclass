# Copyright (C) 2015 PHYTEC Messtechnik GmbH,
# Author: Stefan Christ <s.christ@phytec.de>
#         Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

# The bbclass phygittag is a generic approach to fullfill our development
# needs. It allows a developer to overwrite the git url and to use ${AUTOREV}
# in his local.conf without interfering with the yocto/bitbake design
# principles. An example recipe is given below.
#
# The bbclass exports the following useful variables:
#
#    GIT_TAG     is the name of the tag in the git repository. It's generated
#                from the version in the recipe filename.
#                (The variable is also used in the bbclass buildinfo)
#    BRANCH      is the branch name in the git repository. It is constructed
#                from the version in the recipe filename.
#    PV          is a dynamic PV variable. If ${AUTOREV} is used in SRCREV,
#                it will include ${SRCPV}.
#
# The developer is only allowed to overwrite the variable BRANCH in his
# local.conf. GIT_TAG and PV must be set by this bbclass.
#
# An example barebox recipe which uses the bbclass phygittag and buildinfo
# follows here. The recipe filename is "barebox_2014.11.0-phy2.bb".
#
#    inherit phygittag
#    inherit buildinfo
#    require barebox.inc
#
#    FILESEXTRAPATHS:prepend := "${THISDIR}/defconfigs:${THISDIR}/defconfigs/features:${THISDIR}/env:"
#
#    # Variable GIT_URL is also used in buildinfo.
#    GIT_URL = "git://git.phytec.de/${BPN}"
#    SRC_URI = "${GIT_URL};branch=${BRANCH}"
#    SRC_URI:append = " \
#        file://commonenv \
#        file://environment \
#    "
#    S = "${WORKDIR}/git"
#
#    # NOTE: Keep version in filename in sync with commit id!
#    SRCREV = "57b87aedbf0b6ae0eb0b858dd0c83411097c777a"
#
#    COMPATIBLE_MACHINE  = "^("
#    COMPATIBLE_MACHINE .=  "phyflex-imx6-1"
#    COMPATIBLE_MACHINE .= "|phyflex-imx6-2"
#    COMPATIBLE_MACHINE .= "|phycard-imx6-1"
#    COMPATIBLE_MACHINE .= "|phyboard-alcor-imx6-1"
#    COMPATIBLE_MACHINE .= "|phyboard-subra-imx6-1"
#    COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-1"
#    COMPATIBLE_MACHINE .= "|phyboard-mira-imx6-2"
#    COMPATIBLE_MACHINE .= ")$"
#
# External assumptions and constraints:
#
#  1) The package maintainer must manually sync the version string in the
#     recipe filename with the commit id in SRCREV in the recipe.  For now the
#     commit id isn't automatically check against the tag name in the git
#     repository.
#
#  2) The git url have to be set with "SRC_URI =". All other source files in
#     the recipe and bbappends should be included with "SRC_URI:append =". This
#     allows a developer to overwrite the git url in his local.conf without
#     removing needed sources files in the variable SRC_URI. A correct example,
#     but not useful example:
#
#          SRC_URI = "git://git.phytec.de/${BPN};branch=${BRANCH}"
#          SRC_URI:append = " \
#              file://commonenv \
#              file://environment \
#          "


# Get the version string from the recipe filename and store it into a 'local'
# variable for further use in this bbclass.
# NOTE: The same handler is used in bitbake.conf for the real PV variable.
_PV_FILE = "${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[1] or '1.0'}"


# Construct the git tag name from the version string in the recipe filename.
# The variable GIT_TAG is used in buildinfo.
GIT_TAG = "v${_PV_FILE}"


# Here the real magic happends. If a commit id is used in SRCREV, variable PV
# will be set from the version string in the recipe filename. This is the
# default behaviour from bitbake.conf.
#
# If a developer sets
#
#      SRCREV:pn-barebox = "${AUTOREV}"
#
# in his local.conf, SRCREV will be the string "AUTOINC". Later the string
# AUTOINC is replaced with the real commit id when bitbake fetches the source
# code. (See function srcrev_internal_helper() in
# bitbake/lib/bb/fetch2/__init__.py) So if AUTOREV is used by the developer, we
# will include the variable ${SRCPV} in the PV variable to have a unique
# version string for the package.
#
# It's important that we only append a string to the PV's value. The prefix
# must be the same, because the bitbake package selection mechanism has to
# select the same recipe whether or not ${AUTOREV} is used in the variable
# SRCREV.
PV = "${@oe.utils.conditional("SRCREV", "AUTOINC", "${_PV_FILE}+git${SRCPV}", "${_PV_FILE}", d)}"


# Extract the phytec branch from version string in filename:
# The package version is for example a phytec version "2014.11.0-phy1" and
# "4.1.18-phy4". Or a upstream version  like "2014.11.0" or "4.1.18" if the
# recipe is based on a upstream version without local changes.
#
# The function converts the version from the filename to a phytec integration
# branch. Names look like "v2014.11.0-phy" or "v4.1.18-phy".
def git_tag_to_integration_branch(_pv_file):
    if "-phy" in _pv_file:
        # Is a phytec version, based on a upstream tag. Remove numbers and
        # add "v" for tag name.
        return "v" + _pv_file.rstrip("0123456789")
    else:
        # Is a original upstream version like "2014.11.0" or "4.1.18".
        return "v" +  _pv_file + "-phy"
BRANCH = "${@git_tag_to_integration_branch("${_PV_FILE}")}"
BRANCH:phynext = "${@git_tag_to_integration_branch("${_PV_FILE}")}next"
SRCREV:phynext = "${AUTOREV}"
