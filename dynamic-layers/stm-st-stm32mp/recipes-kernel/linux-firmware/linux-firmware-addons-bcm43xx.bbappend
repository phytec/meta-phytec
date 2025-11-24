do_install:append:stm32mp25x-libra() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm/

    # 4373
    install -m 644 ${S}/LICENCE.cypress ${D}${nonarch_base_libdir}/firmware/LICENCE.cypress_bcm4373
    install -m 644 ${S}/BCM4373A0_001.001.025.0103.0155.FCC.CE.2AE.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4373A0.hcd
}
