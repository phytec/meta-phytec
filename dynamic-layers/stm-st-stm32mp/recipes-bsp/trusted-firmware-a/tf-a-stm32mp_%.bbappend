FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"


SRC_URI += " \
	file://0003-phycore-update-r1.1.0.patch \
        file://0004-add-phycore-stm32mp1-4-machine-r1.1.0.patch \
        file://0005-add-phycore-stm32mp1-5-machine-r1.1.0.patch \
        file://0006-add-phycore-stm32mp1-6-machine-r1.1.0.patch \
        file://0007-add-phycore-stm32mp1-7-machine-r1.1.0.patch \
"
