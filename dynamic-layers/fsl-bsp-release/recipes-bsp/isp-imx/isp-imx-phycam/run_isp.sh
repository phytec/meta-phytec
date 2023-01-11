#!/bin/sh

AR0144_LENS="AO082"
AR0521_LENS="AO062"

if ! [ -e /dev/cam-csi1 ] && ! [ -e /dev/cam-csi2 ]; then
	echo "No camera"
	exit 6
fi

if ! cat /proc/modules | grep -q vvcam_video; then
	modprobe vvcam-video
	sleep 1
fi

if [ -e /dev/isp-csi1 ] && [ -e /dev/isp-csi2 ]; then
	RUN_OPT="DUAL_CAMERA"
	echo $RUN_OPT
elif [ -e /dev/isp-csi1 ]; then
	RUN_OPT="CAMERA0"
	echo $RUN_OPT
elif [ -e /dev/isp-csi2 ]; then
	RUN_OPT="CAMERA1"
	echo $RUN_OPT
else
	echo "No ISP found"
	exit 6
fi

# Create Sensor0_Entry.cfg link according to connected Sensor and Color
if [ -e /dev/cam-csi1 ]; then
	CAM="$(cat /sys/class/video4linux/$(readlink /dev/cam-csi1)/name | \
		cut -d" " -f1)"
	COLOR="$(v4l2-ctl -d /dev/cam-csi1 --get-subdev-fmt 0 | \
		grep "Mediabus Code" | sed 's/.*BUS_FMT_\([A-Z]*\).*/\1/g')"

	if [ $COLOR = "Y" ]; then
		COLOR="bw"
	else
		COLOR="col"
	fi

	case ${CAM} in
		ar0144) LENS=${AR0144_LENS} ;;
		ar0521) LENS=${AR0521_LENS} ;;
		* ) echo "Unknown camera" ; exit 6
	esac

	ln -sf /opt/imx8-isp/bin/Sensor0_Entry.cfg.${CAM}.${LENS}.${COLOR} \
	       /opt/imx8-isp/bin/Sensor0_Entry.cfg
fi

# Create Sensor1_Entry.cfg link according to connected Sensor and Color
if [ -e /dev/cam-csi2 ]; then
	CAM="$(cat /sys/class/video4linux/$(readlink /dev/cam-csi2)/name | \
		cut -d" " -f1)"
	COLOR="$(v4l2-ctl -d /dev/cam-csi2 --get-subdev-fmt 0 | \
		grep "Mediabus Code" | sed 's/.*BUS_FMT_\([A-Z]*\).*/\1/g')"

	if [ $COLOR = "Y" ]; then
		COLOR="bw"
	else
		COLOR="col"
	fi

	case ${CAM} in
		ar0144) LENS=${AR0144_LENS} ;;
		ar0521) LENS=${AR0521_LENS} ;;
		* ) echo "Unknown camera" ; exit 6
	esac

	ln -sf /opt/imx8-isp/bin/Sensor1_Entry.cfg.${CAM}.${LENS}.${COLOR} \
	       /opt/imx8-isp/bin/Sensor1_Entry.cfg
fi

ISP_LOG_LEVEL=4 /opt/imx8-isp/bin/isp_media_server ${RUN_OPT}
