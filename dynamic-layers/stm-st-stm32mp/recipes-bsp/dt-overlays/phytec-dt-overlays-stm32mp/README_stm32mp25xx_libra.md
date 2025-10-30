Introduction
------------

Device tree overlays are used to enable/disable phyFLEX-STM32MP25x Libra expansion boards and accessories.

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
