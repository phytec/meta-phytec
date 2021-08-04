FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

GIT_URL = "git://git.phytec.de/STM32CubeMP1"
BRANCH = "1.3.0-phy"
SRCREV = "086c5c38e2cae29dbebe338ff58de5af669d6762"

SRC_URI = "${GIT_URL};branch=${BRANCH} \
"

# M4 firwmare build file
SRC_URI += "file://Makefile.stm32 \
            file://parse_project_config.py \
            file://parse_project_config_STM32CubeIDE.py \
"

# Default service for systemd
SRC_URI += "file://st-m4firmware-load-default.sh \
            file://st-m4firmware-load.service \
            file://shutdown-stm32mp1-m4.sh \
"

# Define default boards reference for M4 examples
M4_BOARDS ?= "STM32MP15-phyBOARD-Sargas"

# Project list compatible with the boards reference
PROJECTS_LIST = " \
	STM32MP15-phyBOARD-Sargas/Examples/GPIO/GPIO_EXTI \
	STM32MP15-phyBOARD-Sargas/Examples/UART/UART_Receive_Transmit_Console \
	STM32MP15-phyBOARD-Sargas/Applications/OpenAMP/OpenAMP_raw \
	STM32MP15-phyBOARD-Sargas/Applications/OpenAMP/OpenAMP_TTY_echo \
        ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'STM32MP15-phyBOARD-Sargas/Demonstrations/AI_Character_Recognition', '', d)} \
"

# Define the M4 projects format (IDE)
# Supported IDE projects by this recipe are "STM32CubeIDE" or "SW4STM32"
PROJECTS_IDE_TYPE = "STM32CubeIDE"
#PROJECTS_IDE_TYPE = "SW4STM32"

do_compile() {
    # Compile M4 firmwares listed in bb file.
    for BIN_MACHINE in ${M4_BOARDS}; do
        bbnote "Compile M4 project for : ${BIN_MACHINE}"
        for project in ${PROJECTS_LIST} ; do
            bbnote "Parsing M4 project : ${project}"
            if [ "$(echo ${project} | cut -d'/' -f1)" = "${BIN_MACHINE}" ]; then
                bbnote "Selected M4 project : ${project}"
                unset LDFLAGS CFLAGS CPPFLAGS CFLAGS_ASM

                BIN_NAME=$(basename ${project})
                PROJECT_DIR=${B}/${project}
                if [ ${PROJECTS_IDE_TYPE} = "STM32CubeIDE" ]; then
                    PROJECT_APP="${S}/Projects/${project}/STM32CubeIDE/CM4"
                elif [ ${PROJECTS_IDE_TYPE} = "SW4STM32" ]; then
                    PROJECT_APP="${S}/Projects/${project}/SW4STM32/${BIN_NAME}"
                fi

                bbnote "BIN_NAME     : ${BIN_NAME}"
                bbnote "PROJECT_DIR  : ${PROJECT_DIR}"
                bbnote "PROJECT_APP  : ${PROJECT_APP}"
                bbnote "BUILD_CONFIG : ${BUILD_CONFIG}"
                bbnote "CPU_TYPE     : ${CPU_TYPE}"
                bbnote "SOURCE       : ${S}"

                mkdir -p ${PROJECT_DIR}/out/${BUILD_CONFIG}

                bbnote "Parsing M4 project to get file list and build flags for ${project}"
                if [ ${PROJECTS_IDE_TYPE} = "STM32CubeIDE" ]; then
                    ${PYTHON} ${WORKDIR}/parse_project_config_STM32CubeIDE.py "${PROJECT_APP}" "${BUILD_CONFIG}" "${PROJECT_DIR}"
                elif [ ${PROJECTS_IDE_TYPE} = "SW4STM32" ]; then
                    ${PYTHON} ${WORKDIR}/parse_project_config.py "${PROJECT_APP}" "${BUILD_CONFIG}" "${PROJECT_DIR}"
                fi
                
                bbnote "Cleaning M4 project : ${project}"
                oe_runmake -f ${WORKDIR}/Makefile.stm32 BIN_NAME=${BIN_NAME} PROJECT_DIR=${PROJECT_DIR} clean
                bbnote "Building M4 project : ${project}"
                oe_runmake -f ${WORKDIR}/Makefile.stm32 BIN_NAME=${BIN_NAME} PROJECT_DIR=${PROJECT_DIR} all
            fi
        done
    done
}
