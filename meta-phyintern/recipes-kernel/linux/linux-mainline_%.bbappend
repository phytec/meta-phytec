FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "git://git@git.phytec.de/linux-mainline;protocol=ssh;branch=${BRANCH}"

SRC_URI_append = " \
  file://ipv6.cfg \
  file://systemd.cfg \
  file://resetdriver.cfg \
"

do_deploy_append () {
    if [ ${DEV_BUILD} == "True" ]; then
        bbnote "Deploying linuximage and oftree to ${TFTP_ROOT}"
        install -m 777 ${KERNEL_OUTPUT} ${TFTP_ROOT}/${DEV_USER}-linux-${MACHINE}
        install -m 777 ${DTB_PATH} ${TFTP_ROOT}/${DEV_USER}-oftree-${MACHINE}
    fi
}
