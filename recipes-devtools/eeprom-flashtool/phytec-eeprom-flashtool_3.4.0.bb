DESCRIPTION = "This tool is intended for reading from and writing to PHYTEC SOM EEPROM chips. \
Use of this tool requires a properly-formatted configuration file for each \
target PHYTEC platform (PCM-057.yml for PCM-057 boards, for example). \
By default, this tool looks for configuration files in a 'configs' subdirectory \
to where the script is currently located."
HOMEPAGE = "https://github.com/phytec/phytec-eeprom-flashtool"
SECTION = "devel"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT.txt;md5=3912d958d00bac4a6b550f75d7c806bb"

SRC_URI = "git://github.com/phytec/${BPN}.git;protocol=https;branch=main"
SRCREV = "365788dcce9c060bcd4d3390ffa9021cfaa7d609"

S = "${WORKDIR}/git"

inherit python_setuptools_build_meta

DEPENDS += "python3-pip-native python3-build-native python3-wheel-native"

FILES:${PN} += "${libdir}/python*/site-packages/*"

RDEPENDS:${PN} = "python3-pyyaml python3-crc8"
