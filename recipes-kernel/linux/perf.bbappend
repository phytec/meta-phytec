do_configure_prepend() {
	if [ -e "${S}/tools/build/Makefile.feature" ]; then
	    sed -i 's,CFLAGS=,CC="\$(CC)" CFLAGS=,' ${S}/tools/build/Makefile.feature
	fi
}
