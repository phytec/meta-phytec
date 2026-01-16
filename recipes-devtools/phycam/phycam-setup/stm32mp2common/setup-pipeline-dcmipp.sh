play_help() {
        echo "Usage: ./setup-pipeline-dcmipp.sh [-f <format>] [-s <frame size>] [-o <offset>] [-c <sensor size>] [-v] AUX/DUMP/ISP"
}

OPTIONS="hf:s:o:c:v"
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
                v ) VERBOSE="-v";;
                h  ) display_help; exit;;
                \? ) echo "Unknown option: -$OPTARG" >&2; exit 1;;
                :  ) echo "Missing option argument for -$OPTARG" >&2; exit 1;;
                *  ) echo "Unimplemented option: -$OPTARG" >&2; exit 1;;
        esac
done

eval "last=\${$#}"
if [ "$last" = "AUX" ]; then
        PIPE="AUX"
elif [ "$last" = "DUMP" ]; then
        PIPE="DUMP"
elif [ "$last" = "ISP" ]; then
        PIPE="ISP"
else
		echo "Error: last parameter need to be AUX, DUMP or ISP "
		exit 1
fi

# Check if phyCAM is detected on DCMIPP
CAM="/dev/cam-dcmipp"
if ! [ -e ${CAM} ] ; then
        echo "No camera found on DCMIPP"
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


DCMIPP_PARALLEL="/dev/parallel-dcmipp"
DCMIPP_PARALLEL_ENT="$(cat /sys/class/video4linux/$(readlink ${DCMIPP_PARALLEL})/name)"
CSI="/dev/csi"
CSI_ENT="$(cat /sys/class/video4linux/$(readlink ${CSI})/name)"
MAIN_ISP="/dev/dcmipp-main-isp"
MAIN_ISP_ENT="$(cat /sys/class/video4linux/$(readlink ${MAIN_ISP})/name)"
DCMIPP_MAIN_POSTPROC="/dev/dcmipp-main-postproc"
DCMIPP_MAIN_POSTPROC_ENT="$(cat /sys/class/video4linux/$(readlink ${DCMIPP_MAIN_POSTPROC})/name)"
DCMIPP_AUX_POSTPROC="/dev/dcmipp-aux-postproc"
DCMIPP_AUX_POSTPROC_ENT="$(cat /sys/class/video4linux/$(readlink ${DCMIPP_AUX_POSTPROC})/name)"
DCMIPP_DUMP_POSTPROC="/dev/dcmipp-dump-postproc"
DCMIPP_DUMP_POSTPROC_ENT="$(cat /sys/class/video4linux/$(readlink ${DCMIPP_DUMP_POSTPROC})/name)"

MC="media-ctl"

echo ""
echo "Setting up MEDIA Pipeline with"
echo "${FMT}/${RES} ${OFFSET}/${FRES} for ${CAM_ENT}"
echo "========================================================="

echo " Setting up MEDIA Formats:"
echo " -------------------------"
echo "  Sensor:"
echo "   $MC -d platform:48030000.dcmipp -V \"'${CAM_ENT}':0[fmt:${FMT}/${RES}]\""
$MC -d platform:48030000.dcmipp -V "'${CAM_ENT}':0[fmt:${FMT}/${RES}]"
echo ""
echo "  CSI:"
echo "   $MC -d platform:48030000.dcmipp -l \"'${CSI_ENT}':1->'${DCMIPP_PARALLEL_ENT}':0[1]\""
echo "   $MC -d platform:48030000.dcmipp -V \"'${CSI_ENT}':1[fmt:${FMT}/${RES}]\""
$MC -d platform:48030000.dcmipp -l "'${CSI_ENT}':1->'${DCMIPP_PARALLEL_ENT}':0[1]"
$MC -d platform:48030000.dcmipp -V "'${CSI_ENT}':1[fmt:${FMT}/${RES}]"
echo ""
echo "  DCMIPP Input:"
if [ "$PIPE" = "AUX" ]; then
        echo "   $MC -d platform:48030000.dcmipp -l \"'${DCMIPP_PARALLEL_ENT}':3->'${DCMIPP_AUX_POSTPROC_ENT}':0[1]\""
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_PARALLEL_ENT}':3[fmt:${FMT}/${RES} field:none]\""
        $MC -d platform:48030000.dcmipp -l "'${DCMIPP_PARALLEL_ENT}':3->'${DCMIPP_AUX_POSTPROC_ENT}':0[1]"
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_PARALLEL_ENT}':3[fmt:${FMT}/${RES} field:none]"
        echo ""
        echo "  DCMIPP aux postproc:"
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_AUX_POSTPROC_ENT}':0[compose:(0,0)/${RES}]\""
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_AUX_POSTPROC_ENT}':1[fmt:${FMT}/${RES}]\""
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_AUX_POSTPROC_ENT}':0[compose:(0,0)/${RES}]"
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_AUX_POSTPROC_ENT}':1[fmt:${FMT}/${RES}]"
elif [ "$PIPE" = "DUMP" ]; then
        echo "   $MC -d platform:48030000.dcmipp -l \"'${DCMIPP_PARALLEL_ENT}':1->'${DCMIPP_DUMP_POSTPROC_ENT}':0[1]\""
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_PARALLEL_ENT}':1[fmt:${FMT}/${RES} field:none]\""
        $MC -d platform:48030000.dcmipp -l "'${DCMIPP_PARALLEL_ENT}':1->'${DCMIPP_DUMP_POSTPROC_ENT}':0[1]"
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_PARALLEL_ENT}':1[fmt:${FMT}/${RES} field:none]"
        echo ""
        echo "  DCMIPP dump postproc:"
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_DUMP_POSTPROC_ENT}':0[compose:(0,0)/${RES}]\""
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_DUMP_POSTPROC_ENT}':1[fmt:${FMT}/${RES}]\""
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_DUMP_POSTPROC_ENT}':0[compose:(0,0)/${RES}]"
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_DUMP_POSTPROC_ENT}':1[fmt:${FMT}/${RES}]"
elif [ "$PIPE" = "ISP" ]; then
        echo "   $MC -d platform:48030000.dcmipp -l \"'${DCMIPP_PARALLEL_ENT}':2->'${MAIN_ISP_ENT}':0[1]\""
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_PARALLEL_ENT}':2[fmt:${FMT}/${RES} field:none]\""
        $MC -d platform:48030000.dcmipp -l "'${DCMIPP_PARALLEL_ENT}':2->'${MAIN_ISP_ENT}':0[1]"
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_PARALLEL_ENT}':2[fmt:${FMT}/${RES} field:none]"
        echo ""
        echo "  DCMIPP main ISP"
        echo "   $MC -d platform:48030000.dcmipp -V \"'${MAIN_ISP_ENT}':1[fmt:${FMT}/${RES} field:none]\""
        $MC -d platform:48030000.dcmipp -V "'${MAIN_ISP_ENT}':1[fmt:${FMT}/${RES} field:none]"
        echo ""
        echo "  DCMIPP main postproc:"
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_MAIN_POSTPROC_ENT}':0[compose:(0,0)/${RES}]\""
        echo "   $MC -d platform:48030000.dcmipp -V \"'${DCMIPP_MAIN_POSTPROC_ENT}':1[fmt:${FMT}/${RES}]\""
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_MAIN_POSTPROC_ENT}':0[compose:(0,0)/${RES}]"
        $MC -d platform:48030000.dcmipp -V "'${DCMIPP_MAIN_POSTPROC_ENT}':1[fmt:${FMT}/${RES}]"
fi
echo ""
