FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

GIT_URL = "git://git.phytec.de/STM32CubeMP1"
BRANCH = "1.0.0-phy"
SRC_URI = "\
    ${GIT_URL};branch=${BRANCH} \
    file://Makefile.stm32 \
    file://parse_project_config.py \
    file://st-m4firmware-load-default.sh \
    file://st-m4firmware-load.service \
"
SRCREV = "6f54fa63552de269c2c9baf8cba28c51efdb2548"

PROJECTS_LIST_PHY = " \
	'STM32MP157C-PHY/Examples/ADC/ADC_SingleConversion_TriggerTimer_DMA' \
	'STM32MP157C-PHY/Examples/Cortex/CORTEXM_MPU' \
	'STM32MP157C-PHY/Examples/CRC/CRC_UserDefinedPolynomial' \
	'STM32MP157C-PHY/Examples/CRYP/CRYP_AES_DMA' \
	'STM32MP157C-PHY/Examples/DMA/DMA_FIFOMode' \
	'STM32MP157C-PHY/Examples/GPIO/GPIO_EXTI' \
	'STM32MP157C-PHY/Examples/HASH/HASH_SHA224SHA256_DMA' \
	'STM32MP157C-PHY/Examples/I2C/I2C_TwoBoards_ComIT' \
	'STM32MP157C-PHY/Examples/LPTIM/LPTIM_PulseCounter' \
	'STM32MP157C-PHY/Examples/SPI/SPI_FullDuplex_ComDMA_Master' \
	'STM32MP157C-PHY/Examples/SPI/SPI_FullDuplex_ComDMA_Slave' \
	'STM32MP157C-PHY/Examples/SPI/SPI_FullDuplex_ComIT_Master' \
	'STM32MP157C-PHY/Examples/SPI/SPI_FullDuplex_ComIT_Slave' \
	'STM32MP157C-PHY/Examples/TIM/TIM_DMABurst' \
	'STM32MP157C-PHY/Examples/UART/UART_TwoBoards_ComDMA' \
	'STM32MP157C-PHY/Examples/UART/UART_TwoBoards_ComIT' \
	'STM32MP157C-PHY/Examples/WWDG/WWDG_Example' \
	'STM32MP157C-PHY/Applications/OpenAMP/OpenAMP_raw' \
	'STM32MP157C-PHY/Applications/OpenAMP/OpenAMP_TTY_echo' \
	'STM32MP157C-PHY/Applications/OpenAMP/OpenAMP_TTY_echo_wakeup' \
	'STM32MP157C-PHY/Applications/FreeRTOS/FreeRTOS_ThreadCreation' \
"

PROJECTS_LIST_append = " ${PROJECTS_LIST_PHY}"

