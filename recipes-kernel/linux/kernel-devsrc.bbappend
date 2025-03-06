do_install:append() {
    # This fixes the rpm dependency failure on install of kernel-devsrc depending on /bin/sed
    cd ${D} || true
    for i in $(grep -srI "!/bin/sed" | cut -d":" -f1); do
        sed -i -e "s#!/bin/sed#!/usr/bin/env sed#g" $i
    done
}
