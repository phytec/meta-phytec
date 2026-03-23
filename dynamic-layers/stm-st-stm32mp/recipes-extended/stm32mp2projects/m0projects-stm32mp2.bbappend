require mxxprojects-stm32mp2-phytec-common.inc

# Temporary
SRC_URI += "file://fw_cortex_m0.sh"

PROJECTS_LIST_M0 = " \
    STM32MP257F-PHYFLEX/Demonstrations/CM0PLUS_DEMO \
"

# Define default board reference for M0
M0_BOARDS = "STM32MP257F-PHYFLEX"
