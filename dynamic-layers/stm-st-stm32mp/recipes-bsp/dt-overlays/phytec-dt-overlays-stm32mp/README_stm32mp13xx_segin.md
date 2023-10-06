Introduction
------------

Device tree overlays are used to enable/disable STM32MP13 phyBOARD-Segin expansion boards and accessories.
DT Overlays are applied on u-boot via boot.scr script.


List of available device tree overlays
--------------------------------------
The following device tree overlays are located in /boot/overlays directory.

# Displays (select only one display interface at a time)
stm32mp135x-phyboard-segin-peb-av-02-lcd.dtbo - LCD with capacitive touchscreen
stm32mp135x-phyboard-segin-peb-av-02-lcd-resistive.dtbo - LCD with resistive touchscreen
stm32mp135x-phyboard-segin-peb-av-01-hdmi.dtbo - if HDMI feature enabled

# Evaluation board
stm32mp13xx-phyboard-segin-peb-eval-01-leds-buttons.dtbo
stm32mp13xx-phyboard-segin-peb-eval-01-jtag.dtbo (disables the SAI
interface to safely connect JTAG when Linux is booted. In thise case,
the Audio codec of Segin board is not functional)

# phyCAM-P
stm32mp135x-phyboard-segin-phycam-vm016.dtbo


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
