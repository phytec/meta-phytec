require mxxprojects-stm32mp2-phytec-common.inc

# Default service for systemd
SRC_URI += "file://st-m33firmware-load-default.sh"
SRC_URI += "file://st-m33firmware-load.service"
SRC_URI += "file://shutdown-stm32mp2-m33.sh"
# Temporary
SRC_URI += "file://fw_cortex_m33.sh"
