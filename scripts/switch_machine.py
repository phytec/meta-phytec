#!/usr/bin/env python2
# -*- coding: utf-8 -*-
# Copyright 2015, PHYTEC Messtechnik GmbH
# Author: Stefan Müller-Klieser <s.mueller-klieser@phytec.de>

import argparse
from phylib import *


class BSP_Switcher(BoardSupportPackage):
    """This class extends the bsp with functionality to select one of the
    included machines throu a shell selection menu
    """
    def __init__(self):
        super(BSP_Switcher, self).__init__()

    def switch_machine(self, arg=None):
        if arg is not None:
            self.selected_machine = arg
            return self.write_machine_to_localconf()

        print '***************************************************'
        print '* Please choose one of the available Machines:'
        print '*'
        machines = sorted(self.src.machines)
        max_len_machines = max(len(machine) for machine in machines)
        for i, line in enumerate(machines, 1):
            machine = line.rjust(max_len_machines)
            print '*   %2d : %s : %s' % (i, machine, self.src.machines[line]['description'])
        while True:
            try:
                user_input = int(raw_input('$ '))
                if user_input < 1 or user_input > len(self.src.machines):
                    raise ValueError
                break
            except ValueError:
                print 'No valid input.  Try again...'

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

    args = parser.parse_args()

    bsp = BSP_Switcher()
    bsp.switch_machine(args.machine)

if __name__ == "__main__":
    main()
