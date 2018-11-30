inherit pkgconfig deploy

#The firmware is build outside of yocto.
#Steps how the firmware was created
#Used toolchain: gcc-arm-none-eabi-6-2017-q2-update
#1. tar xvjf Downloads/gcc-arm-none-eabi-6-2017-q2-update-linux.tar.bz2
#2. export TOOLS=~/Downloads/
#3. make qm B=phycore DL=2 V=1

SRC_URI = "ftp://ftp.phytec.de/pub/Software/Linux/Driver/${PN}_${PV}-phy1.tar.gz"

SRC_URI[md5sum] = "382e60db63e115f1e2d532363bb87bb9"
SRC_URI[sha256sum] = "18050b95a1c9912e52ee7e562018b0b0af0bc2f113e488475e6904ac38f2f5e2"

S = "${WORKDIR}/${PN}_${PV}-phy1"
