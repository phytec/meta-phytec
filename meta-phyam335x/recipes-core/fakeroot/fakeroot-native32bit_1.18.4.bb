require recipes-core/fakeroot/fakeroot-native_${PV}.bb

EXTRA_OECONF = "--program-prefix= --target=i686-pc-linux-gnu"
CFLAGS = "-O2 -m32"
CXXFLAGS = "${CFLAGS}"
