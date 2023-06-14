#!/bin/sh
arg=""
if [ ${#@} -gt 0 ]; then
	arg="-b ssl $@"
	if [ -n "${CST_KEY_SOURCE}" ] ; then
		if [ "${CST_KEY_SOURCE}" = "token" ] ; then
			arg="-b pkcs11 $@"
		fi
	fi
fi
cst-bin $arg
