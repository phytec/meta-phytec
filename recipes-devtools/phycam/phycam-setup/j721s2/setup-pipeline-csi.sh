#!/bin/sh

display_help() {
	echo "Usage: ${0} [-f <format>] [-s <frame size>] [-o <offset>] [-c <sensor size>] [-p <phycam-l port>] [-v]"
}

read_port() {
	PORT="$1"
	if [ ${PORT} = "0" ]; then
		PORT0=true
	elif [ ${PORT} = "1" ]; then
		PORT1=true
	elif [ ${PORT} = "both" ]; then
		PORT0=true
		PORT1=true
	else
		echo "Invalid port argument, use '0', '1' or 'both'!"
	fi
}

OPTIONS="hi:f:s:o:c:p:v"
RES=
FMT=
OFFSET=
FRES=
VERBOSE=
CSI="0"
PORT0=false
PORT1=false

while getopts $OPTIONS option
do
	case $option in
		i ) CSI=${OPTARG};;
		f ) FMT=${OPTARG};;
		s ) RES=${OPTARG};;
		o ) OFFSET=${OPTARG};;
		c ) FRES=${OPTARG};;
		p ) read_port "${OPTARG}";;
		v ) VERBOSE="-v";;
		h  ) display_help; exit;;
		\? ) echo "Unknown option: -${OPTARG}" >&2; exit 1;;
		:  ) echo "Missing option argument for -${OPTARG}" >&2; exit 1;;
		*  ) echo "Unimplemented option: -${OPTARG}" >&2; exit 1;;
	esac
done

# Select the correct camera subdevice. Can be phyCAM-M or phyCAM-L (Port 0 or 1).
if [ -L /dev/cam-csi${CSI} ] && [ "${PORT0}" = false ] && [ "${PORT1}" = false ]; then
	CAM0="/dev/cam-csi${CSI}"
elif [ -L /dev/cam-csi${CSI}-port0 ] && [ "${PORT0}" = true ] ; then
	CAM0="/dev/cam-csi${CSI}-port0"
	if [ -L /dev/cam-csi${CSI}-port1 ] && [ "${PORT1}" = true ] ; then
		CAM1="/dev/cam-csi${CSI}-port1"
		CAM1_ENT="$(cat /sys/class/video4linux/$(readlink ${CAM1})/name)"
	fi
elif [ -L /dev/cam-csi${CSI}-port1 ] && [ "${PORT1}" = true ] ; then
	CAM0="/dev/cam-csi${CSI}-port1"
else
	echo "No cameras found on CSI${CSI}"
	exit 1
fi
CAM0_ENT="$(cat /sys/class/video4linux/$(readlink ${CAM0})/name)"

# Check if we have a phyCAM-L interface connected.
SER_P0="/dev/phycam-serializer-port0-csi${CSI}"
SER_P0_ENT=
SER_P1="/dev/phycam-serializer-port1-csi${CSI}"
SER_P1_ENT=
DESER="/dev/phycam-deserializer-csi${CSI}"
DESER_ENT=

if [ "${PORT0}" = true ]; then
	if [ -L $SER_P0 ]; then
		SER_P0_ENT="$(cat /sys/class/video4linux/$(readlink ${SER_P0})/name)"
	else
		echo "Serializer for port0 not found (CSI${CSI})"; exit 1
	fi
fi

if [ "${PORT1}" = true ]; then
	if [ -L $SER_P1 ]; then
		SER_P1_ENT="$(cat /sys/class/video4linux/$(readlink ${SER_P1})/name)"
	else
		echo "Serializer for port1 not found (CSI${CSI})"; exit 1
	fi
fi

if [ "${PORT0}" = true ] || [ "${PORT1}" = true ]; then
	if [ -L $DESER ] ; then
		DESER_ENT="$(cat /sys/class/video4linux/$(readlink ${DESER})/name)"
	else
		echo "Deserializer not found (CSI${CSI})"; exit 1
	fi
fi

##TODO not supported yet: different sensors; we only get the info from sensor0, sensor1 are assumed to be same!
# Get sensor default values.
case $(echo ${CAM0_ENT} | cut -d" " -f1) in
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
COLOR="$(v4l2-ctl -d ${CAM0} --get-subdev-fmt | \
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

if [ -L /dev/phycam-csi2rx-csi${CSI} ]; then
	CSI_ENT="$(cat /sys/class/video4linux/$(readlink /dev/phycam-csi2rx-csi${CSI})/name)"
else
	echo "CSI device not found" ; exit 1
fi
if [ -L /dev/phycam-mipi-csi${CSI} ]; then
	MIPI_ENT="$(cat /sys/class/video4linux/$(readlink /dev/phycam-mipi-csi${CSI})/name)"
else
	echo "MIPI bridge device not found" ; exit 1
fi

MC_CSI="media-ctl -d /dev/media-csi${CSI}"

echo ""
echo "Setting up MEDIA Pipeline with"
echo "${FMT}/${RES} ${OFFSET}/${FRES} for ${CAM0_ENT}"
echo "========================================================="

echo " Setting up MEDIA Formats:"
echo " -------------------------"
echo "  Sensor:"
echo "   ${MC_CSI} -V \"'${CAM0_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]\""
${MC_CSI} -V "'${CAM0_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]" ${VERBOSE}
if [ -n "${CAM1_ENT}" ] ; then
	echo "   ${MC_CSI} -V \"'${CAM1_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]\""
	${MC_CSI} -V "'${CAM1_ENT}':0[fmt:${FMT}/${RES} ${OFFSET}/${FRES}]" ${VERBOSE}
fi
echo ""

if [ -n "${DESER_ENT}" ] ; then
	echo "  De-/Serializer:"

	VC_P0_DESER="0/0->2/0[1]"
	VC_P1_DESER="1/0->2/1[1]"

	VC_P0_MIPI="0/0->1/0[1]"
	VC_P1_MIPI="0/1->1/1[1]"

	VC_P0_CSI="0/0->1/0[1]"
	VC_P1_CSI="0/1->2/0[1]"

	VC_DESER=
	VC_MIPI=
	VC_CSI=

	if [ "${PORT0}" = true ] && [ "${PORT1}" = true ] ; then
		VC_DESER="${VC_P0_DESER},${VC_P1_DESER}" # comma separation if use both!
		VC_MIPI="${VC_P0_MIPI},${VC_P1_MIPI}"
		VC_CSI="${VC_P0_CSI},${VC_P1_CSI}"
	elif [ "${PORT0}" = true ]; then
		VC_DESER="${VC_P0_DESER}"
		VC_MIPI="${VC_P0_MIPI}"
		VC_CSI="${VC_P0_CSI}"
	elif [ "${PORT1}" = true ]; then
		VC_DESER="${VC_P1_DESER}"
		VC_MIPI="${VC_P1_MIPI}"
		VC_CSI="${VC_P1_CSI}"
	else
		echo "   Deserializer defined, but no port given! Exit ..."
		exit 1
	fi

	echo "   media-ctl -R \"'${DESER_ENT}'[${VC_DESER}]\""
	media-ctl -R "'${DESER_ENT}'[${VC_DESER}]"

	echo "   media-ctl -R \"'${MIPI_ENT}'[${VC_MIPI}]\""
	media-ctl -R "'${MIPI_ENT}'[${VC_MIPI}]"

	echo "   media-ctl -R \"'${CSI_ENT}'[${VC_CSI}]\""
	media-ctl -R "'${CSI_ENT}'[${VC_CSI}]"
	echo ""

	if [ -n "${SER_P0_ENT}" ]; then
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
	if [ -n "${SER_P1_ENT}" ]; then
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
