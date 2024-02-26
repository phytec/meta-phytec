DESCRIPTION = "This tool is intended for reading from and writing to PHYTEC SOM EEPROM chips. \
Use of this tool requires a properly-formatted configuration file for each \
target PHYTEC platform (PCM-057.yml for PCM-057 boards, for example). \
By default, this tool looks for configuration files in a 'configs' subdirectory \
to where the script is currently located."
HOMEPAGE = "https://github.com/phytec/phytec-eeprom-flashtool"
SECTION = "devel"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=017e4848cc19ac0eb4e3f461b957234b"

SRC_URI = "git://github.com/phytec/${BPN}.git;protocol=https;branch=main"
SRCREV = "d67a9dc9300bb428bc454e3957ef50255475e278"

S="${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} = "python3-pyyaml python3-crc8"
