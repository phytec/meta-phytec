#!/usr/bin/env python
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

    def switch_machine(self, machine=None, distro=None):
        if distro is not None:
            self.selected_distro = "distro"
        if machine is not None:
            self.selected_machine = machine
            return self.write_machine_to_localconf()

        configs = self.supported_configs
        if configs == []:
            print("No build/machine supported.")
            return self.write_machine_to_localconf()

        min_len_machines = min(len(config[0]) for config in configs)

        print('*********************************************************************')
        print('* Please choose one of the available builds:')
        print('*')
        print('no: %s: description and article number' % 'machine'.rjust(min_len_machines))
        print('    %s  distro: supported yocto distribution' % ' '.rjust(min_len_machines))
        print('    %s  target: supported build target'       % ' '.rjust(min_len_machines))
        print('')
        for i, (machine, distro, targets) in enumerate(configs, 1):
            # split description at first comma and print it as a two-liner
            description = lambda x: self.src.machines[x]['DESCRIPTION'].split(',',1)
            print('%2d: %s: %s' % (i, machine, description(machine)[0].strip()))
            if len(description(machine)) > 1:
                print('%s %s' % (' ' * (5 + min_len_machines), description(machine)[1].strip()))
            if self.src.machines[machine]['ARTICLENUMBERS']:
                print('%s %s' % (' ' * (5 + min_len_machines),
                                self.src.machines[machine]['ARTICLENUMBERS']))
            print('%s distro: %s' % (' ' * (5 + min_len_machines), distro))
            for target in targets:
                print('%s target: %s' % (' ' * (5 + min_len_machines), target))


        # request user input
        while True:
            try:
                user_input = int(input('$ '))
                if user_input < 1 or user_input > len(configs):
                    raise ValueError
                break
            except (ValueError, NameError):
                print('No valid input.  Try again...')
            except KeyboardInterrupt:
                # If the user presses CTRL+C, exit the application nicely.
                print('')  # print newline, so prompt doesn't start after '$ '
                return False

        # User index starts with 1, list index starts with 0.
        (machine, distro, targets) = configs[user_input -1]
        self.selected_machine = machine
        self.selected_distro = distro

        # write build target to conf-notex.txt so it will be displayed after
        # sourcing the environment
        if any(targets):
            confnotes = os.path.join(self.src.bsp_dir, 'tools', 'templateconf', 'conf-notes.txt')
            f = open(confnotes, 'r')
            lines = f.readlines()
            f.close()
            f = open(confnotes, 'w')
            f.writelines([l for l in lines[:-1] if 'bitbake' not in l])
            for target in targets:
                print('add TARGET %s to conf-notes.txt' % target)
                f.write('   $ bitbake %s\n' % target)
            f.write('\n')
            f.close()

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
    parser.add_argument('-d', dest='distro', help='set the distro string')

    args = parser.parse_args()

    bsp = BSP_Switcher()
    if not bsp.switch_machine(args.machine, args.distro):
        # An error has happened. Report it back to calling program.
        sys.exit(1)

if __name__ == "__main__":
    main()
