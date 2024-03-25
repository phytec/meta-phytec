play_help() {
        echo "Usage: ./setup-pipeline-dcmipp.sh [-f <format>] [-s <frame size>] [-o <offset>] [-c <sensor size>] [-v]"
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

CAM="/dev/cam-dcmipp"
# Check if phyCAM is detected on DCMIPP
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
DCMIPP_BYTEPROC="/dev/byteproc-dcmipp"
DCMIPP_BYTEPROC_ENT="$(cat /sys/class/video4linux/$(readlink ${DCMIPP_BYTEPROC})/name)"

MC="media-ctl"

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
echo "  DCMIPP Parallel Interface:"
echo "   $MC -V \"'${DCMIPP_PARALLEL_ENT}':0[fmt:${FMT}/${RES}]\""
$MC -V "'${DCMIPP_PARALLEL_ENT}':0[fmt:${FMT}/${RES}]" ${VERBOSE}
echo ""
echo "  DCMIPP ByteProc Interface:"
echo "   $MC -V \"'${DCMIPP_BYTEPROC_ENT}':0[fmt:${FMT}/${RES}]\""
$MC -V "'${DCMIPP_BYTEPROC_ENT}':0[fmt:${FMT}/${RES}]" ${VERBOSE}
echo ""
