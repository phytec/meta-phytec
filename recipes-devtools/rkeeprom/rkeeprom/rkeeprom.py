#!/usr/bin/python2.7
# Copyright 2017 Phytec Messtechnik GmbH
# Author: Wadim Egorov <w.egorov@phytec.de>

import smbus
import time
import argparse

# Set the I2C bus. The config EEPROM is always on the first bus, i2c0.
i2c = smbus.SMBus(0)

# Set current adress of eeprom
def at24c32_set_addr(devaddr, addr):
	upperbyte = (addr & 0b1111111100000000) >> 8
	lowerbyte = addr & 0b0000000011111111
	i2c.write_i2c_block_data(devaddr, upperbyte, [lowerbyte])
	time.sleep(0.1)

# Valid addr for RAM: 0 - 4096
def at24c32_get_ram(devaddr, addr):
	at24c32_set_addr(devaddr, addr)
	return i2c.read_byte(devaddr)

# Valid addr for RAM: 0 - 4096
def at24c32_set_ram(devaddr, addr, val):
	upperbyte = (addr & 0b1111111100000000) >> 8
	lowerbyte = addr & 0b0000000011111111
	i2c.write_i2c_block_data(devaddr, upperbyte, [lowerbyte, val])
	time.sleep(0.1)

# Lock EEPROM permanently
def lock_eeprom(devaddr):
	at24c32_set_ram(devaddr, 0x400, 2)

# EEPROM layout version
api_version = 0
# Module version
mod_version = 0

# EEPROM coding
coding_ddr = {
	0: "256MB-DDR3-RAM 1 Bank",
	1: "512MB-DDR3-RAM 1 Bank",
	2: "512MB-DDR3-RAM 2 Bank",
	3: "1GB-DDR3-RAM 1 Bank",
	4: "1GB-DDR3-RAM 2 Banks",
	5: "2GB-DDR3-RAM 1 Bank",
	6: "2GB-DDR3-RAM 2 Banks",
	7: "4GB-DDR3-RAM 1 Banks",
	8: "4GB-DDR3-RAM 2 Banks",
}

coding_emmc = {
	0: "no eMMC",
	1: "2GB-eMMC (MLC)",
	2: "4GB-eMMC (MLC)",
	3: "8GB-eMMC (MLC)",
	4: "16GB-eMMC (MLC)",
	5: "32GB-eMMC (MLC)",
}

coding_spi = {
	0: "No SPI FLASH",
	1: "4MB-FLASH",
	2: "8MB-FLASH",
	3: "16MB-FLASH",
}

coding_controller = {
	0: "RK3288 (1.8Ghz, Quad A17)",
	1: "RK3288-",
}

coding_eeprom = {
	1: "4KB EEPROM",
}

coding_eth = {
	0: "No ethernet",
	1: "10/100/1000 ethernet PHY",
}

coding_temp = {
	'I': "Industrial -40C to +85C",
	'C': "Commercial 0C to +85C",
}

coding_mod = {
	0: "PCM-059",
}

# phyCORE-RK3288 Kit variant
# 423011I.A0
variant_opt = {}
variant_artno = {}
variant_opt["prot"] = [0x04, 0x04, 0x03, 0x00, 0x01, 0x01, ord('I'), 0x00,
	0x00, 0x00, 0x00, ord('A'), 0x00]
variant_artno["prot"] = "443011I.A0"
variant_opt["kit"] = [0x04, 0x02, 0x03, 0x00, 0x01, 0x01, ord('I'), 0x00,
	0x00, 0x00, 0x00, ord('A'), 0x00]
variant_artno["kit"] = "423011I.A0"

variant_opt["min"] = [0x01, 0x00, 0x00, 0x00, 0x01, 0x00, ord('I'), 0x00,
	0x00, 0x00, 0x00, ord('A'), 0x00]
variant_artno["min"] = "100011I.A0"

variant_opt["max"] = [0x06, 0x04, 0x03, 0x00, 0x01, 0x01, ord('I'), 0x00,
	0x00, 0x00, 0x00, ord('A'), 0x00]
variant_artno["max"] = "643011I.A0"

def decode_eeprom(devaddr):
	decoded = {}
	decoded["ArtNo"]	= coding_mod.get(at24c32_get_ram(
                devaddr, 1)) + "-" + str(get_artno(devaddr))
	decoded["DDR"]		= coding_ddr.get(at24c32_get_ram(
		devaddr, 14 + 0), "Unknown")
	decoded["eMMC"]		= coding_emmc.get(at24c32_get_ram(
		devaddr, 14 + 1), "Unknown")
	decoded["SPI"]		= coding_spi.get(at24c32_get_ram(
		devaddr, 14 + 2), "Unknown")
	decoded["Controller"] 	= coding_controller.get(at24c32_get_ram(
		devaddr, 14 + 3), "Unknown")
	decoded["EEPROM"]	= coding_eeprom.get(at24c32_get_ram(
		devaddr, 14 + 4), "Unknown")
	decoded["Ethernet"]	= coding_eth.get(at24c32_get_ram(
		devaddr, 14 + 5), "Unknown")
	decoded["Temp"]		= coding_temp.get(unichr(at24c32_get_ram(
		devaddr, 14 + 6)), "Unknown")
	decoded["reserved0"]	= at24c32_get_ram(devaddr, 14 + 7)
	decoded["reserved1"]	= at24c32_get_ram(devaddr, 14 + 8)
	decoded["reserved2"]	= at24c32_get_ram(devaddr, 14 + 9)
	decoded["reserved3"]	= at24c32_get_ram(devaddr, 14 + 10)
	decoded["version0"]	= str(unichr(at24c32_get_ram(devaddr, 14 + 11)))
	decoded["version1"]	= at24c32_get_ram(devaddr, 14 + 12)
	decoded["somrev"]	= at24c32_get_ram(devaddr, 14 + 13)
	return decoded

def set_artno(devaddr, artno):
	# clear artno
	for i in range(12):
		at24c32_set_ram(devaddr, 0x02 + i, 0)

	for i, c in enumerate(artno):
		at24c32_set_ram(devaddr, 0x02 + i, ord(c))

def get_artno(devaddr):
	artno = ""
	for i in range(12):
		artno += chr(at24c32_get_ram(devaddr, 0x02 + i))
	return artno

def set_option(devaddr, opt):
	# clear option
	for i in range(13):
		at24c32_set_ram(devaddr, 14 + i, 0)

	for i in range(13):
		at24c32_set_ram(devaddr, 14 + i, opt[i])

def set_api(devaddr, api):
	at24c32_set_ram(devaddr, 0, api)

def set_mod(devaddr, mod):
	at24c32_set_ram(devaddr, 1, mod)

def set_rev(devaddr, rev):
	at24c32_set_ram(devaddr, 14 + 13, rev)

def main():
	dev = 0x58

	parser = argparse.ArgumentParser(
		description="phyCORE-RK3288 SoM EEPROM configurator.")
	parser.add_argument("-p", nargs=2,
		help="Program EEPROM: -p variant revision", required=False)
	parser.add_argument("-l", help="List all known SoM variants",
		action="store_true")
	parser.add_argument("-d", action="store_true",
		help="decode SoM EEPROM."
	)
	parser.add_argument("-x", action="store_true",
		help="lock EEPROM. This will set write protectioin.",
	)

	try:
		args = parser.parse_args()
		if args.l:
			for opt in variant_opt:
				print(opt)
		if args.d:
			for s, c in sorted(decode_eeprom(dev).items()):
				print(s + ":    \t" + str(c))
		if args.x:
			lock_eeprom(dev)
		if args.p:
			if args.p[0] in variant_opt:
				# clear identification page
				for i in range(32):
					at24c32_set_ram(dev, i, 0)

				set_api(dev, api_version)
				set_mod(dev, mod_version)
				set_artno(dev, variant_artno[args.p[0]])
				set_option(dev, variant_opt[args.p[0]])
				set_rev(dev, int(args.p[1]))
			else:
				print("Unsupported variant")
	except ValueError:
		print("Please enter a valid type.")
	except IOError as (errno, strerror):
		print("EEPROM is locked.")
		print("I/O error({0}): {1}".format(errno, strerror))

# Only run when you are the main program. Not when you're importes as a module:
if __name__ == '__main__':
	main()
