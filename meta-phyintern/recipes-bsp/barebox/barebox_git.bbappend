# Overwrite GIT_URL and SRC_URI from recipe
# NOTE: GIT_URL is used in the task buildinfo and should be useable for git
# clone. Sadly yocto needs a different format of the url to checkout out the
# git repository over the ssh protocol. Therefore we have to specify both formats.
GIT_URL = "ssh://git@git.phytec.de/${PN}-dev"
SRC_URI = "git://git@git.phytec.de/${PN}-dev;protocol=ssh;branch=${BRANCH}"

VERBOSE_BUILD = "1"

# write username and filenames in barebox environment
python do_prepare_env_append() {
    if d.getVar('DEV_USER', True):
        cmd='sed -i.bak "s/.*global\.user.*/global\.user=${DEV_USER}/g" ${S}/.commonenv/config'
        subprocess.call(cmd,shell=True)
    if d.getVar('BAREBOX_BOOTSRC', True):
        cmd='sed -i.bak "s/.*global\.boot\.default=.*/global\.boot\.default=${BAREBOX_BOOTSRC}/g" ${S}/.commonenv/config'
        subprocess.call(cmd,shell=True)
    if os.path.exists(os.path.join(S,'.commonenv/config.bak')):
        oe.path.remove(os.path.join(S,'.commonenv/config.bak'))
}

do_deploy_append () {
	if [ ${DEV_BUILD} == "True" ]; then
		bbnote "Deploying barebox to ${TFTP_ROOT}"
		install -m 777 ${B}/${BAREBOX_BIN} ${TFTP_ROOT}/${DEV_USER}-barebox-${MACHINE}
	fi
}

do_copysd () {
	if [ -e ${SDCARD_MOUNT}${SDCARD_BOOTPART} ]; then
		install -m 777 ${B}/${BAREBOX_BIN} ${SDCARD_MOUNT}${SDCARD_BOOTPART}/barebox.bin
	fi
}
do_copysd[nostamp] = "1"
addtask copysd
