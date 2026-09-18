SUMMARY = "Provides machine depended external environment for u-boot on PHYTEC hardware"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://bootenv.txt \
    file://overlays.txt \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${UNPACKDIR}"

BOOTENV_OVERLAYS_APPEND ?= ""

BOOTENV_FILE ?= "bootenv.txt"
BOOTENV_FILE:k3 = "overlays.txt"
BOOTENV_FILE:mx8-generic-bsp = "overlays.txt"
BOOTENV_FILE:mx9-generic-bsp = "overlays.txt"

inherit deploy
do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/${BOOTENV_FILE} ${DEPLOYDIR}

    if [ -n "${FIT_OVERLAY}" ]; then
        # if no overlays are set in the file
        if ! grep -q -E '^(overlays=.*)' "${DEPLOYDIR}/${BOOTENV_FILE}"; then
            echo "overlays=${FIT_OVERLAY}" > "${DEPLOYDIR}/${BOOTENV_FILE}"
        fi

        if ! grep -q -E '^(fit_overlay_conf=.*)' "${DEPLOYDIR}/${BOOTENV_FILE}"; then
            # Prepend "conf-" to each word, then join with "#"
            fit_overlays_conf=""
            fit_conf_prefix="${FIT_CONF_PREFIX}"
            if [ -z "$fit_conf_prefix" ]; then
                fit_conf_prefix="conf-"
            fi
            for f in ${FIT_OVERLAY}; do
                if [ -z "$fit_overlays_conf" ]; then
                    fit_overlays_conf="${fit_conf_prefix}$f"
                else
                    fit_overlays_conf="${fit_overlays_conf}#${fit_conf_prefix}$f"
                fi
            done

            echo "fit_overlay_conf=${fit_overlays_conf}" >> "${DEPLOYDIR}/${BOOTENV_FILE}"
        fi
    fi

    if echo ${KERNEL_IMAGETYPES} | grep -wq "fitImage"; then
        # Replace whitespaces by a single #.
        OVERLAYS_APPEND=$(echo "${BOOTENV_OVERLAYS_APPEND}" | sed -e "s/\s\+/#/g")

        sed -i -e "s/\(overlays=.*\)/\1#${OVERLAYS_APPEND}/" -e "s/\(overlays=#\)/\overlays=/" ${DEPLOYDIR}/${BOOTENV_FILE}

        # Remove trailing whitespaces.
        sed -i -e "s/\ *$//g" ${DEPLOYDIR}/${BOOTENV_FILE}

        # Remove trailing hashtags
        sed -i -e "s/#*$//g" ${DEPLOYDIR}/${BOOTENV_FILE}
    else
        # Replace multiple whitespaces by single one.
        OVERLAYS_APPEND=$(echo "${BOOTENV_OVERLAYS_APPEND}" | sed -e "s/\s\+/ /g")

        sed -i -e "s/\(overlays=.*\)/\1 ${OVERLAYS_APPEND}/" ${DEPLOYDIR}/${BOOTENV_FILE}

        # Remove trailing whitespaces.
        sed -i -e "s/\ *$//g" ${DEPLOYDIR}/${BOOTENV_FILE}
    fi
}
addtask deploy before do_build after do_unpack

COMPATIBLE_MACHINE = "^("
COMPATIBLE_MACHINE .= "mx7-nxp-bsp"
COMPATIBLE_MACHINE .= "|mx8m-generic-bsp"
COMPATIBLE_MACHINE .= "|mx8x-generic-bsp"
COMPATIBLE_MACHINE .= "|mx9-generic-bsp"
COMPATIBLE_MACHINE .= "|k3"
COMPATIBLE_MACHINE .= "|stm32mpcommon"
COMPATIBLE_MACHINE .= ")$"
