FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
# Machine specific

SRC_SND_FILES = " "
SRC_SND_FILES_phycore-stm32mp1-1 += " \
    file://asound-phycore-stm32mp1-1.state   \
    file://asound-phycore-stm32mp1-1.conf   \
"

SRC_SND_FILES_phycore-stm32mp1-2 += " \
    file://asound-phycore-stm32mp1-2.state   \
    file://asound-phycore-stm32mp1-2.conf   \
"

SRC_SND_FILES_phycore-stm32mp1-3 += " \
    file://asound-phycore-stm32mp1-3.state   \
    file://asound-phycore-stm32mp1-3.conf   \
"

SRC_URI += " \
    ${SRC_SND_FILES} \
"
