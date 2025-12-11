#!/bin/sh

display_help() {
	echo "Usage: ./setup-pipeline-csi1.sh [-f <format>] [-s <frame size>] [-o <offset>] [-c <sensor size>] [-v]"
}

OPTIONS="hf:s:o:c:v"
RES=
FMT=
OFFSET=
FRES=
VERBOSE=

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

# Select the correct camera subdevice. Can be phyCAM-M or phyCAM-L (Port 0 or 1).
if [ -L /dev/cam-csi1 ] ; then
	CAM="/dev/cam-csi1"
else
	echo "No camera found on CSI1"
	exit 1
fi

# Get sensor default values.
CAM_ENT="$(cat /sys/class/video4linux/$(readlink ${CAM})/name)"
case $(echo ${CAM_ENT} | cut -d" " -f1) in
	ar0144 )
		CAM_BW_FMT="Y8_1X8"
		CAM_COL_FMT="SGRBG8_1X8"
		SENSOR_RES="1280x800"
		OFFSET_SENSOR="(0,4)"
		;;
	ar0234 )
		CAM_BW_FMT="Y8_1X8"
		CAM_COL_FMT="SGRBG8_1X8"
		SENSOR_RES="1920x1200"
		OFFSET_SENSOR="(8,8)"
		;;
	* ) echo "Unknown camera" ; exit 1
esac

# Evaluate if a monochrome or color sensor is connected by checking the
# default MBUS code.
COLOR="$(v4l2-ctl -d ${CAM} --get-subdev-fmt 0 | \
	 grep "Mediabus Code" | \
	 sed 's/.*BUS_FMT_\([A-Z]*\).*/\1/g')"
if [ $COLOR = "Y" ]; then
	SENSOR_FMT="${CAM_BW_FMT}"
else
	SENSOR_FMT="${CAM_COL_FMT}"
fi

# Set defaults if user did not supply a setting.
if [ -z $FMT ] ; then FMT="$SENSOR_FMT" ; fi
if [ -z $RES ] ; then RES="$SENSOR_RES" ; fi
if [ -z $FRES ] ; then FRES="$SENSOR_RES" ; fi
if [ -z $OFFSET ] ; then OFFSET="$OFFSET_SENSOR" ; fi

CAP_ENT="mxc_isi.0.capture"
ISI_ENT="mxc_isi.0"
XBAR_ENT="crossbar"
MIPI_ENT="csidev-4ae00000.csi"
MC="media-ctl"

echo "Setting ${FMT}/${RES} ${OFFSET}/${FRES} for ${CAM_ENT}"
# Setup pipeline links.
$MC -l "'${CAM_ENT}':0->'${MIPI_ENT}':0[1]" ${VERBOSE}

# Setup the desired format on sensor, MIPI bridge and CSI interface.
$MC -V "'${CAM_ENT}':0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
$MC -V "'${MIPI_ENT}':0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
$MC -V "'${XBAR_ENT}':0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
$MC -V "'${ISI_ENT}':0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
