#!/bin/sh

display_help() {
	echo "Usage: ./setup-pipeline-csi0.sh [-f <format>] [-s <frame size>] [-o <offset>] [-c <sensor size>] [-p <phycam-l port] [-v]"
}

OPTIONS="hf:s:o:c:p:v"
RES=
FMT=
OFFSET=
FRES=
VERBOSE=
PORT=

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

# Select the correct camera subdevice. Can be phyCAM-M or phyCAM-L (Port 0 xor 1).
if [ -L /dev/cam-csi0 ] && [ -z "$PORT" ]; then
	CAM="/dev/cam-csi0"
elif [ -L /dev/cam-csi0-port0 ] && [ "$PORT" = "0" ] ; then
	CAM="/dev/cam-csi0-port0"
elif [ -L /dev/cam-csi0-port1 ] && [ "$PORT" = "1" ] ; then
	CAM="/dev/cam-csi0-port1"
else
	echo "No camera found on CSI0"
	exit 1
fi

# Check if we have a phyCAM-L interface connected.
SER_P0="/dev/phycam-serializer-port0-csi0"
SER_P0_ENT=
SER_P1="/dev/phycam-serializer-port1-csi0"
SER_P1_ENT=
DESER="/dev/phycam-deserializer-csi0"
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

CSI_ENT="4500000.ticsi2rx"
MIPI_ENT="cdns_csi2rx.4504000.csi-bridge"
MC_CSI="media-ctl -d /dev/media-csi0"

echo ""
echo "Setting up MEDIA Pipeline with"
echo "${FMT}/${RES} ${OFFSET}/${FRES} for ${CAM_ENT}"
echo "========================================================="

echo " Setting up MEDIA Formats:"
echo " -------------------------"
echo "  Sensor:"
echo "   ${MC_CSI} -V \"'${CAM_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]\""
${MC_CSI} -V "'${CAM_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]" ${VERBOSE}
echo ""

if [ -n "${DESER_ENT}" ] ; then
	echo "  De-/Serializer:"

	VC_P0_DESER="0/0->2/0[1]"
	VC_P1_DESER="1/0->2/1[1]"

	VC_P0_MIPI="0/0->1/0[1]"
	VC_P1_MIPI="0/1->1/1[1]"

	VC_P0_CSI="0/0->1/0[1]"
	VC_P1_CSI="0/1->2/0[1]"

	# TODO check ports, what about both?!->rework to allow setting both!
	VC_DESER="${VC_P0_DESER}" # comma separation if use both!
	VC_MIPI="${VC_P0_MIPI}"
	VC_CSI="${VC_P0_CSI}"

	if [ "${PORT}" = "1" ] ; then
		VC_DESER="${VC_P1_DESER}"
		VC_MIPI="${VC_P1_MIPI}"
		VC_CSI="${VC_P1_CSI}"
	fi

	echo "   media-ctl -R \"'${DESER_ENT}'[${VC_DESER}]\""
	media-ctl -R "'${DESER_ENT}'[${VC_DESER}]"

	echo "   media-ctl -R \"'${MIPI_ENT}'[${VC_MIPI}]\""
	media-ctl -R "'${MIPI_ENT}'[${VC_MIPI}]"

	echo "   media-ctl -R \"'${CSI_ENT}'[${VC_CSI}]\""
	media-ctl -R "'${CSI_ENT}'[${VC_CSI}]"
	echo ""

	if [ -n "${SER_P0_ENT}" ] && [ "${PORT}" = "0" ] ; then
		echo "   ${MC_CSI} -V \"'${SER_P0_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
		${MC_CSI} -V "'${SER_P0_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
		echo "   ${MC_CSI} -V \"'${DESER_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
		${MC_CSI} -V "'${DESER_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
		echo ""
		echo "  MIPI/CSI Interface VC0:"
		echo "   ${MC_CSI} -V \"'${MIPI_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
		${MC_CSI} -V "'${MIPI_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
		echo "   ${MC_CSI} -V \"'${CSI_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
		${MC_CSI} -V "'${CSI_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}

	fi
	if [ -n "${SER_P1_ENT}" ] && [ "${PORT}" = "1" ] ; then
		echo "   ${MC_CSI} -V \"'${SER_P1_ENT}':0/0[fmt:${FMT}/${RES} field:none]\""
		${MC_CSI} -V "'${SER_P1_ENT}':0/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
		echo "   ${MC_CSI} -V \"'${DESER_ENT}':1/0[fmt:${FMT}/${RES} field:none]\""
		${MC_CSI} -V "'${DESER_ENT}':1/0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
		echo ""
		echo "  MIPI/CSI Interface VC1:"
		echo "   ${MC_CSI} -V \"'${MIPI_ENT}':0/1[fmt:${FMT}/${RES} field:none]\""
		${MC_CSI} -V "'${MIPI_ENT}':0/1[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
		echo "   ${MC_CSI} -V \"'${CSI_ENT}':0/1[fmt:${FMT}/${RES} field:none]\""
		${MC_CSI} -V "'${CSI_ENT}':0/1[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
	fi
else
	echo "  MIPI Interface:"
	echo "   ${MC_CSI} -V \"'${MIPI_ENT}':0[fmt:${FMT}/${RES} field:none]\""
	${MC_CSI} -V "'${MIPI_ENT}':0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
	echo ""
	echo "  CSI Interface:"
	echo "   ${MC_CSI} -V \"'${CSI_ENT}':0[fmt:${FMT}/${RES} field:none]\""
	${MC_CSI} -V "'${CSI_ENT}':0[fmt:${FMT}/${RES} field:none]" ${VERBOSE}
fi
echo ""
