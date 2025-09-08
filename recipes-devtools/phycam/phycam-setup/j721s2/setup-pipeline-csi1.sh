#!/bin/sh

display_help() {
	echo "Usage: ./setup-pipeline-csi1.sh [-f <format>] [-s <frame size>] [-o <offset>] [-c <sensor size>] [-p <phycam-l port] [-v]"
}

OPTIONS="hf:s:o:c:p:v"
RES=
FMT=
OFFSET=
FRES=
VERBOSE=
PORT=0

while getopts $OPTIONS option
do
	case $option in
		f ) FMT=$OPTARG;;
		s ) RES=$OPTARG;;
		o ) OFFSET=$OPTARG;;
		c ) FRES=$OPTARG;;
		p ) PORT=$OPTARG;;
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
elif [ -L /dev/cam-csi1-port0 ] && [ "$PORT" = "0" ] ; then
	CAM="/dev/cam-csi1-port0"
elif [ -L /dev/cam-csi1-port1 ] && [ "$PORT" = "1" ] ; then
	CAM="/dev/cam-csi1-port1"
else
	echo "No camera found on CSI1"
	exit 1
fi

# Check if we have a phyCAM-L interface connected.
SER_P0="/dev/phycam-serializer-port0-csi1"
SER_P0_ENT=
SER_P1="/dev/phycam-serializer-port1-csi1"
SER_P1_ENT=
DESER="/dev/phycam-deserializer-csi1"
DESER_ENT=

if [ -L $SER_P0 ] ; then
	SER_P0_ENT="$(cat /sys/class/video4linux/$(readlink ${SER_P0})/name)"
fi

if [ -L $SER_P1 ] ; then
	SER_P1_ENT="$(cat /sys/class/video4linux/$(readlink ${SER_P1})/name)"
fi

if [ -L $DESER ] ; then
	DESER_ENT="$(cat /sys/class/video4linux/$(readlink ${DESER})/name)"
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
	ar0521 )
		CAM_BW_FMT="Y8_1X8"
		CAM_COL_FMT="SGRBG8_1X8"
		SENSOR_RES="2592x1944"
		OFFSET_SENSOR="(4,4)"
		;;
	* ) echo "Unknown camera" ; exit 1
esac

# Evaluate if a monochrome or color sensor is connected by checking the
# default MBUS code.
COLOR="$(v4l2-ctl -d ${CAM} --get-subdev-fmt | \
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

CSI_ENT="4510000.ticsi2rx"
MIPI_ENT="cdns_csi2rx.4514000.csi-bridge"
MC="media-ctl -d /dev/media-csi1"

echo ""
echo "Setting up MEDIA Pipeline with"
echo "${FMT}/${RES} ${OFFSET}/${FRES} for ${CAM_ENT}"
echo "========================================================="

echo " Setting up MEDIA Formats:"
echo " -------------------------"
echo "  Sensor:"
echo "   $MC -V \"'${CAM_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]\""
$MC -V "'${CAM_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]" ${VERBOSE}
echo ""
echo "  MIPI Interface:"
echo "   $MC -V \"'${MIPI_ENT}':1[fmt:${FMT}/${RES}]\""
$MC -V "'${MIPI_ENT}':1[fmt:${FMT}/${RES}]" ${VERBOSE}
echo ""
echo "  CSI Interface:"
echo "   $MC -V \"'${CSI_ENT}':1[fmt:${FMT}/${RES}]\""
$MC -V "'${CSI_ENT}':1[fmt:${FMT}/${RES}]" ${VERBOSE}
echo ""
