require mxxprojects-stm32mp2-phytec-common.inc

# Default service for systemd
SRC_URI += "file://st-m33firmware-load-default.sh"
SRC_URI += "file://st-m33firmware-load.service"
SRC_URI += "file://shutdown-stm32mp2-m33.sh"
# Temporary
SRC_URI += "file://fw_cortex_m33.sh"

# Define default boards reference for M33 examples
M33_BOARDS = "STM32MP257F-PHYFLEX"

# Project list compatible with the boards reference
PROJECTS_LIST_M33 = " \
        STM32MP257F-PHYFLEX/Applications/OpenAMP/OpenAMP_TTY_echo \
        STM32MP257F-PHYFLEX/Examples/GPIO/GPIO_EXTI \
"

# WARNING: You MUST put only one project on DEFAULT_COPRO_FIRMWARE per board
# If there is several project defined for the same board while you MUST have issue at runtime
# (not the correct project could be executed).
DEFAULT_COPRO_FIRMWARE = ""
