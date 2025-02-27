FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " \
    file://0001-Fix-cryptodev_verbosity-sysctl-for-Linux-6.11-rc1.patch \
"
