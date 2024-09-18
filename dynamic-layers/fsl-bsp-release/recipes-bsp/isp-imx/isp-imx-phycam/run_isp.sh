#!/bin/sh

if ! [ -e /dev/cam-csi1 ] && \
   ! [ -e /dev/cam-csi2 ] && \
   ! [ -e /dev/cam-csi1-port0 ] && \
   ! [ -e /dev/cam-csi1-port1 ] && \
   ! [ -e /dev/cam-csi2-port0 ] && \
   ! [ -e /dev/cam-csi2-port1 ]; then
	echo "No camera"
	exit 1
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
if [ -e /dev/cam-csi1 ] || \
   [ -e /dev/cam-csi1-port0 ] || \
   [ -e /dev/cam-csi1-port1 ]; then

	if [ -e /dev/cam-csi1 ]; then
		isp-mode-select-csi1
	elif [ -e /dev/cam-csi1-port0 ] && ! [ -e /dev/cam-csi1-port1 ]; then
		isp-mode-select-csi1 -p 0
		setup-pipeline-csi1 -p 0
	elif [ -e /dev/cam-csi1-port1 ] && ! [ -e /dev/cam-csi1-port0 ]; then
		isp-mode-select-csi1 -p 1
		setup-pipeline-csi1 -p 1
	else
		echo "No valid configuration"
		echo "Dual port phyCAM-L not supported with ISP"
		exit 6
	fi
fi

# Create Sensor1_Entry.cfg link according to connected Sensor and Color
if [ -e /dev/cam-csi2 ] || \
   [ -e /dev/cam-csi2-port0 ] || \
   [ -e /dev/cam-csi2-port1 ]; then

	if [ -e /dev/cam-csi2 ]; then
		isp-mode-select-csi2
	elif [ -e /dev/cam-csi2-port0 ] && ! [ -e /dev/cam-csi2-port1 ]; then
		isp-mode-select-csi2 -p 0
		setup-pipeline-csi2 -p 0
	elif [ -e /dev/cam-csi2-port1 ] && ! [ -e /dev/cam-csi2-port0 ]; then
		isp-mode-select-csi2 -p 1
		setup-pipeline-csi2 -p 1
	else
		echo "No valid configuration"
		echo "Dual port phyCAM-L not supported with ISP"
		exit 6
	fi
fi

ISP_LOG_LEVEL=4 /opt/imx8-isp/bin/isp_media_server ${RUN_OPT}
