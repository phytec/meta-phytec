SUMMARY = "skeleton main.c generator"
DESCRIPTION = "Gengetopt is a tool to write command line option parsing code for C programs."
SECTION = "utils"
HOMEPAGE = "https://www.gnu.org/software/gengetopt/gengetopt.html"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=ff95bfe019feaf92f524b73dd79e76eb"

SRC_URI = "${GNU_MIRROR}/gengetopt/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "29749a48dda69277ab969c510597a14e"
SRC_URI[sha256sum] = "30b05a88604d71ef2a42a2ef26cd26df242b41f5b011ad03083143a31d9b01f7"

inherit autotools

BBCLASSEXTEND = "native nativesdk"

PARALLEL_MAKE = ""
