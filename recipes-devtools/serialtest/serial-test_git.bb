DESCRIPTION = "Serial test application"
HOMEPAGE = "https://github.com/cbrake/linux-serial-test"
SECTION = "utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT;md5=544799d0b492f119fa04641d1b8868ed"
SRCREV = "3b52d0c1ba4633ced69b499c0d3032f3d85f1f8d"

SRC_URI = "git://github.com/cbrake/linux-serial-test.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit cmake
