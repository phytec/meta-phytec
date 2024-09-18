#!/bin/sh

display_help() {
	echo "Usage: ./isp-select-mode-csi2.sh [-m <mode>] [-l <lens>] [-p <phycam-l port] [-M] [-L]"
}

display_modes() {
	case $1 in
		ar0144)
			echo "Available Modes for VM-016:"
			echo "---------------------------"
			echo " 0: 1280x720 HD"
			echo " 1: 1280x800 Full"
			echo ""
			;;
		ar0234)
			echo "Available Modes for VM-020:"
			echo "---------------------------"
			echo " 0: 1280x720 HD"
			echo " 1: 1920x1080 FullHD"
			echo " 2: 1920x1200 Full"
			echo ""
			;;
		ar0521)
			echo "Available Modes for VM-017:"
			echo "---------------------------"
			echo " 0: 1280x720 HD"
			echo " 1: 1920x1080 FullHD"
			echo " 2: 2592x1944 Full"
			echo ""
			;;
		* ) echo "Unknown camera" ; exit 6
	esac
	exit 0
}

display_lenses() {
	case $1 in
		ar0144)
			echo "Available Lens Configurations for VM-016:"
			echo "-----------------------------------------"
			echo " AO082 (default)"
			echo " AO086"
			echo ""
			;;
		ar0234)
			echo "Available Lens Configurations for VM-020:"
			echo "-----------------------------------------"
			echo " AO082 (default)"
			echo " AO070.A1"
			echo ""
			;;
		ar0521)
			echo "Available Lens Configurations for VM-017:"
			echo "-----------------------------------------"
			echo " AO062 (default)"
			echo " AO070.A1"
			echo ""
			;;
		* ) echo "Unknown camera" ; exit 6
	esac
	exit 0
}

OPTIONS="hLMm:l:p:"
MODE=
LENS=
PORT=0
LIST_MODES=0
LIST_LENSES=0

while getopts $OPTIONS option
do
	case $option in
		m ) MODE=$OPTARG;;
		l ) LENS=$OPTARG;;
		p ) PORT=$OPTARG;;
		L ) LIST_LENSES=1;;
		M ) LIST_MODES=1;;
		h  ) display_help; exit;;
		\? ) echo "Unknown option: -$OPTARG" >&2; exit 1;;
		:  ) echo "Missing option argument for -$OPTARG" >&2; exit 1;;
		*  ) echo "Unimplemented option: -$OPTARG" >&2; exit 1;;
	esac
done

if [ ! -e /dev/isp-csi2 ] ; then
	echo "No ISP configured on CSI2"
	exit 1
fi

if [ -L /dev/cam-csi2-port0 ] && [ -L /dev/cam-csi2-port1 ] ; then
	echo "Dual port phyCAM-L not supported with ISP"
	exit 1
fi

# Select the correct camera subdevice. Can be phyCAM-M or phyCAM-L (Port 0 or 1).
if [ -L /dev/cam-csi2 ] ; then
	CAM="/dev/cam-csi2"
elif [ -L /dev/cam-csi2-port0 ] && [ "$PORT" = "0" ] ; then
	CAM="/dev/cam-csi2-port0"
elif [ -L /dev/cam-csi2-port1 ] && [ "$PORT" = "1" ] ; then
	CAM="/dev/cam-csi2-port1"
else
	echo "No camera found on CSI2"
	exit 1
fi

SENSOR_NAME="$(cat /sys/class/video4linux/$(readlink ${CAM})/name | cut -d" " -f1)"
COLOR="$(v4l2-ctl -d ${CAM} --get-subdev-fmt 0 | \
	grep "Mediabus Code" | sed 's/.*BUS_FMT_\([A-Z]*\).*/\1/g')"

if [ $COLOR = "Y" ]; then
	COLOR="bw"
else
	COLOR="col"
fi

if [ $LIST_MODES = 1 ] ; then
	display_modes ${SENSOR_NAME}
fi

if [ $LIST_LENSES = 1 ] ; then
	display_lenses ${SENSOR_NAME}
fi

if [ -z $LENS ] ; then
	case ${SENSOR_NAME} in
		ar0144) LENS="AO082" ;;
		ar0234) LENS="AO082" ;;
		ar0521) LENS="AO062" ;;
		* ) echo "Unknown camera" ; exit 6
	esac
else
	case ${SENSOR_NAME} in
		ar0144 )
			if [ ! "${LENS}" = "AO082" ] && [ ! "${LENS}" = "AO086" ] ; then
				echo -e "Uknown Lens: ${LENS}\n" ; display_lenses ${SENSOR_NAME}
			fi
			;;
		ar0234 )
			if [ ! "${LENS}" = "AO082" ] && [ ! "${LENS}" = "AO070.A1" ] ; then
				echo -e "Uknown Lens: ${LENS}\n" ; display_lenses ${SENSOR_NAME}
			fi
			;;
		ar0521 )
			if [ ! "${LENS}" = "AO062" ] && [ ! "${LENS}" = "AO070.A1" ] ; then
				echo -e "Uknown Lens: ${LENS}\n" ; display_lenses ${SENSOR_NAME}
			fi
			;;
		* ) echo "Unknown camera" ; exit 6
	esac
fi

if [ -z $MODE ] ; then
	case ${SENSOR_NAME} in
		ar0144) MODE=0 ;;
		ar0234) MODE=1 ;;
		ar0521) MODE=1 ;;
		* ) echo "Unknown camera" ; exit 6
	esac
else
	case ${SENSOR_NAME} in
		ar0144 )
			if [ ! "${MODE}" = "0" ] && [ ! "${MODE}" = "1" ] ; then
				echo -e "Uknown Mode: ${MODE}\n" ; display_modes ${SENSOR_NAME}
			fi
			;;
		ar0234 )
			if [ ! "${MODE}" = "0" ] && [ ! "${MODE}" = "1" ] && [ ! "${MODE}" = "2" ] ; then
				echo -e "Uknown Mode: ${MODE}\n" ; display_modes ${SENSOR_NAME}
			fi
			;;
		ar0521 )
			if [ ! "${MODE}" = "0" ] && [ ! "${MODE}" = "1" ] && [ ! "${MODE}" = "2" ] ; then
				echo -e "Uknown Mode: ${MODE}\n" ; display_modes ${SENSOR_NAME}
			fi
			;;
		* ) echo "Unknown camera" ; exit 6
	esac
fi

ISP_SENSOR_FILE="/opt/imx8-isp/bin/Sensor1_Entry.cfg"

# Create Sensor0_Entry.cfg link according to connected Sensor and Color
ln -sf ${ISP_SENSOR_FILE}.${SENSOR_NAME}.${LENS}.${COLOR} ${ISP_SENSOR_FILE}

sed -i "s/^mode= .*/mode= ${MODE}/" "$(readlink -f "${ISP_SENSOR_FILE}")"
XML_FILE=$(grep -A1 "mode\.${MODE}" ${ISP_SENSOR_FILE} | grep -o "VM-.*\.xml")
JSON_FILE=$(grep -A2 "mode\.${MODE}" ${ISP_SENSOR_FILE} | grep -o "VM-.*\.json")

echo "Selecting ${ISP_SENSOR_FILE}.${SENSOR_NAME}.${LENS}.${COLOR} as active Sensor1_Entry.cfg"
echo "Mode: ${MODE}"
echo "XML: ${XML_FILE}"
echo "JSON: ${JSON_FILE}"
