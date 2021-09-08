do_install:append:stm32mpcommon() {
   # Install calibration file
   install -m 0644 ${WORKDIR}/nvram-murata/cyfmac4339-sdio.ZP.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.txt
   install -m 0644 ${WORKDIR}/nvram-murata/cyfmac4339-sdio.ZP.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.${MACHINE}.txt
   install -m 0644 ${WORKDIR}/nvram-murata/cyfmac43430-sdio.1DX.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.${MACHINE}.txt

   # Remove calibration files not used for phycore-stm32mp1
   rm -f ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.st,stm32mp157c-dk2.txt
   rm -f ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.st,stm32mp157f-dk2.txt

   #take newest murata firmware
   install -m 0644 ${WORKDIR}/murata/cyfmac4339-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.bin
}

FILES:${PN}-bcm4339:append:stm32mpcommon = " \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.txt \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.${MACHINE}.txt \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.bin \
"

FILES:${PN}-bcm43430:append:stm32mpcommon = " \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.${MACHINE}.txt \
"

FILES:${PN}-bcm4339:remove:stm32mpcommon = " \
   ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.st,stm32mp157c-dk2.txt \
   ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.st,stm32mp157f-dk2.txt \
"
