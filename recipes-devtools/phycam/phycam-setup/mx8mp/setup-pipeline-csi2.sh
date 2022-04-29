#!/bin/bash

display_help() {
	echo "Usage: ./setup-pipeline-csi2.sh [-f <format>] [-s <frame size>] [-o <offset>] [-c <sensor size>] [-v]"
}

if ! [ -L /dev/cam-csi2 ] ; then
	echo "No camera found on CSI2"
	exit 1
fi

CAM_ENT="$(cat /sys/class/video4linux/$(readlink /dev/cam-csi2)/name)"

case $(echo ${CAM_ENT} | cut -d" " -f1) in
	ar0144 )
		CAM_BW_FMT="Y8_1X8"
		CAM_COL_FMT="SGRBG8_1X8"
		SENSOR_RES="1280x800"
		OFFSET_SENSOR="(0,4)"
		;;
	ar0521 )
		CAM_BW_FMT="Y8_1X8"
		CAM_COL_FMT="SGRBG8_1X8"
		SENSOR_RES="2592x1944"
		OFFSET_SENSOR="(0,0)"
		;;
	* ) echo "Unknown camera" ; exit 1
esac

OPTIONS="hf:s:o:c:v"
COLOR="$(v4l2-ctl -d /dev/cam-csi2 --get-subdev-fmt 0 | grep "Mediabus Code" | sed 's/.*BUS_FMT_\([A-Z]*\).*/\1/g')"
if [ $COLOR = "Y" ]; then
	FMT="${CAM_BW_FMT}"
else
	FMT="${CAM_COL_FMT}"
fi
RES="$SENSOR_RES"
OFFSET="$OFFSET_SENSOR"
FRES="$SENSOR_RES"
VERBOSE=""

while getopts $OPTIONS option
do
	case $option in
		f ) FMT=$OPTARG;;
		s ) RES=$OPTARG;;
		o ) OFFSET=$OPTARG;;
		c ) FRES=$OPTARG;;
		v ) VERBOSE="-v";;
		h  ) display_help; exit;;
		\? ) echo "Unknown option: -$OPTARG" >&2; exit 1;;
		:  ) echo "Missing option argument for -$OPTARG" >&2; exit 1;;
		*  ) echo "Unimplemented option: -$OPTARG" >&2; exit 1;;
	esac
done

echo "Setting ${FMT}/${RES} ${OFFSET}/${FRES} for ${CAM_ENT}"

media-ctl -l "'mxc-mipi-csi2.1':4->'mxc_isi.1':4[1]" ${VERBOSE}
media-ctl -l "'mxc_isi.1':12->'mxc_isi.1.capture':0[1]" ${VERBOSE}

if [ -L /dev/phycam-deserializer-csi2 ] && [ -L /dev/phycam-serializer-csi2 ] ; then
	DESERIALIZER="$(cat /sys/class/video4linux/$(readlink /dev/phycam-deserializer-csi2)/name)"
	SERIALIZER="$(cat /sys/class/video4linux/$(readlink /dev/phycam-serializer-csi2)/name)"
	media-ctl -l "'${SERIALIZER}':1->'${DESERIALIZER}':0[1]" ${VERBOSE}
fi

media-ctl -V "'${CAM_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]" ${VERBOSE}
media-ctl -V "'mxc-mipi-csi2.1':0 [fmt:${FMT}/${RES} field:none]" ${VERBOSE}
