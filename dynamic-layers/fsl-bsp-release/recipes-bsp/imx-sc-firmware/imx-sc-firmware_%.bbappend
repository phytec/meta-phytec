inherit pkgconfig deploy

#The firmware is build outside of yocto.
#Steps how the firmware was created
#Used toolchain: gcc-arm-none-eabi-6-2017-q2-update
#1. tar xvjf Downloads/gcc-arm-none-eabi-6-2017-q2-update-linux.tar.bz2
#2. export TOOLS=~/Downloads/
#3. make qm B=phycore DL=5 V=1 R=B0

SRC_URI = "ftp://ftp.phytec.de/pub/Software/Linux/Driver/${PN}_${PV}-phy1.tar.gz"

SRC_URI[md5sum] = "9ee6ef2e2a28f0428455bd4869cfe965"
SRC_URI[sha256sum] = "4bdbe5c36249e3c8eee7312c1b1fb61682bde8f5837067c95ee7844b0ec6f142"

S = "${WORKDIR}/${PN}_${PV}-phy1"
