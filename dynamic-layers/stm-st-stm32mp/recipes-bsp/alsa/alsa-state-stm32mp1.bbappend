FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
# Machine specific

SRC_URI += " \
    file://asound-phycore-stm32mp1-1.state   \
    file://asound-phycore-stm32mp1-1.conf   \
    "
