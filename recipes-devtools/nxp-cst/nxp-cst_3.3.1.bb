DESCRIPTION = "The link to the download.phytec.de server is only a \
        placeholder. Please download the NXP cst tool from \
        https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL \
        and store it as tar file in the yocto download folder"

LICENSE = "Proprietary"

NXP_CST_BASE_PATH ?= "cst-3.3.1"

LIC_FILES_CHKSUM = " \
    file://${NXP_CST_BASE_PATH}/LICENSE.bsd3;md5=1fbcd66ae51447aa94da10cbf6271530 \
    file://${NXP_CST_BASE_PATH}/LICENSE.hidapi;md5=e0ea014f523f64f0adb13409055ee59e \
    file://${NXP_CST_BASE_PATH}/LICENSE.openssl;md5=06698624268f7be8151210879bbcbcab \
"

require nxp-cst.inc

SRC_URI = " \
        https://download.phytec.de/cst-${PV}.tar;name=tarball \
"

SRC_URI[tarball.md5sum] = "830965fe59a0e9c505fd18c7d2e60dbd"
SRC_URI[tarball.sha256sum] = "8cf41fb146298a9caa14e3db9aec1c7e0f0279ddc51ee3f7d76770287032e7a3"

INSANE_SKIP_${PN} += "already-stripped"
