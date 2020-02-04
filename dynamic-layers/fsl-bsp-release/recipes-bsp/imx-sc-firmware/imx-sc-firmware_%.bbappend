inherit pkgconfig deploy

#The firmware is build outside of yocto.
#Steps how the firmware was created
#Used toolchain: gcc-arm-none-eabi-6-2017-q2-update
#1. tar xvjf Downloads/gcc-arm-none-eabi-6-2017-q2-update-linux.tar.bz2
#2. export TOOLS=~/Downloads/
#3. make qm B=phycore DL=5 V=1 R=B0

SRC_URI = "ftp://ftp.phytec.de/pub/Software/Linux/Driver/${PN}_${PV}-phy2.tar.gz"

SRC_URI[md5sum] = "d6f919a5945b1bd5fd2239cd66d105eb"
SRC_URI[sha256sum] = "665de92eeccadee5a99882c0c5416205c3caf5c5f9f269bca1362778d6055168"

S = "${WORKDIR}/${PN}_${PV}-phy2"
