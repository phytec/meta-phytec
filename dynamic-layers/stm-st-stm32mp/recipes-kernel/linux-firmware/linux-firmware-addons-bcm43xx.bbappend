# Install the BRCM 2AE firmware version by default
FIRMWARE_STM32MP215F_DK_BRCM_MODULE_2AE = "1"
FIRMWARE_STM32MP215F_DK_BRCM_MODULE_2BZ = "0"

do_install:append:stm32mp2-phyflex-libra-rdk() {

    install -d ${D}${nonarch_base_libdir}/firmware/brcm/

    # Add symlinks for newest kernel compatibility
    cd ${D}${nonarch_base_libdir}/firmware/brcm/
    ln -sf brcmfmac4373-sdio.bin brcmfmac4373-sdio.phytec,stm32mp257f-phyFLEX-libra-rdk.bin
    ln -sf brcmfmac4373-sdio.txt brcmfmac4373-sdio.phytec,stm32mp257f-phyFLEX-libra-rdk.txt
    ln -sf BCM4373A0.hcd BCM.phytec,stm32mp257f-phyFLEX-libra-rdk.hcd
}
