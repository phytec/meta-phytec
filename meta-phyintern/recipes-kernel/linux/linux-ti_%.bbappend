FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# Overwrite GIT_URL and SRC_URI from recipe
# NOTE: GIT_URL is used in the task buildinfo and should be useable for git
# clone. Sadly yocto needs a different format of the url to checkout out the
# git repository over the ssh protocol. Therefore we have to specify both formats.
GIT_URL = "ssh://git@git.phytec.de/${PN}-dev"
SRC_URI = "git://git@git.phytec.de/${PN}-dev;protocol=ssh;branch=${BRANCH}"
#SRC_URI = "git://${HOME}/linux-ti;protocol=file;branch=${BRANCH} file://defconfig"
#BRANCH = ""
#SRCREV = "${AUTOREV}"
#PV = "${LINUX_VERSION}-git${SRCPV}"

#SRC_URI_append_phycore-am335x-1 = " \
#    file://0001-phycore-rdk-activate-display.patch \
#"
#SRC_URI_append_phycore-am335x-2 = " \
#    file://0001-phycore-rdk-activate-display.patch \
#"
#SRC_URI_append_phyflex-am335x-1 = " \
#    file://0002-phyflex-enable-display.patch
#    file://smtpe.cfg 
#    file://0001-phyflex-try-without-fixups.patch 

#SRC_URI_append_phyboard-wega-1 = " \
#    file://0001-wega-activate-hdmi-and-leds.patch \
#"

do_deploy_append () {
    if [ ${DEV_BUILD} == "True" ]; then
        bbnote "Deploying linuximage and oftree to ${TFTP_ROOT}"
        install -m 777 ${KERNEL_OUTPUT} ${TFTP_ROOT}/${DEV_USER}-linux-${MACHINE}
        install -m 777 ${DTB_PATH} ${TFTP_ROOT}/${DEV_USER}-oftree-${MACHINE}
    fi
}
