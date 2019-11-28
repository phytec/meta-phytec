PACKAGECONFIG += " opencv zbar"

PACKAGECONFIG[zbar] = "--enable-zbar,--disable-zbar,zbar"

EXTRA_OECONF_remove = "--disable-zbar"
