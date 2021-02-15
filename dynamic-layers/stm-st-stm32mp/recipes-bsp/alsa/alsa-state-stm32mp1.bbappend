FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://asound-stm32mp1-phyboard-sargas.state   \
    file://asound-stm32mp1-phyboard-sargas.conf   \
"

SUPPORTED_SARGAS_MACHINE_IDS ?= "1 2 3 4 5 6 7"

do_install_append() {
    # follow the st style of handling alsa config
    for id in ${SUPPORTED_SARGAS_MACHINE_IDS};
    do
        cd ${D}${sysconfdir}/
        ln -sf asound-stm32mp1-phyboard-sargas.conf asound-phycore-stm32mp1-$id.conf
        cd ${D}${localstatedir}/lib/alsa
        ln -sf asound-stm32mp1-phyboard-sargas.state asound-phycore-stm32mp1-$id.state
    done
}
