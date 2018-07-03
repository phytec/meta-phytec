FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/:"

SRC_URI += "\
    file://0001-ATF-support-to-different-LPDDR4-configurations.patch \
    file://0002-imx8m_bl31_setup-set-mem-size-to-1GB.patch \
"
