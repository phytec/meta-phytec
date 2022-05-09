SUMARY = "Configuration files for PHYTECs i.MX 8MP ISP camera drivers"
HOMEPAGE = "http://www.phytec.de"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PR = "r0"

SRC_URI += "\
    file://xml/VM-016-COL_AO082-C_1280x720.xml \
    file://xml/VM-016-COL_AO082-C_1280x800.xml \
    file://xml/VM-016-BW_AO082_1280x720.xml \
    file://xml/VM-016-BW_AO082_1280x800.xml \
    file://xml/VM-016-COL_AO086-C_1280x720.xml \
    file://xml/VM-016-COL_AO086-C_1280x800.xml \
    file://xml/VM-016-BW_AO086_1280x720.xml \
    file://xml/VM-016-BW_AO086_1280x800.xml \
    file://xml/VM-017-COL_AO062-C_1280x720.xml \
    file://xml/VM-017-COL_AO062-C_1920x1080.xml \
    file://xml/VM-017-COL_AO062-C_2592x1944.xml \
    file://xml/VM-017-BW_AO062_1280x720.xml \
    file://xml/VM-017-BW_AO062_1920x1080.xml \
    file://xml/VM-017-BW_AO062_2592x1944.xml \
    file://xml/VM-017-COL_AO070-C.A1_1280x720.xml \
    file://xml/VM-017-COL_AO070-C.A1_1920x1080.xml \
    file://xml/VM-017-COL_AO070-C.A1_2592x1944.xml \
    file://xml/VM-017-BW_AO070.A1_1280x720.xml \
    file://xml/VM-017-BW_AO070.A1_1920x1080.xml \
    file://xml/VM-017-BW_AO070.A1_2592x1944.xml \
    file://dwe/VM-016_AO082_1280x720.json \
    file://dwe/VM-016_AO082_1280x800.json \
    file://dwe/VM-016_AO086_1280x720.json \
    file://dwe/VM-016_AO086_1280x800.json \
    file://dwe/VM-017_AO062_1280x720.json \
    file://dwe/VM-017_AO062_1920x1080.json \
    file://dwe/VM-017_AO062_2592x1944.json \
    file://dwe/VM-017_AO070.A1_1280x720.json \
    file://dwe/VM-017_AO070.A1_1920x1080.json \
    file://dwe/VM-017_AO070.A1_2592x1944.json \
    file://Sensor_Entry.cfg.ar0144.AO082.col \
    file://Sensor_Entry.cfg.ar0144.AO082.bw \
    file://Sensor_Entry.cfg.ar0144.AO086.col \
    file://Sensor_Entry.cfg.ar0144.AO086.bw \
    file://Sensor_Entry.cfg.ar0521.AO062.col \
    file://Sensor_Entry.cfg.ar0521.AO062.bw \
    file://Sensor_Entry.cfg.ar0521.AO070.A1.col \
    file://Sensor_Entry.cfg.ar0521.AO070.A1.bw \
"

do_install() {
    install -d ${D}/opt/imx8-isp/bin
    install -d ${D}/opt/imx8-isp/bin/xml
    install -d ${D}/opt/imx8-isp/bin/dwe

    install -m 0644 ${WORKDIR}/xml/* ${D}/opt/imx8-isp/bin/xml
    install -m 0644 ${WORKDIR}/dwe/* ${D}/opt/imx8-isp/bin/dwe

    for file in $(find ${WORKDIR}/ -maxdepth 1 -name "Sensor_Entry.cfg.*" -type f); do
        APPENDIX="$(echo ${file} | sed 's/^.*Sensor_Entry\.cfg\(.*\)/\1/g')"
        install -m 0644 ${file} ${D}/opt/imx8-isp/bin/Sensor0_Entry.cfg${APPENDIX}
        install -m 0644 ${file} ${D}/opt/imx8-isp/bin/Sensor1_Entry.cfg${APPENDIX}
    done
}

FILES:${PN} += " \
    /opt/imx8-isp/bin \
"
