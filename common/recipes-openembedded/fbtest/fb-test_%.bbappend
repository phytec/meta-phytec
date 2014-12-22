do_install() {
    install -d ${D}${bindir}
    for prog in rect fb-test offset ; do
        install -m 0755 $prog ${D}${bindir}
    done
    #conflicts with perf
    install -m 0755 perf ${D}${bindir}/fbtest-perf
}

