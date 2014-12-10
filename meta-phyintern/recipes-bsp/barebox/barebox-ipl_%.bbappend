SRC_URI = "git://git@git.phytec.de/barebox-dev;protocol=ssh;branch=${BRANCH}"

do_deploy_append () {
    if [ ${DEV_BUILD} == "True" ]; then
        bbnote "Deploying barebox-ipl to ${TFTP_ROOT}"
        install -m 777 ${S}/${BAREBOX_IPL_BIN} ${TFTP_ROOT}/${BAREBOX_IPL_BIN_SYMLINK}
    fi
}

do_copysd () {
    if [ -e ${SDCARD_MOUNT}${SDCARD_BOOTPART} ]; then
        install -m 777 ${S}/${BAREBOX_IPL_BIN} ${SDCARD_MOUNT}${SDCARD_BOOTPART}/${BAREBOX_IPL_BIN_SYMLINK}
    fi
}
do_copysd[nostamp] = "1"
addtask copysd
