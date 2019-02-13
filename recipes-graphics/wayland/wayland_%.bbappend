do_install_append_rk3288() {
	# libwayland-egl has been moved to wayland 1.15+
	rm -f ${D}${libdir}/libwayland-egl*
	rm -f ${D}${libdir}/pkgconfig/wayland-egl.pc
}

FILES_${PN}_remove_rk3288 = "${libdir}/libwayland-egl.so"
