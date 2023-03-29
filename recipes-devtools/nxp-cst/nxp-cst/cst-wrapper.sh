#!/bin/sh
if [ -n "${CST_KEY_SOURCE}" ] ; then
    if [ "${CST_KEY_SOURCE}" = "token" ] ; then
        cst-pkcs11 $@
        exit $?
    fi
fi
cst-bin $@
