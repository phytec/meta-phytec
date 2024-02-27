sed_safely() {
    if [ $(ls -1 "${S}"/${2} 2>/dev/null | wc -l) -gt 0 ]; then
        sed -i "${1}" "${S}"/${2}
    fi
}
