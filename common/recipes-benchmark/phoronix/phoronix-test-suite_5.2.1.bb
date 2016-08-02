SUMMARY = "Phoronix Test Suite - Installer Script"
DESCRIPTION = " The Phoronix Test Suite is the most comprehensive testing and \
                benchmarking platform available that provides an extensible \
                framework for which new tests can be easily added. The software \
                is designed to effectively carry out both qualitative and quantitative \
                benchmarks in a clean, reproducible, and easy-to-use manner."
HOMEPAGE = "http://phoronix-test-suite.com"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

RDEPENDS_${PN} = "php"
SRC_URI =  "http://www.phoronix-test-suite.com/releases/{BP}.tar.gz"
SRC_URI[md5sum] = "51e52d883710dc516c5494bd1c377219"
SRC_URI[sha256sum] = "1186f460691e2fe7a07df5edb8d8ed1ac0c65327512e646da2b2e3a60dda6cd9"

do_configure () {
}
do_compile () {
}
do_install () {
    DESTDIR=${D} ./install-sh ${exec_prefix}
}

FILES_${PN} += " \
${datadir}/phoronix-test-suite \
${datadir}/appdata/phoronix-test-suite.appdata.xml \
${datadir}/icons/hicolor/48x48/apps/phoronix-test-suite.png \
${datadir}/icons/hicolor/64x64/mimetypes/application-x-openbenchmarking.png \
${datadir}/mime/packages/openbenchmarking-mime.xml \
"
