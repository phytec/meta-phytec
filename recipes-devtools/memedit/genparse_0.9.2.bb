SUMMARY = "Genparse"
DESCRIPTION = " Genparse is a generic command line parser generator.  From simple and \
                concise specification file, you can define the command line parameters \
                and switches that you would like to be able to pass to your program. \
                Genparse creates the C or C++ code of the parser for you, which you can then \
                compile as a separate file and link with your program."
HOMEPAGE = "http://genparse.sourceforge.net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools
BBCLASSEXTEND = "native"

DEPENDS = ""

SRC_URI = " \
    http://sourceforge.net/projects/genparse/files/genparse/0.9.2/genparse-0.9.2.tar.gz \
    file://genparse-build-minimal.patch \
"
SRC_URI[md5sum] = "c0849a5f309318fd866237685093ac12"
SRC_URI[sha256sum] = "b57337dd28685fd49df34d4788dba1fa660803fec5c309a62d1861e64e5d3323"
