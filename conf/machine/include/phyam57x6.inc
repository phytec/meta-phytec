# TI AM57x6 SoCs without display/graphics support
include conf/machine/include/phyam57xx.inc

SOC_FAMILY:append = ":am57x6"

# Remove machine features
MACHINE_FEATURES:remove = "gpu mmip screen resistivetouch gc320"

QT_QPA_PLATFORM = ""
