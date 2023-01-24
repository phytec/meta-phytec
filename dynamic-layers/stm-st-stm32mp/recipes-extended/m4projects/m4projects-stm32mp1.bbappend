FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

GIT_URL = "git://git.phytec.de/STM32CubeMP1"
BRANCH = "1.6.0-phy"
SRCREV = "1f1b2171f5bf0f060cebdd6c1b8bea2d19957413"

SRC_URI = "${GIT_URL};branch=${BRANCH} \
"

# M4 firwmare build file
SRC_URI += "file://Makefile.stm32 \
            file://parse_project_config.py \
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
	STM32MP15-phyBOARD-Sargas/Examples/ADC/ADC_SingleConversion_Polling \
	STM32MP15-phyBOARD-Sargas/Examples/ADC/ADC_SingleConversion_TriggerTimer_DMA \
	STM32MP15-phyBOARD-Sargas/Examples/GPIO/GPIO_EXTI \
	STM32MP15-phyBOARD-Sargas/Examples/UART/UART_Receive_Transmit_Console \
	STM32MP15-phyBOARD-Sargas/Applications/OpenAMP/OpenAMP_raw \
	STM32MP15-phyBOARD-Sargas/Applications/OpenAMP/OpenAMP_TTY_echo \
        ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'STM32MP15-phyBOARD-Sargas/Demonstrations/AI_Character_Recognition', '', d)} \
"

# Default Project list when using specific config for STM32CubeMX generated project
PROJECTS_LIST:stm32mpcommonmx = " \
	STM32MP15-phyBOARD-Sargas/Examples/ADC/ADC_SingleConversion_Polling \
	STM32MP15-phyBOARD-Sargas/Examples/ADC/ADC_SingleConversion_TriggerTimer_DMA \
	STM32MP15-phyBOARD-Sargas/Examples/GPIO/GPIO_EXTI \
	STM32MP15-phyBOARD-Sargas/Examples/UART/UART_Receive_Transmit_Console \
	STM32MP15-phyBOARD-Sargas/Applications/OpenAMP/OpenAMP_raw \
	STM32MP15-phyBOARD-Sargas/Applications/OpenAMP/OpenAMP_TTY_echo \
        ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'STM32MP15-phyBOARD-Sargas/Demonstrations/AI_Character_Recognition', '', d)} \
"
