# Override meta-networking's SRC_URI variable, because it's overdefined
# and will generate conflicting revisions on Scarthgap.
SRC_URI = " \
	gitsm://github.com/Mbed-TLS/mbedtls.git;protocol=https;branch=mbedtls-3.6 \
	file://run-ptest \
"
