#!/bin/sh

XBAR_ROUTINGS=""
MIPI_CSI1_ROUTINGS=""
MIPI_CSI2_ROUTINGS=""
FORMATTER_CSI1_ROUTINGS=""
FORMATTER_CSI2_ROUTINGS=""
DESER_CSI1_ROUTINGS=""
DESER_CSI2_ROUTINGS=""

add_routing() {
	DEVICE="$1"
	XBAR_ROUTE="$2"
	MIPI_ROUTE="$3"
	FORMATTER_ROUTE="$4"
	DESER_ROUTE="$5"

	if [ -L "$DEVICE" ]; then
		case "$DEVICE" in
			*csi1* )
				XBAR_ROUTINGS="${XBAR_ROUTINGS}, ${XBAR_ROUTE}"
				MIPI_CSI1_ROUTINGS="${MIPI_CSI1_ROUTINGS}, ${MIPI_ROUTE}"
				FORMATTER_CSI1_ROUTINGS="${FORMATTER_CSI1_ROUTINGS}, ${FORMATTER_ROUTE}"
				if [ -n "$DESER_ROUTE" ]; then
					DESER_CSI1_ROUTINGS="${DESER_CSI1_ROUTINGS}, ${DESER_ROUTE}"
				fi
				;;
			*csi2* )
				XBAR_ROUTINGS="${XBAR_ROUTINGS}, ${XBAR_ROUTE}"
				MIPI_CSI2_ROUTINGS="${MIPI_CSI2_ROUTINGS}, ${MIPI_ROUTE}"
				FORMATTER_CSI2_ROUTINGS="${FORMATTER_CSI2_ROUTINGS}, ${FORMATTER_ROUTE}"
				if [ -n "$DESER_ROUTE" ]; then
					DESER_CSI2_ROUTINGS="${DESER_CSI2_ROUTINGS}, ${DESER_ROUTE}"
				fi
				;;
		esac
	fi
}

add_routing "/dev/cam-csi1" "2/0->5/0[1]" "0/0->1/0[1]" "0/0->1/0[1]" ""
add_routing "/dev/cam-csi1-port0" "2/0->5/0[1]" "0/0->1/0[1]" "0/0->1/0[1]" "0/0->2/0[1]"
add_routing "/dev/cam-csi1-port1" "2/1->7/0[1]" "0/1->1/1[1]" "0/1->1/1[1]" "1/0->2/1[1]"
add_routing "/dev/cam-csi2" "3/0->9/0[1]" "0/0->1/0[1]" "0/0->1/0[1]" ""
add_routing "/dev/cam-csi2-port0" "3/0->9/0[1]" "0/0->1/0[1]" "0/0->1/0[1]" "0/0->2/0[1]"
add_routing "/dev/cam-csi2-port1" "3/1->11/0[1]" "0/1->1/1[1]" "0/1->1/1[1]" "1/0->2/1[1]"

XBAR_ROUTINGS=${XBAR_ROUTINGS##, }
MIPI_CSI1_ROUTINGS=${MIPI_CSI1_ROUTINGS##, }
MIPI_CSI2_ROUTINGS=${MIPI_CSI2_ROUTINGS##, }
FORMATTER_CSI1_ROUTINGS=${FORMATTER_CSI1_ROUTINGS##, }
FORMATTER_CSI2_ROUTINGS=${FORMATTER_CSI2_ROUTINGS##, }
DESER_CSI1_ROUTINGS="${DESER_CSI1_ROUTINGS##, }"
DESER_CSI2_ROUTINGS="${DESER_CSI2_ROUTINGS##, }"


MC="media-ctl -d /dev/media-isi"
XBAR_ENT="crossbar"
FORMATTER_ENT_CSI1="4ac10000.syscon:formatter@20"
FORMATTER_ENT_CSI2="4ac10000.syscon:formatter@120"
MIPI_ENT_CSI1="csidev-4ad30000.csi"
MIPI_ENT_CSI2="csidev-4ad40000.csi"

DESER_CSI1="/dev/phycam-deserializer-csi1"
DESER_CSI2="/dev/phycam-deserializer-csi2"
DESER_ENT_CSI1=
DESER_ENT_CSI2=

if [ -L $DESER_CSI1 ] ; then
	DESER_ENT_CSI1="$(cat /sys/class/video4linux/$(readlink ${DESER_CSI1})/name)"
fi
if [ -L $DESER_CSI2 ] ; then
	DESER_ENT_CSI2="$(cat /sys/class/video4linux/$(readlink ${DESER_CSI2})/name)"
fi

print_routing() {
	ENT="$1"
	ROUTINGS="$2"

	if [ -n "$ROUTINGS" ]; then
		echo "  ${ENT}:"
		echo "    routes:"
		for ROUTE in ${ROUTINGS} ; do
			echo "      ${ROUTE%%,}"
		done
	fi
}

echo ""
echo "  ISI Stream Routing"
echo "  ------------------"
echo "  The stream routing configuration is only setup once for both CSI1"
echo "  and CSI2, according to the connected cameras."
echo "  The setup looks like this:"

print_routing "${XBAR_ENT}" "${XBAR_ROUTINGS}"
print_routing "${MIPI_ENT_CSI1}" "${MIPI_CSI1_ROUTINGS}"
print_routing "${MIPI_ENT_CSI2}" "${MIPI_CSI2_ROUTINGS}"
print_routing "${FORMATTER_ENT_CSI1}" "${FORMATTER_CSI1_ROUTINGS}"
print_routing "${FORMATTER_ENT_CSI2}" "${FORMATTER_CSI2_ROUTINGS}"
print_routing "${DESER_ENT_CSI1}" "${DESER_CSI1_ROUTINGS}"
print_routing "${DESER_ENT_CSI2}" "${DESER_CSI2_ROUTINGS}"

echo ""
echo "  The aquivalent set routing commands look like this:"
if [ -n "$DESER_CSI1_ROUTINGS" ] ; then
	echo "    ${MC} -R \"'${DESER_ENT_CSI1}'[${DESER_CSI1_ROUTINGS}]\""
fi
if [ -n "$DESER_CSI2_ROUTINGS" ] ; then
	echo "    ${MC} -R \"'${DESER_ENT_CSI2}'[${DESER_CSI2_ROUTINGS}]\""
fi
if [ -n "$MIPI_CSI1_ROUTINGS" ] ; then
	echo "    ${MC} -R \"'${MIPI_ENT_CSI1}'[${MIPI_CSI1_ROUTINGS}]\""
fi
if [ -n "$MIPI_CSI2_ROUTINGS" ] ; then
	echo "    ${MC} -R \"'${MIPI_ENT_CSI2}'[${MIPI_CSI2_ROUTINGS}]\""
fi
if [ -n "$FORMATTER_CSI1_ROUTINGS" ] ; then
	echo "    ${MC} -R \"'${FORMATTER_ENT_CSI1}'[${FORMATTER_CSI1_ROUTINGS}]\""
fi
if [ -n "$FORMATTER_CSI2_ROUTINGS" ] ; then
	echo "    ${MC} -R \"'${FORMATTER_ENT_CSI2}'[${FORMATTER_CSI2_ROUTINGS}]\""
fi
echo "    ${MC} -R \"'${XBAR_ENT}'[${XBAR_ROUTINGS}]\""

if [ ! -d /tmp/setup-routing ]; then
	mkdir /tmp/setup-routing
fi

setup_routing() {
	NAME="$1"
	ENT="$2"
	ROUTINGS="$3"

	if [ ! -f "/tmp/setup-routing/${NAME}" ] && [ -n "${ROUTINGS}" ] ; then
		if ! ${MC} -R "'${ENT}'[${ROUTINGS}]" ; then
			echo "Failed to setup ${NAME} routing!"
			exit 1
		fi

		touch "/tmp/setup-routing/${NAME}"
	fi
}

setup_routing "deserializer-csi1" "${DESER_ENT_CSI1}" "${DESER_CSI1_ROUTINGS}"
setup_routing "deserializer-csi2" "${DESER_ENT_CSI2}" "${DESER_CSI2_ROUTINGS}"
setup_routing "mipi-csi1" "${MIPI_ENT_CSI1}" "${MIPI_CSI1_ROUTINGS}"
setup_routing "mipi-csi2" "${MIPI_ENT_CSI2}" "${MIPI_CSI2_ROUTINGS}"
setup_routing "formatter-csi1" "${FORMATTER_ENT_CSI1}" "${FORMATTER_CSI1_ROUTINGS}"
setup_routing "formatter-csi2" "${FORMATTER_ENT_CSI2}" "${FORMATTER_CSI2_ROUTINGS}"
setup_routing "crossbar" "${XBAR_ENT}" "${XBAR_ROUTINGS}"
