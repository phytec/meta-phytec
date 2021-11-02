DESCRIPTION = "Serial test application"
HOMEPAGE = "https://github.com/cbrake/linux-serial-test"
SECTION = "utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=544799d0b492f119fa04641d1b8868ed"
SRCREV = "aed2a6e78160b63295368d70dbdbc19fe3a38225"

SRC_URI = "git://github.com/cbrake/linux-serial-test.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit cmake
