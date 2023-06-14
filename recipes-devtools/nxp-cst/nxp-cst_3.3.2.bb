DESCRIPTION = "The link to the download.phytec.de server is only a \
        placeholder. Please download the NXP cst tool from \
        https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL \
        and store it as tar file in the yocto download folder"

LICENSE = "Proprietary"

LIC_FILES_CHKSUM = " \
    file://LICENSE.bsd3;md5=1fbcd66ae51447aa94da10cbf6271530 \
    file://LICENSE.hidapi;md5=e0ea014f523f64f0adb13409055ee59e \
    file://LICENSE.openssl;md5=06698624268f7be8151210879bbcbcab \
"

require nxp-cst.inc

SRC_URI += " \
        https://download.phytec.de/dummyurl/cst-${PV}.tgz;name=tarball \
"

SRC_URI[tarball.md5sum] = "4b9fccac381fa412cba8ba7028c154c7"
SRC_URI[tarball.sha256sum] = "517b11dca181e8c438a6249f56f0a13a0eb251b30e690760be3bf6191ee06c68"

INSANE_SKIP:${PN} += "already-stripped"
