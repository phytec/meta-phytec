FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:am57xx = " \
    file://0001-km-Fix-build-against-recent-kernels.patch \
    file://0002-km-Fix-build-by-importing-DMA_BUF-namespace.patch \
"
