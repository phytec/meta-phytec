#!/bin/sh

display_help() {
	echo "Usage: ./setup-pipeline-csi1.sh [-f <format>] [-s <frame size>] [-o <offset>] [-c <sensor size>] [-v]"
}

if ! [ -e /dev/cam-csi1 ] ; then
	echo "No camera found on CSI1"
	exit 1
fi

CAM_ENT="$(cat /sys/class/video4linux/$(readlink /dev/cam-csi1)/name)"

case $(echo ${CAM_ENT} | cut -d" " -f1) in
	mt9m111 )
		CAM_COL_FMT="YUYV8_2X8"
		SENSOR_RES="1280x1024"
		OFFSET_SENSOR="(26,8)"
		;;
	mt9v032 )
		CAM_BW_FMT="Y8_1X8"
		CAM_COL_FMT="SGRBG8_1X8"
		SENSOR_RES="752x480"
		OFFSET_SENSOR="(1,5)"
		;;
	mt9p031 )
		CAM_BW_FMT="Y8_1X8"
		CAM_COL_FMT="SGRBG8_1X8"
		SENSOR_RES="2592x1944"
		OFFSET_SENSOR="(16,54)"
		;;
	ar0144 )
		CAM_BW_FMT="Y8_1X8"
		CAM_COL_FMT="SGRBG8_1X8"
		SENSOR_RES="1280x800"
		OFFSET_SENSOR="(0,4)"
		;;
	* ) echo "Unknown camera" ; exit 1
esac

OPTIONS="hf:s:o:c:v"
COLOR="$(v4l2-ctl -d /dev/cam-csi1 --get-subdev-fmt 0 | grep "Mediabus Code" | sed 's/.*BUS_FMT_\([A-Z]*\).*/\1/g')"
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

media-ctl -V "'${CAM_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]" ${VERBOSE}
media-ctl -V "'csi':1 [fmt:${FMT}/${RES} field:none]" ${VERBOSE}
