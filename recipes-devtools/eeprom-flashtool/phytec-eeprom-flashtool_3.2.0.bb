DESCRIPTION = "This tool is intended for reading from and writing to PHYTEC SOM EEPROM chips. \
Use of this tool requires a properly-formatted configuration file for each \
target PHYTEC platform (PCM-057.yml for PCM-057 boards, for example). \
By default, this tool looks for configuration files in a 'configs' subdirectory \
to where the script is currently located."
HOMEPAGE = "https://github.com/phytec/phytec-eeprom-flashtool"
SECTION = "devel"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=017e4848cc19ac0eb4e3f461b957234b"

SRC_URI = "https://github.com/phytec/${BPN}/archive/refs/tags/v${PV}.tar.gz"
SRC_URI[sha256sum] = "62151ca675e23f34b40ced74d5d7edbf87e80ad7a2487dcd9d903ad4f6362a13"

inherit setuptools3

RDEPENDS:${PN} = "python3-pyyaml python3-crc8"
