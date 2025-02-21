#!/usr/bin/python3
# Copyright 2017 Phytec Messtechnik GmbH
# Author: Wadim Egorov <w.egorov@phytec.de>

import smbus
import time
import argparse
import re
import os
import os.path
import math

# Set the I2C bus. The config EEPROM is always on the first bus, i2c0.
i2c = smbus.SMBus(0)

# Set current adress of eeprom
def at24c32_set_addr(devaddr, addr):
	upperbyte = (addr & 0b1111111100000000) >> 8
	lowerbyte = addr & 0b0000000011111111
	i2c.write_i2c_block_data(devaddr, upperbyte, [lowerbyte])
	time.sleep(0.01)

# Valid addr for RAM: 0 - 4096
def at24c32_get_ram(devaddr, addr):
	at24c32_set_addr(devaddr, addr)
	return i2c.read_byte(devaddr)

# Valid addr for RAM: 0 - 4096
def at24c32_set_ram(devaddr, addr, val):
	upperbyte = (addr & 0b1111111100000000) >> 8
	lowerbyte = addr & 0b0000000011111111
	i2c.write_i2c_block_data(devaddr, upperbyte, [lowerbyte, val])
	time.sleep(0.01)

# Lock EEPROM permanently
def lock_eeprom(devaddr):
	at24c32_set_ram(devaddr, 0x400, 2)

# EEPROM layout version
api_version = 0
# Module version
mod_version = 0

# Module flash devices
emmc_som_device = "/dev/mmcblk0"
spi_som_device = "/dev/mtd0"

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
	0: "No eMMC",
	1: "eMMC Flash populated",
}

coding_spi = {
	0: "No SPI FLASH",
	1: "SPI Flash populated",
}

coding_controller = {
	0: "RK3288 (1.8Ghz, Quad A17)",
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

coding_ksp = {
	0: "Not a KSP/KSM",
	1: "KSP",
	2: "KSM",
}

variant_opt = {}

# phyCORE-RK3288 Kit variant
# PCM-059-423011I.A0
variant_opt["kit"] = [
	0x04,		# DDR
	0x01,		# eMMC
	0x01,		# SPI flash
	0x00,		# Controller
	0x01,		# Ethernet
	ord('I'),	# Temp
	0x00,		# reserved
	0x00,		# reserved
	0x00,		# reserved
	0x00,		# reserved
	ord('A'),	# version
	0x00		# version
]

# phyCORE-RK3288 Prototype variant
# PCM-059-443011I.A0
variant_opt["prot"] = [
	0x04,		# DDR
	0x01,		# eMMC
	0x01,		# SPI flash
	0x00,		# Controller
	0x01,		# Ethernet
	ord('I'),	# Temp
	0x00,		# reserved
	0x00,		# reserved
	0x00,		# reserved
	0x00,		# reserved
	ord('A'),	# version
	0x00		# version
]

# phyCORE-RK3288 Min variant
# PCM-059-100010I.A0
variant_opt["min"] = [
	0x01,		# DDR
	0x00,		# eMMC
	0x00,		# SPI flash
	0x00,		# Controller
	0x00,		# Ethernet
	ord('I'),	# Temp
	0x00,		# reserved
	0x00,		# reserved
	0x00,		# reserved
	0x00,		# reserved
	ord('A'),	# version
	0x00		# version
]

# phyCORE-RK3288 Max variant
# PCM-059-643011I.A0
variant_opt["max"] = [
	0x06,		# DDR
	0x01,		# eMMC
	0x01,		# SPI flash
	0x00,		# Controller
	0x01,		# Ethernet
	ord('I'),	# Temp
	0x00,		# reserved
	0x00,		# reserved
	0x00,		# reserved
	0x00,		# reserved
	ord('A'),	# version
	0x00		# version
]

def decode_eeprom(devaddr):
	decoded = {}
	decoded["api"]		= at24c32_get_ram(devaddr, 0)
	decoded["mod"]		= coding_mod.get(at24c32_get_ram(devaddr, 1),
						"Unknown")
	decoded["DDR"]		= coding_ddr.get(at24c32_get_ram(devaddr, 2),
						 "Unknown")
	decoded["eMMC"]		= coding_emmc.get(at24c32_get_ram(devaddr, 3),
                                                 "Unknown")
	decoded["SPI"]		= coding_spi.get(at24c32_get_ram(devaddr, 4),
                                                 "Unknown")
	decoded["Controller"]	= coding_controller.get(at24c32_get_ram(devaddr, 5),
                                                 "Unknown")
	decoded["Ethernet"]	= coding_eth.get(at24c32_get_ram(devaddr, 6),
                                                 "Unknown")
	decoded["Temp"]		= coding_temp.get(chr(at24c32_get_ram(devaddr, 7)),
                                                 "Unknown")
	decoded["reserved0"]	= at24c32_get_ram(devaddr, 8)
	decoded["reserved1"]	= at24c32_get_ram(devaddr, 9)
	decoded["reserved2"]	= at24c32_get_ram(devaddr, 10)
	decoded["reserved3"]	= at24c32_get_ram(devaddr, 11)
	decoded["version0"]	= str(unichr(at24c32_get_ram(devaddr, 12)))
	decoded["version1"]	= at24c32_get_ram(devaddr, 13)
	decoded["somrev"]       = at24c32_get_ram(devaddr, 14)
	decoded["mac"]		= [None] * 6
	for i in range(len(decoded["mac"])):
		decoded["mac"][i] = at24c32_get_ram(devaddr, 15 + i)
	decoded["ksp"]		= coding_ksp.get(at24c32_get_ram(devaddr, 21),
                                                 "Unknown")
	decoded["kspno"]	= at24c32_get_ram(devaddr, 22)

	return decoded

def validate_option(opt):
	if opt[0] not in coding_ddr:
		raise ValueError("Invalid ddr option")
	if opt[1] not in coding_emmc:
		raise ValueError("Invalid emmc option")
	if opt[2] not in coding_spi:
		raise ValueError("Invalid spi option")
	if opt[3] not in coding_controller:
		raise ValueError("Invalid controller option")
	if opt[4] not in coding_eth:
		raise ValueError("Invalid eth option")
	if chr(opt[5]) not in coding_temp:
		raise ValueError("Invalid temperature option")
	return True

def set_option(devaddr, opt):
	for i in range(len(opt)):
		at24c32_set_ram(devaddr, 2 + i, opt[i])

def set_api(devaddr, api):
	at24c32_set_ram(devaddr, 0, api)

def set_mod(devaddr, mod):
	at24c32_set_ram(devaddr, 1, mod)

def set_rev(devaddr, rev):
	at24c32_set_ram(devaddr, 14, rev)

def set_mac(devaddr, mac):
	for idx, bt in enumerate(mac):
		at24c32_set_ram(devaddr, 15 + idx, int(bt, 16))

def set_ksp(devaddr, ksp, kspno):
	at24c32_set_ram(devaddr, 22, kspno)
	if ksp == "ksp":
		at24c32_set_ram(devaddr, 21, 1)
	elif ksp == "ksm":
		at24c32_set_ram(devaddr, 21, 2)
	else:
		raise ValueError("Invalid KSP/KSM, only ksp or ksm is allowed")

# Sets the version which is part of the ArtNo (last two bytes)
def set_version(devaddr, v0, v1):
	at24c32_set_ram(devaddr, 12, v0)
	at24c32_set_ram(devaddr, 13, v1)

def popcount(bb):
	s = 0
	for b in bb:
		s += bin(b).count("1")
	return s

def calc_chksm(devaddr):
	eeprom=[]
        for i in range(31):
                eeprom.append(at24c32_get_ram(devaddr, i))
        return popcount(eeprom)

def set_chksm(devaddr):
	at24c32_set_ram(devaddr, 31, calc_chksm(devaddr))

def parse_mac(macstr):
	if re.match("[0-9a-f]{2}([:])[0-9a-f]{2}(\\1[0-9a-f]{2}){4}$",
		macstr.lower()):
		return macstr.split(':')
	else:
		raise ValueError("Invalid MAC format")

def get_file_size(filename):
    fd = os.open(filename, os.O_RDONLY)
    try:
        return os.lseek(fd, 0, os.SEEK_END)
    finally:
        os.close(fd)

# Returns a string which represents the ArtNo from the OptionTree
def get_artno(devaddr):
	result = coding_mod.get(at24c32_get_ram(devaddr, 1), "Unknown")
	result += "-"

	v0 = str(unichr(at24c32_get_ram(devaddr, 12)))
	v1 = str(at24c32_get_ram(devaddr, 13))

	ksp = at24c32_get_ram(devaddr, 21)
	if ksp != 0:
		kspno = at24c32_get_ram(devaddr, 22)
		result += coding_ksp.get(ksp, "Unknown") + "-" + str(kspno)
		result += "." + v0 + v1
		return result

	# Check if eMMC is populated on the module
	if at24c32_get_ram(devaddr, 3) > 0:
		# Some carrier boards are able to disable the eMMC by
		# cutting the clock pin. Check if eMMC was probed and is working
		if os.path.exists(emmc_som_device):
			# detect eMMC size
			emmc_size = get_file_size(emmc_som_device)
			# actual size
			emmc_size = pow(2, int(math.log(emmc_size, 2)) + 1)
			# size in GB
			emmc_size /= (1024*1024*1024)
			coding_ot_emmc = int(math.log(emmc_size, 2))
		else:
			raise ValueError("Missing eMMC device")
	else:
		# no eMMC populated
		coding_ot_emmc = 0

	# Check if SPI nor flash is populated on the module
	if at24c32_get_ram(devaddr, 4) > 0:
		# Some carrier boards are able to disable the SPI nor flash
		# Check if flash was probed and is working
		if os.path.exists(spi_som_device):
			# detect SPI nor flash size
			spi_size = get_file_size(spi_som_device)
			# size in MB
			spi_size /= (1024*1024)
			coding_ot_spi = int(math.log(spi_size, 2)) - 1
		else:
			raise ValueError("Missing SPI nor flash device")
	else:
		# no SPI nor flash populated
		coding_ot_spi = 0

	result += str(at24c32_get_ram(devaddr, 2)) # RAM
	result += str(coding_ot_emmc) # eMMC
	result += str(coding_ot_spi) # SPI
	result += str(at24c32_get_ram(devaddr, 5)) # Controller
	result += str("1") # EEPROM
	result += str(at24c32_get_ram(devaddr, 6)) # Eth
	result += str(unichr(at24c32_get_ram(devaddr, 7))) # Temp
	result += "." + v0 + v1
	return result


def main():
	# Identification Page
	dev = 0x58

	parser = argparse.ArgumentParser(
		description="phyCORE-RK3288 SoM EEPROM configurator.")
	subparsers = parser.add_subparsers(dest="cmd")
	set_parser = subparsers.add_parser("set", help="Program EEPROM")
	set_parser.add_argument("--variant", type=str, help="variant",
				dest="variant", required=True)
	set_parser.add_argument("--revision", type=int, help="SoM revision",
				dest="somrev", required=True)
	set_parser.add_argument("--mac", type=str,
				help="MAC, format: x is hex: xx:xx:xx:xx:xx:xx",
				dest="mac", required=True)
	set_parser.add_argument("--version", type=str, help="Version", dest="v",
				required=False, nargs=2)
	subparsers.add_parser("decode", help="Decode content of EEPROM")
	subparsers.add_parser("list", help="List all known SoM variants")
	subparsers.add_parser("lock", help="Lock EEPROM permanently. This will \
			      set write protection on the identification page")
	ksp_parser = subparsers.add_parser("ksp", help="Program KSP variant")
	ksp_parser.add_argument("--ksp", type=str, help="KSP or KSM",
				dest="ksp", required=True)
	ksp_parser.add_argument("--kspno", type=int, help="KSP number",
				dest="kspno", required=True)
	ksp_parser.add_argument("--ddr", type=int, help="DDR", dest="ddr",
				required=True)
	ksp_parser.add_argument("--emmc", type=int, help="emmc", dest="emmc",
				required=True)
	ksp_parser.add_argument("--spi", type=int, help="SPI nor flash",
				dest="spi", required=True)
	ksp_parser.add_argument("--controller", type=int, help="Controller",
				dest="controller", required=True)
	ksp_parser.add_argument("--eth", type=int, help="Ethernet", dest="eth",
				required=True)
	ksp_parser.add_argument("--tmp", type=str, help="Temperature",
				dest="tmp", required=True)
	ksp_parser.add_argument("--version", type=str, help="Version", dest="v",
				required=True, nargs=2)
	ksp_parser.add_argument("--mac", type=str,
				help="MAC, format: x is hex: xx:xx:xx:xx:xx:xx",
				dest="mac", required=True)
	ksp_parser.add_argument("--revision", type=int, help="SoM revision",
				dest="somrev", required=True)

	try:
		args = parser.parse_args()

		if args.cmd == "decode":
			print(get_artno(dev) + ":")
			for s, c in sorted(decode_eeprom(dev).items()):
				print(s + ":    \t" + str(c))
		if args.cmd == "list":
			for opt in variant_opt:
				print(opt)
		if args.cmd == "set":
			if args.variant in variant_opt:
				# clear identification page
				for i in range(32):
					at24c32_set_ram(dev, i, 0)

				set_api(dev, api_version)
				set_mod(dev, mod_version)
				set_option(dev, variant_opt[args.variant])
				set_rev(dev, args.somrev)
				set_mac(dev, parse_mac(args.mac))
				if args.v:
					set_version(dev, ord(args.v[0]),
							 int(args.v[1]))
				set_chksm(dev)
			else:
				print("Unsupported variant")
		if args.cmd == "ksp":
			# clear identification page
			for i in range(32):
				at24c32_set_ram(dev, i, 0)

			set_ksp(dev, args.ksp, args.kspno)
			ksp_variant = [
					args.ddr,
					args.emmc,
					args.spi,
					args.controller,
					args.eth,
					ord(args.tmp),
					0, 0, 0, 0, # reserved
					ord(args.v[0]),
					int(args.v[1]),
				]
			set_api(dev, api_version)
			set_mod(dev, mod_version)
			validate_option(ksp_variant)
			set_option(dev, ksp_variant)
			set_rev(dev, args.somrev)
			set_mac(dev, parse_mac(args.mac))
			set_chksm(dev)

		if args.cmd == "lock":
			lock_eeprom(dev)

	except ValueError as err:
		print(err)
	except IOError as (errno, strerror):
		print("EEPROM is locked.")
		print("I/O error({0}): {1}".format(errno, strerror))

# Only run when you are the main program. Not when you're importes as a module:
if __name__ == '__main__':
	main()
