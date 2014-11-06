FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "git://git@git.phytec.de/linux-ti;protocol=ssh;branch=${BRANCH}"
#SRC_URI = "git://${HOME}/linux-ti;protocol=file;branch=${BRANCH} file://defconfig"
#BRANCH = ""
#SRCREV = "${AUTOREV}"
#PV = "${LINUX_VERSION}-git${SRCPV}"

SRC_URI_append = " \
    file://ipv6.cfg \
    file://systemd.cfg \
    file://resetdriver.cfg \
"

SRC_URI_append_phycore-am335x-1 = " \
    file://0001-phycore-rdk-activate-display.patch \
"
SRC_URI_append_phycore-am335x-2 = " \
    file://0001-phycore-rdk-activate-display.patch \
"
SRC_URI_append_phyflex-am335x-1 = " \
    file://0001-phyflex-try-without-fixups.patch \
    file://0002-phyflex-enable-display.patch \
    file://smtpe.cfg \
"

SRC_URI_append_phyboard-wega-1 = " \
    file://0001-wega-activate-hdmi-and-leds.patch \
"

do_deploy_append () {
    if [ ${DEV_BUILD} == "True" ]; then
        bbnote "Deploying linuximage and oftree to ${TFTP_ROOT}"
        install -m 777 ${KERNEL_OUTPUT} ${TFTP_ROOT}/${DEV_USER}-linux-${MACHINE}
        install -m 777 ${DTB_PATH} ${TFTP_ROOT}/${DEV_USER}-oftree-${MACHINE}
    fi
}


