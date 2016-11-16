#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import sys
import argparse
from phylib import *

class BSP_Switcher(BoardSupportPackage):
    """
    Provide a shell selection menu for the machines.
    """
    def __init__(self):
        super(BSP_Switcher, self).__init__()

    def switch_machine(self, arg=None, show_all=False):
        if arg is not None:
            self.selected_machine = arg
            return self.write_machine_to_localconf()

        if show_all:
            machines = self.src.machines.keys()
        else:
            machines = self.supported_machines

        machines.sort()
        min_len_machines = min(len(machine) for machine in machines)

        print '***************************************************'
        print '* Please choose one of the available Machines:'
        print '*'
        print 'no: %s: description and article number' % "machine".rjust(min_len_machines)
        print ''
        for i, machine in enumerate(machines, 1):
            # split description at first comma and print it as a two-liner
            description = map(str.strip, self.src.machines[machine]['DESCRIPTION'].split(',',1))
            print '%2d: %s: %s' % (i, machine, description[0])
            if len(description) > 1:
                print '%s %s' % (' ' * (5 + min_len_machines), description[1])
            print '%s %s' % (' ' * (5 + min_len_machines),
                            self.src.machines[machine]['ARTICLENUMBERS'])

        # request user input
        while True:
            try:
                user_input = int(raw_input('$ '))
                if user_input < 1 or user_input > len(machines):
                    raise ValueError
                break
            except ValueError:
                print 'No valid input.  Try again...'
            except KeyboardInterrupt:
                # If the user presses CTRL+C, exit the application nicely.
                print ''  # print newline, so prompt doesn't start after '$ '
                return False

        # User index starts with 1, list index starts with 0.
        self.selected_machine = machines[user_input - 1]
        return self.write_machine_to_localconf()


##############
# Executable #
##############

def main():
    """This script prints out the machines which can be selected in the release
    and writes the selection to the local.conf
    """
    parser = argparse.ArgumentParser(description='Select an available machine and set it in your local.conf')
    parser.add_argument('-m', dest='machine', help='set the machine string')
    parser.add_argument('-a', "--all", dest="show_all", action="store_true",
                        default=False, help='show all hidden machines')

    args = parser.parse_args()

    bsp = BSP_Switcher()
    if not bsp.switch_machine(args.machine, show_all=args.show_all):
        # An error has happened. Report it back to calling program.
        sys.exit(1)

if __name__ == "__main__":
    main()
