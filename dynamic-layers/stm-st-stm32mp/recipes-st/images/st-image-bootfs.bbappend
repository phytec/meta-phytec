# Add specific package for device tree overlay:
PACKAGE_INSTALL += " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', 'phytec-dt-overlays-stm32mp-imagebootfs', '', d)} \
"
