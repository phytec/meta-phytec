#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import sys
import argparse
from phylib import *


class BSP_Switcher(BoardSupportPackage):
    """This class extends the bsp with functionality to select one of the
    included machines throu a shell selection menu
    """
    def __init__(self):
        super(BSP_Switcher, self).__init__()

    def switch_machine(self, arg=None, show_all=False):
        if arg is not None:
            self.selected_machine = arg
            return self.write_machine_to_localconf()

        # Create a list of available machines. Don't include machines with tag
        # [hide] if requested.
        machines = []
        for machine in self.machines:
            if show_all or not "[hide]" in self.src.machines[machine]["description"].lower():
                machines.append(machine)

        machines.sort()
        max_len_machines = max(len(machine) for machine in machines)

        print '***************************************************'
        print '* Please choose one of the available Machines:'
        print '*'
        for i, line in enumerate(machines, 1):
            machine = line.rjust(max_len_machines)
            print '*   %2d : %s : %s' % (i, machine, self.src.machines[line]['description'])

        while True:
            try:
                user_input = int(raw_input('$ '))
                if user_input < 1 or user_input > len(self.machines):
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
