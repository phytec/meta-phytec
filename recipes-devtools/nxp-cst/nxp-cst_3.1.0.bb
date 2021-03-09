DESCRIPTION = "The link to the download.phytec.de server is only a \
        placeholder. Please download the NXP cst tool from \
        https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL \
        and store it as tar file in the yocto download folder"

LICENSE = "Proprietary"                                                          
LIC_FILES_CHKSUM = " \
    file://release/LICENSE.nxp;md5=6604ef69bd4ea2c604f8779985efd277 \
    file://release/LICENSE.openssl;md5=06698624268f7be8151210879bbcbcab \
"

require nxp-cst.inc

SRC_URI = " \
        https://download.phytec.de/cst-${PV}.tar;name=tarball \
"

SRC_URI[tarball.md5sum] = "89a2d6c05253c4de9a1bf9d5710bb7ae"
SRC_URI[tarball.sha256sum] = "a8cb42c99e9bacb216a5b5e3b339df20d4c5612955e0c353e20f1bb7466cf222"

INSANE_SKIP_${PN} += "already-stripped"
