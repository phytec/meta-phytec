DESCRIPTION = "This tool is intended for reading from and writing to PHYTEC SOM EEPROM chips. \
Use of this tool requires a properly-formatted configuration file for each \
target PHYTEC platform (PCM-057.yml for PCM-057 boards, for example). \
By default, this tool looks for configuration files in a 'configs' subdirectory \
to where the script is currently located."
HOMEPAGE = "https://github.com/phytec/phytec-eeprom-flashtool"
SECTION = "devel"

S="${WORKDIR}/git"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=1117940313875d7598ccbb05f20129a7"

SRC_URI = "git://github.com/phytec/phytec-eeprom-flashtool.git;branch=master;protocol=git"
SRCREV = "d9528c092921cb04a11c2b25bbf3f56d948d6e40"

SRC_URI[md5sum] = "0e322bb28e1645430b79afb16954bb22"
SRC_URI[sha256sum] = "3c583060f7f755a0262debfa27ec2ad6720e8b6df875fe277320852353366ede"

do_install () {
        install -d ${D}${bindir}/${BPN}
        install -m 0644 ${S}/README.rst ${D}${bindir}/${BPN}
        install -d ${D}${bindir}/${BPN}/src
        install -m 0755 ${S}/src/phytec_eeprom_flashtool.py ${D}${bindir}/${BPN}/src
        install -d ${D}${bindir}/${BPN}/configs
        install -m 0644 ${S}/configs/* ${D}${bindir}/${BPN}/configs
}

RDEPENDS_${PN} = "python3-pyyaml"
