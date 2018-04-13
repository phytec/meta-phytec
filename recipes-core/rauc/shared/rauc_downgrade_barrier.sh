#!/bin/bash

VERSION_FILE=/etc/rauc/version
MANIFEST_FILE=${RAUC_UPDATE_SOURCE}/manifest.raucm

if [ ! -f ${VERSION_FILE} ]
then
        exit 1
fi
if [ ! -f ${MANIFEST_FILE} ]
then
        exit 2
fi

VERSION=`cat ${VERSION_FILE}`
BUNDLE_VERSION=`grep "version" -rI ${MANIFEST_FILE}`

SOC=`echo ${VERSION} | cut -d '-' -f 1`
RELEASE=`echo ${VERSION} | cut -d '-' -f 3`

YEAR=`echo ${RELEASE} | cut -d '.' -f 1`
MAJOR=`echo ${RELEASE} | cut -d '.' -f 2`
MINOR=`echo ${RELEASE} | cut -d '.' -f 3`

if [[ ! ${RELEASE} =~ [0-9\.] ]]
then
        echo "Seems like this rootfs contains a next image. Skip version check..."
        exit 0
fi

TMP=`echo ${BUNDLE_VERSION} | cut -d '=' -f 2`
BUNDLE_RELEASE=`echo ${TMP} | cut -d '-' -f 3`

BUNDLE_YEAR=`echo ${BUNDLE_RELEASE} | cut -d '.' -f 1`
BUNDLE_MAJOR=`echo ${BUNDLE_RELEASE} | cut -d '.' -f 2`
BUNDLE_MINOR=`echo ${BUNDLE_RELEASE} | cut -d '.' -f 3`

if [[ $((${BUNDLE_YEAR})) -ge $((${YEAR})) ]]
then
	exit 0
else
	if [[ $((${BUNDLE_MAJOR})) -ge $((${MAJOR})) ]]
	then
		exit 0
	else
		if [[ $((${BUNDLE_MINOR})) -ge $((${MINOR})) ]]
		then
			exit 0
		else
			exit 3
		fi
	fi
fi
exit 4
