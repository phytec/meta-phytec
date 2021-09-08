Introduction
------------

Device tree overlays are used to enable/disable phyBOARD-Sargas expansion boards and accessories.
DT Overlays are applied on u-boot via boot.scr script.


List of available device tree overlays
--------------------------------------
The following device tree overlays are located in /boot/overlays directory.

# Displays (select only one display interface at a time)
phyboard-stm32mp1-peb-av02-lcd.dtbo
phyboard-stm32mp1-peb-av01-hdmi.dtbo - if HDMI feature enabled
phyboard-stm32mp1-dsi-lcd-mb1407.dtbo - if MIPI-DSI feature enabled
phyboard-stm32mp1-dsi-rpi-official-display.dtbo - if MIPI-DSI feature enabled

# Configurations examples for the specific connectors
phyboard-stm32mp1-pi-hat-extension.dtbo
phyboard-stm32mp1-uno-r3-extension.dtbo
phyboard-stm32mp1-motor-control.dtbo - not compatible with PEB-AV01/02
phyboard-stm32mp1-motor-control-m4.dtbo - not compatible with PEB-AV01/02

# Wireless expansions
phyboard-stm32mp1-peb-wlbt-05-wlan.dtbo - if WIFI feature enabled
phyboard-stm32mp1-peb-wlbt-05-bluetooth-usart3.dtbo - if Bluetooth feature enabled - no CAN transceiver and no 2nd debug FTDI available
phyboard-stm32mp1-peb-wlbt-05-bluetooth-usart1.dtbo - if Bluetooth feature enabled - no RS232 transceiver available
phyboard-stm32mp1-pi-hat-redbear.dtbo - if WIFI or Bluetooth feature enabled

# phyCAM-P
phyboard-stm32mp1-phycam-vm010-bw.dtbo          - phyCAM-P VM-010 B&W connected with 8bits data bus
phyboard-stm32mp1-phycam-vm010-col.dtbo         - phyCAM-P VM-010 COL connected with 8bits data bus
phyboard-stm32mp1-phycam-vm010-bw-10bits.dtbo   - phyCAM-P VM-010 B&W connected with 10bits data bus
phyboard-stm32mp1-phycam-vm010-col-10bits.dtbo  - phyCAM-P VM-010 COL connected with 10bits data bus

# DT overlay to enable RS485 at boot time
phyboard-stm32mp1-rs485.dtbo


How to apply Devicetree Overlays
--------------------------------
- Edit the /boot/overlays/overlays.txt file to specify the DT overlays to apply.
- Syntax of overlays.txt:
overlay=aa bb cc dd
- aa, bb, cc, dd correspond to a device tree name file (without extension)
present on /overlays/ directory
/overlays/aa.dtbo
/overlays/bb.dtbo
/overlays/cc.dtbo
/overlays/dd.dtbo

Device tree overlays to apply can also be set on u-boot with the following command:
- setenv overlay 'aa bb cc'
- saveenv

This will create the "overlay" u-boot env variable and in this case,
the content of /boot/overlays/overlays.txt will be ignored.
To use the overlays.txt file instead, remove the "overlay" env var with following command:
- setenv overlay
