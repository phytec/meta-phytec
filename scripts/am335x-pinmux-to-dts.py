#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2014 Stefan Müller-Klieser, PHYTEC Messtechnik GmbH
# adapted from a script by Jan Lübbe

import sys
import re
import argparse
import os


def is_dir(dirname):
    """Checks if a path is an actual directory"""
    if not os.path.isdir(dirname):
        msg = "{0} is not a directory".format(dirname)
        raise argparse.ArgumentTypeError(msg)
    else:
        return dirname


def main():
    # parse command line options
    parser = argparse.ArgumentParser(description='Converts the output files from TI AM335x pinmux tool to lines'
                                                 'actual usable in device tree source files. Information is'
                                                 'taken from mux.h and pinmux.h and printed to stdout')
    parser.add_argument('datadirectory', help='input files must exist in this directory.')
    args = parser.parse_args()

    if args.datadirectory:
        dir = is_dir(args.datadirectory)

    # get pinctrl register offset from mux.h
    offset = re.compile(r"(CONTROL_PADCONF_.*?)\s+(0x\w+)")
    offsets = {}
    muxfile = open(os.path.join(dir, 'mux.h'), 'r')
    for line in muxfile.readlines():
        m = offset.search(line)
        if m:
            offsets[m.group(1)] = int(m.group(2), 16) - 0x800

    # get pinctrl register values from pinmux.h
    mux = re.compile(r"(CONTROL_PADCONF_.*?), \((\w+) \| (\w+) \| (\w+) \)\) /\* ([\w\[\]]+) \*/")
    pinmuxfile = open(os.path.join(dir, 'pinmux.h'), 'r')
    for line in pinmuxfile.readlines():
        m = mux.search(line)
        if m:
            off = offsets[m.group(1)]
            reg = 0
            reg_define = "PIN_"
            tabspaces = 1
            comment = m.group(1)[16:].lower()

            # put pinname in comment
            if comment != m.group(5).lower():
                comment += '.' + m.group(5).lower()

            # INPUT OUTPUT defines
            if m.group(2) == 'IEN':
                reg |= (1 << 5)
                reg_define += 'INPUT'
            elif m.group(2) == 'IDIS':
                reg_define += 'OUTPUT'
            else:
                sys.exit("bad field 2: %s" % m.group(2))

            # PULLUP PULLDOWN defines
            if m.group(3) == 'PD':
                reg_define += '_PULLDOWN'
            elif m.group(3) == 'PU':
                reg |= (2 << 3)
                reg_define += '_PULLUP'
            elif m.group(3) == 'OFF':
                reg |= (1 << 3)
                reg_define += ''
                tabspaces += 1
            else:
                sys.exit("bad field 3: %s" % m.group(3))

            # pin muxmode define
            if m.group(4).startswith('MODE'):
                reg |= int(m.group(4)[4:])
                reg_define += ' | ' + 'MUX_' + m.group(4)
            else:
                sys.exit("bad field 4: %s" % m.group(4))

            # print dts line to stdout
            print(3 * '\t' + '0x%03x' % (off) + ' (%s)' % reg_define + tabspaces * '\t' + '/* %s */' % comment)

if __name__ == "__main__":
    main()
