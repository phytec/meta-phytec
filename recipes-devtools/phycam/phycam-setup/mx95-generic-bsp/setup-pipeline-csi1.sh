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
case $(echo "${CAM_ENT}" | cut -d" " -f1) in
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
ISI_ENT2="mxc_isi.2"
XBAR_ENT="crossbar"
FMT_ENT="4ac10000.syscon:formatter@20"
MIPI_ENT="csidev-4ad30000.csi"
MC="media-ctl -d /dev/media-isi"

echo ""
echo "Setting up MEDIACTL Pipeline"
echo "============================"
echo " Setting up MEDIA Links:"
echo " -----------------------"

if [ -n "$SER_P0_ENT" ] && [ -n "$DESER_ENT" ] && [ "$PORT" = "0" ] ; then
	echo "  Enabling phyCAM-L Port 0 on CSI1"
	echo "   $MC -l \"'${SER_P0_ENT}':1->'${DESER_ENT}':0[1]\""
	$MC -l "'${SER_P0_ENT}':1->'${DESER_ENT}':0[1]" ${VERBOSE}
	if [ -n "$VERBOSE" ] ; then echo "" ; fi

	echo "   $MC -l \"'${DESER_ENT}':2->'${MIPI_ENT}':0[1]\""
	$MC -l "'${DESER_ENT}':2->'${MIPI_ENT}':0[1]" ${VERBOSE}
fi

if [ -n "$SER_P1_ENT" ] && [ -n "$DESER_ENT" ] && [ "$PORT" = "1" ] ; then
	echo "  Enabling phyCAM-L Port 1 on CSI1"
	echo "   $MC -l \"'${SER_P1_ENT}':1->'${DESER_ENT}':1[1]\""
	$MC -l "'${SER_P1_ENT}':1->'${DESER_ENT}':1[1]" ${VERBOSE}
	if [ -n "$VERBOSE" ] ; then echo "" ; fi

	echo "   $MC -l \"'${DESER_ENT}':2->'${MIPI_ENT}':0[1]\""
	$MC -l "'${DESER_ENT}':2->'${MIPI_ENT}':0[1]" ${VERBOSE}
fi

if [ -L /dev/cam-csi1 ] ; then
	echo "  Enabling phyCAM-M Link on CSI1"
	echo "   $MC -l \"'${CAM_ENT}':0->'${MIPI_ENT}':0[1]\""
	$MC -l "'${CAM_ENT}':0->'${MIPI_ENT}':0[1]" ${VERBOSE}
	if [ -n "$VERBOSE" ] ; then echo "" ; fi
fi

setup-isi-routing

echo ""
echo " Setting up MEDIA Formats with"
echo " ${FMT}/${RES} for ${CAM_ENT}"
echo " ----------------------------------------------------------------"
echo "  Sensor CSI1:"
echo "   $MC -V \"'${CAM_ENT}':0/0[fmt:${FMT}/${RES} ${OFFSET}/${FRES} field:none]\""
$MC -V "'${CAM_ENT}':0/0[fmt:${FMT}/${RES} ${OFFSET}/${FRES} field:none]" ${VERBOSE}
echo ""

if [ -n "$SER_P0_ENT" ] && [ -n "$DESER_ENT" ] && [ "$PORT" = "0" ] ; then
	echo "  phyCAM-L Port 0 Serializer on CSI1"
	echo "   $MC -V \"'${SER_P0_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${SER_P0_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
	echo ""
	echo "  phyCAM-L Deserializer on CSI1"
	echo "   $MC -V \"'${DESER_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${DESER_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
	echo ""
fi

if [ -n "$SER_P1_ENT" ] && [ -n "$DESER_ENT" ] && [ "$PORT" = "1" ] ; then
	echo "  phyCAM-L Port 1 Serializer on CSI1"
	echo "   $MC -V \"'${SER_P1_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${SER_P1_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
	echo ""
	echo "  phyCAM-L Deserializer on CSI1"
	echo "   $MC -V \"'${DESER_ENT}':1/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${DESER_ENT}':1/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
	echo ""
fi

echo "  MIPI Interface CSI1:"
if [ -n "$SER_P1_ENT" ] && [ -n "$DESER_ENT" ] && [ "$PORT" = "1" ] ; then
	echo "   $MC -V \"'${MIPI_ENT}':0/1[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${MIPI_ENT}':0/1[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
else
	echo "   $MC -V \"'${MIPI_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${MIPI_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
fi
echo ""

echo "  CSI Formatter CSI1:"
if [ -n "$SER_P1_ENT" ] && [ -n "$DESER_ENT" ] && [ "$PORT" = "1" ] ; then
	echo "   $MC -V \"'${FMT_ENT}':0/1[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${FMT_ENT}':0/1[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
else
	echo "   $MC -V \"'${FMT_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${FMT_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
fi
echo ""

echo "  XBar CSI1:"
if [ -n "$SER_P1_ENT" ] && [ -n "$DESER_ENT" ] && [ "$PORT" = "1" ] ; then
	echo "   $MC -V \"'${XBAR_ENT}':2/1[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${XBAR_ENT}':2/1[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
else
	echo "   $MC -V \"'${XBAR_ENT}':2/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${XBAR_ENT}':2/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
fi
echo ""

echo "  ISI CSI1:"
if [ -n "$SER_P1_ENT" ] && [ -n "$DESER_ENT" ] && [ "$PORT" = "1" ] ; then
	echo "   $MC -V \"'${ISI_ENT2}':0/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${ISI_ENT2}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
else
	echo "   $MC -V \"'${ISI_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
	$MC -V "'${ISI_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
fi
echo ""
