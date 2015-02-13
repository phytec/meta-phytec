# Overwrite GIT_URL and SRC_URI from recipe
# NOTE: GIT_URL is used in the task buildinfo and should be useable for git
# clone. Sadly yocto needs a different format of the url to checkout out the
# git repository over the ssh protocol. Therefore we have to specify both formats.
GIT_URL = "ssh://git@git.phytec.de/barebox-dev"
SRC_URI = "git://git@git.phytec.de/barebox-dev;protocol=ssh;branch=${BRANCH}"

do_deploy_append () {
    if [ ${DEV_BUILD} == "True" ]; then
        bbnote "Deploying barebox-ipl to ${TFTP_ROOT}"
        install -m 777 ${B}/${BAREBOX_IPL_BIN} ${TFTP_ROOT}/${BAREBOX_IPL_BIN_SYMLINK}
    fi
}

do_copysd () {
    if [ -e ${SDCARD_MOUNT}${SDCARD_BOOTPART} ]; then
        install -m 777 ${B}/${BAREBOX_IPL_BIN} ${SDCARD_MOUNT}${SDCARD_BOOTPART}/${BAREBOX_IPL_BIN_SYMLINK}
    fi
}
do_copysd[nostamp] = "1"
addtask copysd
