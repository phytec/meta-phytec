#!/bin/bash -

_FORMAT_PATTERN='£-£'
_SITECONFSAMPLE_PATH=$(dirname $(realpath ${BASH_SOURCE}))

#----------------------------------------------
# Set supported Linux Distrib Release
#
_SUPPORTED_LINUX_DISTRIB="Ubuntu"
_SUPPORTED_UBUNTU_RELEASE="16.04 18.04"

#----------------------------------------------
# Set default layer root
#
if [ -z $META_LAYER_ROOT ]; then
    _META_LAYER_ROOT=layers/meta-st
else
    _META_LAYER_ROOT=$META_LAYER_ROOT
fi

if [ -z $META_LAYER_BSP ]; then
    _META_LAYER_BSP=layers/meta-phytec
else
    _META_LAYER_BSP=$META_LAYER_BSP
fi


#----------------------------------------------
# Set ROOTOE for oe sdk baseline
#
ROOTOE=$PWD
while test ! -d "${ROOTOE}/${_META_LAYER_ROOT}" && test "${ROOTOE}" != "/"
do
    ROOTOE=$(dirname ${ROOTOE})
done
if test "${ROOTOE}" == "/"
then
    echo "[ERROR] you're trying to launch the script outside oe sdk tree"
    return 1
fi

######################################################
# FUNCTION / ALIAS
# --
#

######################################################
# Envsetup help
#
stoe_help() {
    echo ""
    echo "==========================================================================="
    _stoe_help_usage
    echo ""
    echo "==========================================================================="
    _stoe_help_option
    echo ""
    echo "==========================================================================="
    _stoe_help_extra
    echo ""
    echo "==========================================================================="
    echo ""
}

_stoe_help_usage() {
    echo "Usage:"
    echo "  source ${BASH_SOURCE#$PWD/} [OPTION]"
    echo ""
    echo "Description:"
    echo "  This script allows to enable OpenEmbedded build environment."
    echo "  It will generate if needed the bblayers.conf and local.conf files according"
    echo "  to the DISTRO and MACHINE you want to use and configure by default build"
    echo "  folder as: 'build-<DISTRO>-<MACHINE>'."
    echo "  Note that it has to be sourced from baseline root."
}
_stoe_help_option() {
    echo "Options:"
    echo "  --help"
    echo "      Print this message"
    echo "  --reset"
    echo "      Remove existing configuration files and restore original ones"
    echo "  --no-ui"
    echo "      Disable UI for DISTRO and MACHINE selection"
    echo "  <BUILD_DIR>"
    echo "      Provide specific build folder (should start with 'build' prefix)"
}
_stoe_help_extra() {
    echo "Extra configuration:"
    echo "  Avoid selection by user:"
    echo "      Before sourcing the script, user can define any of:"
    echo "        BUILD_DIR=<BUILD_DIR>   : set specific build folder"
    echo "        DISTRO=<DISTRO>         : set specific DISTRO config"
    echo "        MACHINE=<MACHINE>       : set specific MACHINE config"
    echo "      Example:"
    echo "        $> DISTRO=nodistro MACHINE=stm32mp1 source ${BASH_SOURCE#$PWD/}"
    echo ""
    echo "  Override default script settings:"
    echo "      DL_DIR:"
    echo "        Override with FORCE_DL_CACHEPREFIX var:"
    echo "          $> FORCE_DL_CACHEPREFIX=<DOWNLOAD_CACHE_PATH> source ${BASH_SOURCE#$PWD/}"
    echo "        This configures '<FORCE_DL_CACHEPREFIX>/oe-downloads' as download"
    echo "        cache folder in site.conf file."
    echo "      SSTATE_DIR:"
    echo "        Override with FORCE_SSTATE_CACHEPREFIX var:"
    echo "          $> FORCE_SSTATE_CACHEPREFIX=<SSTATE_CACHE_PATH> source ${BASH_SOURCE#$PWD/}"
    echo "        This configures '<FORCE_SSTATE_CACHEPREFIX>/oe-sstate-cache' as"
    echo "        sstate cache folder in site.conf file."
    echo "      SOURCE_MIRROR_URL:"
    echo "        Override with FORCE_SOURCE_MIRROR_URL var:"
    echo "          $> FORCE_SOURCE_MIRROR_URL=<SOURCE_MIRROR_URL_PATH> source ${BASH_SOURCE#$PWD/}"
    echo "        This configures 'SOURCE_MIRROR_URL = <FORCE_SOURCE_MIRROR_URL>' as"
    echo "        PREMIRRORS when fetching source in site.conf file."
    echo "      SSTATE_MIRROR_URL:"
    echo "        Override with FORCE_SSTATE_MIRROR_URL var:"
    echo "          $> FORCE_SSTATE_MIRROR_URL=<SSTATE_MIRROR_URL_PATH> source ${BASH_SOURCE#$PWD/}"
    echo "        This configures 'file://.* <FORCE_SSTATE_MIRROR_URL>/PATH;downloadfilename=PATH'"
    echo "        as sstate mirror in site.conf file."
    echo ""
    echo "  Configure SDKMACHINE:"
    echo "      By default SDKMACHINE is same as current HOST, but this can be override"
    echo "      if user defines SDKMACHINE=<SDKMACHINE> before sourcing the script."
    echo "      Example: on 64 bits HOST, set 32 bits SDK build"
    echo "        $> SDKMACHINE=i586 source ${BASH_SOURCE#$PWD/}"
    echo ""
    echo "  Define BSP_DEPENDENCY:"
    echo "      For specific machine, outside of meta-st layers, we may need to append"
    echo "      specific layers to bblayers.conf file when sourcing environement setup file"
    echo "      A specific var is available to provide the list of layer path (starting from"
    echo "      baseline root): BSP_DEPENDENCY"
    echo "      Example:"
    echo "        $> BSP_DEPENDENCY='<layer_path1> <layer_path2>' source ${BASH_SOURCE#$PWD/}"
    echo ""
    echo "  Configure META_LAYER_ROOT:"
    echo "      By default the script run using meta-st as default layer root, but this can"
    echo "      be changed if you set META_LAYER_ROOT (starting from baseline root)"
    echo "      Example:"
    echo "        $> META_LAYER_ROOT=openembedded-core source ${BASH_SOURCE#$PWD/}"
}

######################################################
# provide full list of STOE utilities available
_stoe_utilities() {
    echo "==========================================================================="
    echo "STOE Utilities:"
    echo "  stoe_list_env                   : list all ST environement variables"
    echo "  stoe_list_images [LAYER_PATH]   : list all images available in LAYER_PATH"
    echo "  stoe_config_summary [BUILD_DIR] : list config summary for BUILD_DIR"
    echo "  stoe_source_premirror_disable   : disable SOURCE_MIRROR_URL in conf file"
    echo "  stoe_source_premirror_enable    : enable SOURCE_MIRROR_URL in conf file"
    echo "  stoe_sstate_mirror_disable      : disable sstate-mirror usage"
    echo "  stoe_sstate_mirror_enable       : enable sstate-mirror usage"
    echo "==========================================================================="
}

######################################################
# alias function: list all ST environment variables
#
stoe_list_env() {
    echo "==========================================================================="
    echo "List of environment variables available:"
    echo "  BUILDDIR               = $BUILDDIR"
    echo "  ST_OE_DISTRO_CODENAME  = $ST_OE_DISTRO_CODENAME"
    echo "==========================================================================="
}

######################################################
# init UI_CMD if needed
_stoe_set_env_init() {
    if [[ $_ENABLE_UI -eq 1 ]] && [[ -z "${UI_CMD}" ]]; then
        # Init dialog box command if dialog or whiptail is available
        command -v dialog > /dev/null 2>&1 && UI_CMD='dialog'
        command -v whiptail > /dev/null 2>&1 && UI_CMD='whiptail'
    fi
}

######################################################
# alias function: set all ST environment variables
#
stoe_set_env() {
    export ST_OE_DISTRO_CODENAME=$_DISTRO_CODENAME
}

######################################################
# extract requested VAR from conf files for BUILD_DIR provided
_stoe_config_read() {
    local builddir=$(realpath $1)
    local stoe_var=$2
    local findconfig=""
    if ! [[ -z $(grep -Rs "^[ \t]*$stoe_var[ \t]*=" $builddir/conf/*.conf) ]]; then
        # Config defined as "=" in conf file
        findconfig=$(grep -Rs "^[ \t]*$stoe_var[ \t]*=" $builddir/conf/*.conf)
    elif ! [[ -z $(grep -Rs "^[ \t]*$stoe_var[ \t]*?*=" $builddir/conf/*.conf) ]]; then
        # Config defined as "?=" in conf file
        findconfig=$(grep -Rs "^[ \t]*$stoe_var[ \t]*?*=" $builddir/conf/*.conf)
    elif ! [[ -z $(grep -Rs "^[#]*$stoe_var[ \t]*=" $builddir/conf/*.conf) ]]; then
        findconfig="\<disable\>"
    else
        # Config not found
        findconfig="\<no-custom-config-set\>"
    fi
    # Format config
    local formatedconfig=$(echo $findconfig | sed -e 's|^.*"\(.*\)".*$|\1|g;s|\${TOPDIR}|\${builddir}|')
    # Expand and export config
    eval echo "$formatedconfig"
}

######################################################
# alias function: display current configuration
#
stoe_config_summary() {
    local builddir=""
    if [[ $# == 0 ]]; then
        # Override builddir in case of none argument provided
        builddir=$BUILDDIR
    elif [ $(realpath $1) ]; then
        # Use provided dir as builddir
        builddir=$(realpath $1)
    else
        echo "[ERROR] '$1' is not an existing BUILD_DIR."
        echo ""
        return 1
    fi
    echo ""
    echo "==========================================================================="
    echo "Configuration files have been created for the following configuration:"
    echo ""
    echo "    DISTRO            : " $(_stoe_config_read $builddir DISTRO)
    echo "    DISTRO_CODENAME   : " $ST_OE_DISTRO_CODENAME
    echo "    MACHINE           : " $(_stoe_config_read $builddir MACHINE)
    echo "    BB_NUMBER_THREADS : " $(_stoe_config_read $builddir BB_NUMBER_THREADS)
    echo "    PARALLEL_MAKE     : " $(_stoe_config_read $builddir PARALLEL_MAKE)
    echo ""
    echo "    BUILD_DIR         : " $(basename $builddir)
    echo "    DOWNLOAD_DIR      : " $(_stoe_config_read $builddir DL_DIR)
    echo "    SSTATE_DIR        : " $(_stoe_config_read $builddir SSTATE_DIR)
    echo ""
    echo "    SOURCE_MIRROR_URL : " $(_stoe_config_read $builddir SOURCE_MIRROR_URL)
    echo "    SSTATE_MIRRORS    : " $(_stoe_config_read $builddir SSTATE_MIRRORS | sed 's|^.*\(http:.*\)/PATH.*$|\1|')
    echo ""
    echo "    WITH_EULA_ACCEPTED: " $(_stoe_config_read $builddir ACCEPT_EULA_$(_stoe_config_read $builddir MACHINE) | sed 's|0|NO|;s|1|YES|')
    echo ""
    echo "==========================================================================="
    echo ""
}

######################################################
# extract description for images provided
_stoe_list_images_descr() {
    for l in $1;
    do
        local image=$(echo $l | sed -e 's#^.*/\([^/]*\).bb$#\1#')
        if [ ! -z "$(grep "^SUMMARY[ \t]*=" $l)" ]; then
            local descr=$(grep "^SUMMARY[ \t]*=" $l | sed -e 's/^.*"\(.*\)["\]$/\1/')
        else
            local descr=$(grep "^DESCRIPTION[ \t]*=" $l | sed -e 's/^.*"\(.*\)["\]$/\1/')
        fi
        if [ -z "$descr" ] && [ "$2" == "ERR" ]; then
            descr="[ERROR] No description available"
        fi
        printf "    %-33s  -   $descr\n" $image
    done
}

######################################################
# alias function: list all images available
#
stoe_list_images() {
    local metalayer=""
    if [ "$#" = "0" ]; then
        echo "[ERROR] missing layer path."
        return 1
    elif [ -e $(realpath $1)/conf/layer.conf ] || [ "$(realpath $1)" = "$(realpath ${ROOTOE}/${_META_LAYER_ROOT})" ]; then
        # Use provided dir as metalayer
        metalayer=$(realpath $1)
    else
        echo "[ERROR] '$1' is not an existing layer."
        echo ""
        return 1
    fi
    local err=$2
    local filter=$3

    local LIST=$(find $metalayer/ -type d \( -name '.git' -o -name 'source*' -o -name 'script*' \) -prune -o -type f -wholename '*/images/*.bb' -not -wholename '*/meta-skeleton/*' | grep '.*/images/.*\.bb' | sort)

    if [ "$filter" = "FILTER" ]; then
        local LAYERS_LIST=$(find $metalayer/ -type d \( -name '.git' -o -name 'source*' -o -name 'script*' \) -prune -o -type f -wholename '*/conf/layer.conf' -not -wholename '*/meta-skeleton/*' | grep '.*/conf/layer\.conf' | sed 's#/conf/layer.conf##' | sort)
        # Filter for layer available in current bblayers.conf file
        unset LAYERS_SET
        for l in ${LAYERS_LIST}; do
            if ! [[ -z $(grep "${l#$(dirname $BUILDDIR)/}[ '\"]" $BUILDDIR/conf/bblayers.conf) ]]; then
                LAYERS_SET+=(${l})
            fi
        done
        if [ -z "${#LAYERS_SET[@]}" ]; then
            echo "[WARNING] None of the layers from $metalayer are defined in current $(basename $BUILDDIR)/conf/bblayers.conf file."
            echo
            return
        fi
        # Filter images from enabled layers
        unset IMAGE_SET
        for ITEM in ${LAYERS_SET[@]}; do
            for i in ${LIST}; do
                if [ "${i#$ITEM/}" != "$i" ]; then
                    IMAGE_SET+=(${i})
                fi
            done
        done
        if [ -z "${#IMAGE_SET[@]}" ]; then
            echo "[WARNING] From the layers of $metalayer enable in your $(basename $BUILDDIR)/conf/bblayers.conf file, there is no image available for build."
            echo
            return
        fi
        LIST="${IMAGE_SET[@]}"
    fi

    echo ""
    echo "==========================================================================="
    echo "Available images for '$metalayer' layer are:"
    echo ""
    _stoe_list_images_descr "$LIST" "$err"
    echo ""
}

######################################################
# alias function: disable ST premirror
#
stoe_source_premirror_disable() {
    if [ -e $BUILDDIR/conf/site.conf ]; then
        if [ -z "$(grep SOURCE_MIRROR_URL $BUILDDIR/conf/site.conf)" ]; then
            echo "[WARNING] no SOURCE_MIRROR_URL entry in site.conf from $BUILDDIR/conf/"
            echo "Nothing to do..."
        else
            echo ">>> DISABLE SOURCE_MIRROR_URL in $BUILDDIR/conf/site.conf"
            echo ">>> Default to default one from DISTRO or MACHINE settings (if any)"
            sed -e 's|^[ /t]*\(SOURCE_MIRROR_URL.*\)$|#\1|g' -i $BUILDDIR/conf/site.conf
        fi
    else
        echo "[WARNING] site.conf not found in $BUILDDIR/conf"
        echo "Nothing to do..."
    fi
}

######################################################
# alias function: enable ST premirror
#
stoe_source_premirror_enable() {
    if [ -e $BUILDDIR/conf/site.conf ]; then
        if [ -z "$(grep SOURCE_MIRROR_URL $BUILDDIR/conf/site.conf)" ]; then
            echo "[ERROR] missing SOURCE_MIRROR_URL definition in $BUILDDIR/conf/site.conf..."
            echo "Nothing done!"
        else
            echo ">>> ENABLE SOURCE_MIRROR_URL in $BUILDDIR/conf/site.conf"
            sed -e 's|^.*\(SOURCE_MIRROR_URL.*\)$|\1|g' -i $BUILDDIR/conf/site.conf
        fi
    else
        echo "[ERROR] missing site.conf in $BUILDDIR/conf/..."
        echo "Nothing done!"
    fi
}

######################################################
# alias function: disable sstate-cache mirror
#
stoe_sstate_mirror_disable() {
    if [ -e $BUILDDIR/conf/site.conf ]; then
        if [ -z "$(grep SSTATE_MIRRORS $BUILDDIR/conf/site.conf)" ]; then
            echo "[WARNING] no SSTATE_MIRRORS entry in site.conf from $BUILDDIR/conf/"
            echo "Nothing to do..."
        else
            echo ">>> DISABLE SSTATE_MIRRORS in $BUILDDIR/conf/site.conf"
            sed -e 's|^[ /t]*\(SSTATE_MIRRORS.*\)$|#\1|g' -i $BUILDDIR/conf/site.conf
        fi
    else
        echo "[WARNING] site.conf not found in $BUILDDIR/conf"
        echo "Nothing to do..."
    fi
}

######################################################
# alias function: enable sstate-cache mirror
#
stoe_sstate_mirror_enable() {
    if [ -e $BUILDDIR/conf/site.conf ]; then
        if [ -z "$(grep SSTATE_MIRRORS $BUILDDIR/conf/site.conf)" ]; then
            echo "[ERROR] missing SSTATE_MIRRORS definition in $BUILDDIR/conf/site.conf..."
            echo "Nothing done!"
        else
            echo ">>> ENABLE SSTATE_MIRRORS in $BUILDDIR/conf/site.conf"
            sed -e 's|^.*\(SSTATE_MIRRORS.*\)$|\1|g' -i $BUILDDIR/conf/site.conf
        fi
    else
        echo "[ERROR] missing site.conf in $BUILDDIR/conf/..."
        echo "Nothing done!"
    fi
}

######################################################
# Get ST distro code name to udpate DL and SSTATE path in site.conf
#
get_distrocodename()
{
    #get distro related folder from layer root
    local distro_dir=$(find ${ROOTOE}/$_META_LAYER_ROOT/ -type d \( -name '.git' -o -name '.repo' -o -name 'build*' -o -name 'source*' -o -name 'script*' \) -prune -o -type d -wholename '*/conf/distro' | grep '.*/conf/distro')
    if [ -z "$distro_dir" ]; then
        echo ""
        echo "[WARNING] No */conf/distro folder available in $_META_LAYER_ROOT layer"
        echo "[WARNING] Init ST_OE_DISTRO_CODENAME to NONE"
        echo ""
        _DISTRO_CODENAME="NONE"
        return
    fi

    #gather DISTRO_CODENAME values
    _DISTRO_CODENAME=$(grep -Rs '^DISTRO_CODENAME' $distro_dir | sed 's|.*DISTRO_CODENAME[ \t]*=[ \t]*"\(.*\)"[ \t]*$|\1|g'| sort -u)

    #make sure that DISTRO_CODENAME is defined and has only one value
    if [ -z "$_DISTRO_CODENAME" ] ; then
        echo ""
        echo "[ERROR] No DISTRO_CODENAME definition found in folder:"
        echo "$distro_dir"
        echo ""
        return 1
    elif [ "$(echo $_DISTRO_CODENAME | wc -w)" -gt 1 ]; then
        echo ""
        echo "[ERROR] Found different DISTRO_CODENAME definition in $_META_LAYER_ROOT layer. Please cleanup/clarify:"
        echo "$_DISTRO_CODENAME"
        echo ""
        return 1
    fi
}

######################################################
# ST EULA acceptance management based on 96boards implementation scheme:
#
# Handle EULA , if needed. This is a generic method to handle BSPs
# that might (or not) come with a EULA. If a machine has a EULA, we
# assume that its corresponding layers has conf/EULA/$MACHINE file
# with the EULA text, which we will display to the user and request
# for acceptance. If accepted, the variable ACCEPT_EULA_$MACHINE is
# set to 1 in auto.conf, which can later be used by the BSP.
# If the env variable EULA_$MACHINE is set it is used by default,
# without prompting the user.
#
# Update the _EULA_ACCEPT with proper value for update in site.conf
#
eula_check() {
    # Reset by default ACCEPT_EULA_$MACHINE to false
    _EULA_ACCEPT=0

    # Init environment bypath variable name
    # Remove any '-' and '.' from bash variable name
    local eula_machine="EULA_$(echo "$MACHINE" | sed 's/-//g;s/\.//g')"

    # Get machine path folder to check if there is an associated EULA
    local machine_file=$(find ${ROOTOE}/$_META_LAYER_BSP/ -type d \( -name '.git' -o -name '.repo' -o -name 'build*' -o -name 'source*' -o -name 'script*' \) -prune -o -type f -name "$MACHINE.conf" | grep "/machine/$MACHINE.conf")

    # Make sure machine name is uniq in _META_LAYER_ROOT to avoid detecting wrong EULA file
    if [ "$(echo $machine_file | wc -w)" -gt 1 ]; then
        echo "[ERROR] More than one $MACHINE found in ${_META_LAYER_BSP}. Please cleanup/clarify:"
        echo "${machine_file#*${ROOTOE}/}"
        echo
        return 1
    else
        # Init EULA licence file path
        local eula_file=$(echo ${machine_file} | sed 's|/machine/\(.*\)\.conf$|/eula/\1|')
    fi

    if [ -f "${eula_file}" ]; then
        # Init EULA_ENABLE for local.conf update
        _EULA_ENABLE="YES"
        # NOTE: indirect reference / dynamic variable
        if [ -n "${!eula_machine}" ]; then
            # The EULA_$MACHINE variable is set in the environment, so we just use it to bypath EULA acceptance check
            _EULA_ACCEPT=${!eula_machine}
        else
            # Ask user for EULA acceptance
            eula_askuser "${eula_file}"
        fi
    else
        echo "[WARNING] No eula file found : not able to configure EULA agreement or not."
    fi
}

######################################################
# Ask user for EULA acceptance for licence file provided
#
eula_askuser() {
    # License file provided
    local EulaFile=$1

    # Init EULA introduction text
    EulaIntroFile=$(mktemp)
    echo "\
The BSP for $MACHINE depends on packages and firmware which are covered by an \
End User License Agreement (EULA). To have the right to use these binaries \
in your images, you need to read and accept the following...\
" > $EulaIntroFile

    # Select mode to dialogue with user
    if ! [[ -z "$DISPLAY" ]] && ! [[ -z "${UI_CMD}" ]]; then
        # UI mode (through dialogue boxes)
        if (${UI_CMD} --title "EULA management" --yesno "$(cat $EulaIntroFile)" 0 0 --yes-button "Read the EULA" --no-button "EXIT"); then
            if (${UI_CMD} --title "EULA acceptance" --yesno "$(cat $EulaFile)" 0 0 --yes-button "Accept EULA" --no-button "EXIT"); then
                _EULA_ACCEPT=1
            fi
        fi
    else
        # Default console mode
        local answer=
        local wrong_answer=0
        cat $EulaIntroFile
        while [ -z "$answer" ] && [ "$wrong_answer" -lt "$TRIALMAX" ]; do
            echo -n "Would you like to read the EULA ? (y/n) "
            read -r -t $READTIMEOUT answer
            if [ "$?" -gt "128" ]; then
                echo
                echo "[WARNING] Timeout reached"
                echo "[WARNING] Default answer to 'no'."
                echo
                answer="no"
            elif (echo -n $answer | grep -q -e "^[yY][a-zA-Z]*$"); then
                answer="yes"
            elif (echo -n $answer | grep -q -e "^[nN][a-zA-Z]*$"); then
                answer="no"
            else
                answer=
                wrong_answer=$((wrong_answer+1))
                if [ "$wrong_answer" -eq "$TRIALMAX" ]; then
                    echo
                    echo "[WARNING] Maximum trials reached"
                    echo "[WARNING] Default answer to 'no'."
                    echo
                    answer="no"
                fi
            fi
        done
        if [ "$answer" == "yes" ]; then
            # Display EULA to user
            echo
            more -d "$EulaFile"
            echo
            answer=
            wrong_answer=0
            while [ -z "$answer" ] && [ "$wrong_answer" -lt "$TRIALMAX" ]; do
                echo -n "Do you accept the EULA you just read? (y/n) "
                read -r -t $READTIMEOUT answer
                if [ "$?" -gt "128" ]; then
                    echo
                    echo "[WARNING] Timeout reached"
                    echo "[WARNING] Default answer to 'no'."
                    echo
                    answer="no"
                elif (echo -n $answer | grep -q -e "^[yY][a-zA-Z]*$"); then
                    echo "EULA has been accepted."
                    _EULA_ACCEPT=1
                elif (echo -n $answer | grep -q -e "^[nN][a-zA-Z]*$"); then
                    answer="no"
                else
                    answer=
                    wrong_answer=$((wrong_answer+1))
                    if [ "$wrong_answer" -eq "$TRIALMAX" ]; then
                        echo
                        echo "[WARNING] Maximum trials reached"
                        echo "[WARNING] Default answer to 'no'."
                        echo
                        answer="no"
                    fi
                fi
            done
        fi
        if [ "$answer" == "no" ]; then
            echo "[WARNING] Configure build without support of packages under EULA acceptance."
        fi
    fi
    rm $EulaIntroFile
}

######################################################
# Apply configuration to site.conf file
#
conf_siteconf()
{
    if [ -f conf/site.conf ]; then
        echo "[WARNING] site.conf already exists. Nothing done..."
        return
    fi

    _NCPU=$(grep '^processor' /proc/cpuinfo 2>/dev/null | wc -l)
    # Sanity check that we have a valid number, if not then fallback to a safe default
    [ "$_NCPU" -ge 1 ] 2>/dev/null || _NCPU=2

    if [ -f ${_SITECONFSAMPLE_PATH}/site.conf.sample ]; then
        # Copy default site.conf.sample to conf/site.conf
        cp -f ${_SITECONFSAMPLE_PATH}/site.conf.sample conf/site.conf
        # Update site.conf with expected settings
        sed -e 's|##_DISTRO_CODENAME##|'"${_DISTRO_CODENAME}"'|g' -i conf/site.conf
        sed -e 's|##_NCPU##|'"${_NCPU}"'|g' -i conf/site.conf
        # Override default settings if requested
        if ! [ -z "$FORCE_DL_CACHEPREFIX" ]; then
            if ! [ -z "$(grep ^[#]*DL_DIR conf/site.conf)" ]; then
                sed -e 's|^[#]*DL_DIR.*|DL_DIR = "'"${FORCE_DL_CACHEPREFIX}"'/oe-downloads"|g' -i conf/site.conf
            else
                echo "# Configure download cache folder" >> conf/site.conf
                echo "DL_DIR = \"${FORCE_DL_CACHEPREFIX}/oe-downloads\"" >> conf/site.conf
            fi
        fi
        if ! [ -z "$FORCE_SSTATE_CACHEPREFIX" ]; then
            if ! [-z "$(grep ^[#]*SSTATE_DIR conf/site.conf)" ]; then
                sed -e 's|^[#]*SSTATE_DIR.*|SSTATE_DIR = "'"${FORCE_SSTATE_CACHEPREFIX}"'/oe-sstate-cache"|g' -i conf/site.conf
            else
                echo "# Configure sstate cache folder" >> conf/site.conf
                echo "SSTATE_DIR = \"${FORCE_SSTATE_CACHEPREFIX}/oe-sstate-cache\"" >> conf/site.conf
            fi
        else
            # By default set sstate dir at root of baseline to share it among all build folders
            sed -e 's|^[#]*SSTATE_DIR.*|SSTATE_DIR = "'"${ROOTOE}"'/sstate-cache"|g' -i conf/site.conf
        fi
        if ! [ -z "$FORCE_SOURCE_MIRROR_URL" ]; then
            if ! [ -z "$(grep ^[#]*SOURCE_MIRROR_URL conf/site.conf)" ]; then
                sed -e 's|^[#]*SOURCE_MIRROR_URL.*|SOURCE_MIRROR_URL = "'"${FORCE_SOURCE_MIRROR_URL}"'"|g' -i conf/site.conf
                sed -e 's|^[#]*BB_GENERATE_MIRROR_TARBALLS = \"1\"|BB_GENERATE_MIRROR_TARBALLS = \"1\"|g' -i conf/site.conf
            else
                echo "# Configure sstate cache mirror URL" >> conf/site.conf
                echo "SSTATE_MIRRORS = \"file://\.\* ${FORCE_SSTATE_MIRROR_URL}/PATH;downloadfilename=PATH\"" >> conf/site.conf
            fi
        fi
        if ! [ -z "$FORCE_SSTATE_MIRROR_URL" ]; then
            sed -e 's|^[#]*SSTATE_MIRRORS = ".*\(/PATH;.*\)"$|SSTATE_MIRRORS = "file://\.\* '"${FORCE_SSTATE_MIRROR_URL}"'\2"|g' -i conf/site.conf
        fi
    else
        echo "[INFO] No 'site.conf.sample' file available at ${_SITECONFSAMPLE_PATH}. Create default one..."
        cat >> conf/site.conf <<EOF
#
# local.conf covers user settings, site.conf covers site specific information
# such as proxy server addresses and optionally any shared download location
#

# SITE_CONF_VERSION is increased each time build/conf/site.conf
# changes incompatibly
SCONF_VERSION = "1"

EOF
        # Configure default settings if requested
        if ! [ -z "$FORCE_DL_CACHEPREFIX" ]; then
            echo "# Configure download cache folder" >> conf/site.conf
            echo "DL_DIR = \"${FORCE_DL_CACHEPREFIX}/oe-downloads\"" >> conf/site.conf
        fi
        if ! [ -z "$FORCE_SSTATE_CACHEPREFIX" ]; then
            echo "# Configure sstate cache folder" >> conf/site.conf
            echo "SSTATE_DIR = \"${FORCE_SSTATE_CACHEPREFIX}/oe-sstate-cache\"" >> conf/site.conf
        fi
        if ! [ -z "$FORCE_SOURCE_MIRROR_URL" ]; then
            echo "# Configure download cache mirror URL" >> conf/site.conf
            echo "SOURCE_MIRROR_URL = \"${FORCE_SOURCE_MIRROR_URL}\"" >> conf/site.conf
        fi
        if ! [ -z "$FORCE_SSTATE_MIRROR_URL" ]; then
            echo "# Configure sstate cache mirror URL" >> conf/site.conf
            echo "SSTATE_MIRRORS = \"file://\.\* ${FORCE_SSTATE_MIRROR_URL}/PATH;downloadfilename=PATH\"" >> conf/site.conf
        fi
    fi
}

######################################################
# Apply configuration to local.conf file
#
conf_localconf()
{
    if [ -z "$(grep '^MACHINE =' conf/local.conf)" ]; then
        # Apply selected MACHINE in local conf file
        sed -e 's/^\(MACHINE.*\)$/#\1\nMACHINE = "'"$MACHINE"'"/' -i conf/local.conf
    else
        echo "[WARNING] MACHINE is already set in local.conf. Nothing done..."
    fi
    if [ -z "$(grep '^DISTRO =' conf/local.conf)" ]; then
        # Apply selected DISTRO in local conf file
        sed -e 's/^\(DISTRO.*\)$/#\1\nDISTRO = "'"$DISTRO"'"/' -i conf/local.conf
    else
        echo "[WARNING] DISTRO is already set in local.conf. Nothing done..."
    fi

    # Update local.conf with specific settings for EULA management
    if [ "$_EULA_ENABLE" == "YES" ]; then
        cat >> conf/local.conf <<EOF

# =========================================================================
# Set EULA acceptance
# =========================================================================
ACCEPT_EULA_$MACHINE = "${_EULA_ACCEPT}"
EOF
    fi

    if ! [ -z "$SDKMACHINE" ]; then
        # Apply specified SDKMACHINE in local conf file
        if [ -z "$(grep '^SDKMACHINE =' conf/local.conf)" ]; then
            sed -e 's/^[ #]\(SDKMACHINE .*\)$/#\1\nSDKMACHINE = "'"${SDKMACHINE}"'"/' -i conf/local.conf
        else
            echo "[WARNING] SDKMACHINE is already set in local.conf. Nothing done..."
        fi
    fi
    if ! [ -z "$ST_ARCHIVER_ENABLE" ]; then
        # Enable ST_ARCHIVER_ENABLE flag in local conf file
        if [ -z "$(grep '^ST_ARCHIVER_ENABLE =' conf/local.conf)" ]; then
            if [ -z "$(grep 'ST_ARCHIVER_ENABLE =' conf/local.conf)" ]; then
                # Append ST_ARCHIVER_ENABLe flag to local.conf file
                cat >> conf/local.conf <<EOF

ST_ARCHIVER_ENABLE = "1"
EOF
            else
                # Update local.conf file for ST_ARCHIVER_ENABLE flag
                sed -e 's/^.*ST_ARCHIVER_ENABLE .*$/ST_ARCHIVER_ENABLE = \"1\"/g' -i conf/local.conf
            fi
        else
            echo "[WARNING] ST_ARCHIVER_ENABLE is already set in local.conf. Nothing done..."
        fi
    fi

    if ! [ -z "$OPENSTLINUX_RELEASE" ]; then
        # Apply OPENSTLINUX_RELEASE flag: no '/' allowed, so truncate after last '/' if any
        if [ -z "$(grep '^OPENSTLINUX_RELEASE =' conf/local.conf)" ]; then
            cat >> conf/local.conf <<EOF

# =========================================================================
# Set OpenSTLinux release flag
# =========================================================================
OPENSTLINUX_RELEASE = "${OPENSTLINUX_RELEASE##*/}"
EOF
        else
            echo "[WARNING] OPENSTLINUX_RELEASE is already set in local.conf. Nothing done..."
        fi
    fi
}

######################################################
# Apply configuration to bblayer.conf file
#
conf_bblayerconf()
{
    local _MACH_CONF=$(find ${ROOTOE}/$_META_LAYER_BSP/ -type d \( -name '.git' -o -name '.repo' -o -name 'build*' -o -name 'source*' -o -name 'script*' \) -prune -o -type f -name "$MACHINE.conf" | grep "/machine/$MACHINE.conf")

    if [ -n "${_MACH_CONF}" ]; then
        # Get meta layer root for selected machine file
        local _BSP_LAYER_REQUIRED=$(echo ${_MACH_CONF} | sed -n 's|.*'"$_META_LAYER_BSP"'\/\(.*\)\/conf\/machine\/.*|\1|p')
        # Get any specific needed layer in machine file
        local _LAYERS=$(grep '^#@NEEDED_BSPLAYERS:' $_MACH_CONF)
        local _BSP=$(echo ${_LAYERS} |cut -f 2 -d ':')
        # Append any required layer list to _BSP list
        if [ -n "$BSP_DEPENDENCY" ]; then
            _BSP="$BSP_DEPENDENCY $_BSP"
        fi
        if [ -n "${_BSP}" ]; then
            cat >> conf/bblayers.conf <<EOF
# BSP dependencies"
EOF

            for bsp in $_BSP; do
                bsp_to_add=$(echo $bsp | tr -d ' ')
                cat >> conf/bblayers.conf <<EOF
BBLAYERS =+ "${ROOTOE}/$bsp_to_add"
EOF
            done
        fi
        if [ -n "$_BSP_LAYER_REQUIRED" -a \
            "${_LAYERS#*${_BSP_LAYER_REQUIRED}}" = "${_LAYERS}" -a \
            "$(grep "${_BSP_LAYER_REQUIRED}" conf/bblayers.conf)" == "" ]; then
            cat >> conf/bblayers.conf <<EOF

# specific bsp selected
BBLAYERS =+ "${ROOTOE}/$_META_LAYER_BSP/$_BSP_LAYER_REQUIRED"
EOF
        fi
    else
        echo "[WARNING] Not able to find ${MACHINE}.conf file in ${_META_LAYER_BSP} : bblayer.conf not updated..."
    fi
}

######################################################
# Copy 'conf-notes.txt' from available template files to BUILDDIR
#
conf_notes()
{
    if [ -f ${_TEMPLATECONF}/conf-notes.txt ]; then
        cp ${_TEMPLATECONF}/conf-notes.txt conf
    elif [ -z "${_TEMPLATECONF}" ]; then
        # '_TEMPLATECONF' is empty when dealing with 'nodistro' use case
        # Copy then the default OE 'conf-notes.txt' file
        if [ -f ${ROOTOE}/$_BUILDSYSTEM/meta/conf/conf-notes.txt ]; then
            cp ${ROOTOE}/$_BUILDSYSTEM/meta/conf/conf-notes.txt conf
        fi
    fi
}

######################################################
# get folder to use for template.conf files
#
get_templateconf()
{
    if [ "$DISTRO" = "nodistro" ]; then
        #for nodistro choice use default sample files from openembedded-core
        echo ""
        echo "[WARNING] Using default openembedded template configuration files for '$DISTRO' setting."
        echo ""
        _TEMPLATECONF=""
    else
        #extract bsp path
        local distro_path=$(find ${ROOTOE}/$_META_LAYER_ROOT/ -type d \( -name '.git' -o -name '.repo' -o -name 'build*' -o -name 'source*' -o -name 'script*' \) -prune -o -type f -name "$DISTRO.conf" | grep "/distro/$DISTRO.conf" | sed 's|\(.*\)/conf/distro/\(.*\)|\1|')
        if [ -z "$distro_path" ]; then
            echo ""
            echo "[ERROR] No '$DISTRO.conf' file available in $_META_LAYER_ROOT"
            echo ""
            return 1
        fi
        #make sure path is single
        if [ "$(echo $distro_path | wc -w)" -gt 1 ]; then
            echo ""
            echo "[ERROR] Found multiple '$DISTRO.conf' file in $_META_LAYER_ROOT"
            echo ""
            return 1
        fi
        #configure _TEMPLATECONF path
        if [ -f $distro_path/conf/template/bblayers.conf.sample ]; then
            _TEMPLATECONF=$distro_path/conf/template
        else
            echo "[WARNING] default template configuration files not found in $_META_LAYER_ROOT layer: using default ones from openembedded"
            _TEMPLATECONF=""
        fi
    fi
}

######################################################
# Check last modified time for bblayers.conf from list of builddir provided and
# provide builddir that contains the latest bblayers.conf modified
#
_default_config_get() {
    local list=$1
    TmpFile=$(mktemp)
    for l in $list
    do
        [ -f ${ROOTOE}/$l/conf/bblayers.conf ] && echo $(stat -c %Y ${ROOTOE}/$l/conf/bblayers.conf) $l >> $TmpFile
    done
    cat $TmpFile | sort -r | head -n1 | cut -d' ' -f2
    rm -f $TmpFile
}

######################################################
# Init timestamp on bblayers.conf for builddir set
#
_default_config_set() {
    [ -f $BUILDDIR/conf/bblayers.conf ] && touch $BUILDDIR/conf/bblayers.conf
}


######################################################
# Format DISTRO and MACHINE list from configuration file list applying the specific _FORMAT_PATTERN:
#  <CONFIG-NAME>|<_FORMAT_PATTERN>|<CONFIG-DESCRIPTION>
#
_choice_formated_configs() {
    local choices=$(find ${ROOTOE}/$_META_LAYER_ROOT/ -type d \( -name '.git' -o -name '.repo' -o -name 'build*' -o -name 'source*' -o -name 'script*' \) -prune -o -type f -wholename "*/conf/$1/*.conf" 2>/dev/null | grep ".*/conf/$1/.*\.conf" | sort | uniq)

    for ITEM in $choices; do
        if [ -z "$(grep '#@DESCRIPTION' $ITEM)" ]; then
            echo $ITEM | sed 's|^'"${ROOTOE}/$_META_LAYER_ROOT"'/\(.*\)/conf/'"$1"'/\(.*\)\.conf|\2'"${_FORMAT_PATTERN}"'[ERROR] No Description available (\1)|'
        else
            grep -H "#@DESCRIPTION" $ITEM | sed 's|^.*/\(.*\)\.conf:#@DESCRIPTION:[ \t]*\(.*$\)|\1'"${_FORMAT_PATTERN}"'\2|'
        fi
    done
    unset ITEM
}

######################################################
# Format BUILD_DIR list from applying the specific _FORMAT_PATTERN:
#  <DIR-NAME>|<_FORMAT_PATTERN>|<DISTRO-value and MACHINE-value>
#
_choice_formated_dirs() {
    TmpFile=$(mktemp)
    for dir in $1
    do
        echo "${dir}${_FORMAT_PATTERN}DISTRO is '$(_stoe_config_read ${ROOTOE}/$dir DISTRO)' and MACHINE is '$(_stoe_config_read ${ROOTOE}/$dir MACHINE)'" >> $TmpFile
    done
    # Add new build config option
    echo "NEW${_FORMAT_PATTERN}*** SET NEW DISTRO AND MACHINE BUILD CONFIG ***" >> $TmpFile
    echo "$(cat $TmpFile)"
    rm -f $TmpFile
}

######################################################
# Make selection for <TARGET> requested from <LISTING> provided using shell or ui choice
#
_choice_shell() {
    local choice_name=$1
    local choice_list=$2
    local default_choice=$3
    #format list to have display aligned on column with '-' separation between name and description
    local options=$(echo "${choice_list}" | column -t -s "£")
    #change separator from 'space' to 'end of line' for 'select' command
    old_IFS=$IFS
    IFS=$'\n'
    local i=1
    unset LAUNCH_MENU_CHOICES
    for opt in $options; do
        printf "%3.3s. %s\n" $i $opt
        LAUNCH_MENU_CHOICES=(${LAUNCH_MENU_CHOICES[@]} $opt)
        i=$(($i+1))
    done
    IFS=$old_IFS
    # Item selection from list
    local selection=""
    # Init default_choice if not already provided
    [ -z "${default_choice}" ] && default_choice=$(echo ${LAUNCH_MENU_CHOICES[0]} | cut -d' ' -f1)
    while [ -z "$selection" ]; do
        echo -n "Which one would you like? [${default_choice}] "
        read -r -t $READTIMEOUT answer
        # Check that user has answered before timeout, else break
        test "$?" -gt "128" && break

        if [ -z "$answer" ]; then
            selection=${default_choice}
            break
        fi
        if [[ $answer =~ ^[0-9]+$ ]]; then
            if [ $answer -gt 0 ] && [ $answer -le ${#LAUNCH_MENU_CHOICES[@]} ]; then
                selection=${LAUNCH_MENU_CHOICES[$(($answer-1))]}
                break
            fi
        fi
        echo "Invalid choice: $answer"
        echo "Please use numeric value between '1' and '$(echo "$options" | wc -l)'"
    done
    eval ${choice_name}=$(echo $selection | cut -d' ' -f1)
}

_choice_ui() {
    local choice_name=$1
    local choice_list=$2
    local default_choice=$3
    local target=""
    local _help_display=true
    #change separator from 'space' to 'end of line' to get full line
    old_IFS=$IFS
    IFS=$'\n'
    for ITEM in ${choice_list}; do
        local target_name=$(echo $ITEM | awk -F''"${_FORMAT_PATTERN}"'' '{print $1}')
        local target_desc=$(echo $ITEM | awk -F''"${_FORMAT_PATTERN}"'' '{print $NF}')
        local target_stat="OFF"
        # Set selection ON for default_choice
        [ "$target_name" = "$default_choice" ] && target_stat="ON"
        TARGETTABLE+=($target_name "$target_desc" $target_stat)
    done
    IFS=$old_IFS
    while [[ -z "$target" ]]
    do
        target=$(${UI_CMD} --title "Available ${choice_name}" --radiolist "Please choose a ${choice_name}" 0 0 0 "${TARGETTABLE[@]}" 3>&1 1>&2 2>&3)
        test -z $target || break
        if $_help_display; then
            #display dialog box to provide some help to user
            ${UI_CMD} --title "How to select ${choice_name}" --msgbox "Keyboard usage:\n\n'ENTER' to validate\n'SPACE' to select\n 'TAB'  to navigate" 0 0
            _help_display=false
        else
            break
        fi
    done
    unset TARGETTABLE
    unset ITEM
    eval ${choice_name}=$target
}

choice() {
    local __TARGET=$1
    local choices="$2"
    local default_choice=$3
    echo "[$__TARGET configuration]"
    if [[ $(echo "$choices" | wc -l) -eq 1 ]]; then
        eval $__TARGET=$(echo $choices | awk -F''"${_FORMAT_PATTERN}"'' '{print $1}')
    else
        if ! [[ -z "$DISPLAY" ]] && ! [[ -z "${UI_CMD}" ]]; then
            _choice_ui $__TARGET "$choices" $default_choice
        else
            _choice_shell $__TARGET "$choices" $default_choice
        fi
    fi
    echo "Selected $__TARGET: $(eval echo \$$__TARGET)"
    echo ""
}

######################################################
# Check if current HOST is one of the Linux Distrib Release supported
#
_stoe_distrib_check() {
    # Init UbuntuRelease text
    UbuntuReleaseFile=$(mktemp)
    # Set default return value
    local return_value=0
    # Set lsb-release file
    local lsb_release_file=/etc/lsb-release
    # Init host info
    local host_distrib="Not checked"
    local host_release="Not checked"
    if [ -f $lsb_release_file ]; then
        # Check for host Linux Distrib
        host_distrib=$(grep '^DISTRIB_ID=' $lsb_release_file | cut -d'=' -f2)
        if [ "$host_distrib" != "${_SUPPORTED_LINUX_DISTRIB}" ]; then
            echo "[WARNING] Not supported Linux Distrib detected in $lsb_release_file file" > $UbuntuReleaseFile
            echo "[WARNING] ($host_distrib)" >> $UbuntuReleaseFile
            return_value=1
        else
            # Check for host Linux Distrib Release
            host_release=$(grep '^DISTRIB_RELEASE=' $lsb_release_file | cut -d'=' -f2)
            # Init release support info
            local supported_release="no"
            for release in ${_SUPPORTED_UBUNTU_RELEASE}
            do
                [ "$host_release" = "$release" ] && supported_release="yes"
            done
            if [ "$supported_release" = "no" ]; then
                echo "[WARNING] Not supported ${_SUPPORTED_LINUX_DISTRIB} Release detected in $lsb_release_file file" > $UbuntuReleaseFile
                echo "[WARNING] ($host_release)" >> $UbuntuReleaseFile
                return_value=1
            fi
        fi
    else
        echo "[WARNING] Skip checking for Linux Distrib Release support." > $UbuntuReleaseFile
        echo "[WARNING] (missing $lsb_release_file file on host)" >> $UbuntuReleaseFile
        return_value=1
    fi
    # Display host checking info
    echo "Linux Distrib: $host_distrib"
    echo "Linux Release: $host_release"
    echo ""
    # Check for user acknowledgment
    if [ "$return_value" -eq 1 ]; then
        # Amend warning message with default Linux Distrib support and advice
        echo "" >> $UbuntuReleaseFile
        echo "ST recommands to use one of the following distributions (validated by ST)" >> $UbuntuReleaseFile
        echo "    DISTRIB: ${_SUPPORTED_LINUX_DISTRIB}" >> $UbuntuReleaseFile
        echo "    RELEASE: ${_SUPPORTED_UBUNTU_RELEASE}" >> $UbuntuReleaseFile
        echo "Feel free to update your distribution, or to ignore the WARNING (at your risk)..." >> $UbuntuReleaseFile
        # Select mode to dialogue with user
        if ! [[ -z "$DISPLAY" ]] && ! [[ -z "${UI_CMD}" ]]; then
            # UI mode (through dialogue boxes)
            if (${UI_CMD} --title "UBUNTU RELEASE SUPPORT" --yesno "$(cat $UbuntuReleaseFile)" 0 0 --yes-button "IGNORE WARNING" --no-button "EXIT"); then
                return_value=0
            fi
        else
            # Default console mode
            local answer=
            local wrong_answer=0
            cat $UbuntuReleaseFile
            while [ -z "$answer" ] && [ "$wrong_answer" -lt "$TRIALMAX" ]; do
                echo -n "Would you ignore this warning ? (y/n) "
                read -r -t $READTIMEOUT answer
                if [ "$?" -gt "128" ]; then
                    echo
                    echo "[WARNING] Timeout reached"
                    echo "[WARNING] Default answer to 'no'."
                    echo
                    answer="no"
                elif (echo -n $answer | grep -q -e "^[yY][a-zA-Z]*$"); then
                    answer="yes"
                elif (echo -n $answer | grep -q -e "^[nN][a-zA-Z]*$"); then
                    answer="no"
                else
                    answer=
                    wrong_answer=$((wrong_answer+1))
                    if [ "$wrong_answer" -eq "$TRIALMAX" ]; then
                        echo
                        echo "[WARNING] Maximum trials reached"
                        echo "[WARNING] Default answer to 'no'."
                        echo
                        answer="no"
                    fi
                fi
            done
            # Convert answer to expected return value
            if [ "$answer" == "yes" ]; then
                return_value=0
            fi
        fi
    fi
    # Remove temporary file
    rm -f $UbuntuReleaseFile
    return $return_value
}

######################################################
# Since this script is sourced, be careful not to pollute
# caller's environment with temp variables.
#
_stoe_unset() {
    unset BUILD_DIR
    unset DISTRO
    unset DISTRO_INIT
    unset MACHINE
    unset MACHINE_INIT
    unset _DL_CACHEPREFIX
    unset _SSTATE_CACHEPREFIX
    unset _NCPU
    unset _SITECONFSAMPLE_PATH
    unset _FORCE_RECONF
    unset _ENABLE_UI
    unset _INIT
    unset _BUILDSYSTEM
    unset _QUIET
    unset UI_CMD
    unset _TEMPLATECONF
    unset _DISTRO_CODENAME
    unset _EULA_ACCEPT
    unset _EULA_ENABLE
    unset _SUPPORTED_LINUX_DISTRIB
    unset _SUPPORTED_UBUNTU_RELEASE
    unset BSP_DEPENDENCY
    # Clean env from unwanted functions
    unset -f choice
    unset -f _choice_ui
    unset -f _choice_shell
    unset -f _verify_env
    unset -f _choice_formated_dirs
    unset -f _choice_formated_configs
    unset -f get_templateconf
    unset -f conf_bblayerconf
    unset -f conf_localconf
    unset -f conf_siteconf
    unset -f conf_notes
    unset -f _stoe_set_env_init
    unset -f stoe_set_env
    unset -f _default_config_get
    unset -f _default_config_set
    unset -f get_distrocodename
    unset -f eula_check
    unset -f eula_askuser
    unset -f _stoe_distrib_check
    # Delete File
    rm -f ${LISTDIR}
}

######################################################
# Check if script is sourced as expected
#
_verify_env() {
    local  __resultvar=$1
    if [ "$0" = "$BASH_SOURCE" ]; then
        echo "###################################"
        echo "[ERROR] YOU MUST SOURCE the script"
        echo "###################################"
        if [[ "$__resultvar" ]]; then
            eval $__resultvar="ERROR_SOURCE"
        fi
        return
    fi
    # check that we are not root!
    if [ "$(whoami)" = "root" ]; then
        echo -e "\n[ERROR] do not use the BSP as root. Exiting..."
        if [[ "$__resultvar" ]]; then
            eval $__resultvar="ERROR_ROOT"
        fi
        return
    fi
    # check that we are where we think we are!
    local oe_tmp_pwd=$(pwd)
    # need to take care of build system available
    if [[ ! -d $oe_tmp_pwd/layers/openembedded-core ]] && [[ ! -d $oe_tmp_pwd/layers/poky ]]; then
        echo "PLEASE launch the envsetup script at root tree of your oe sdk"
        echo ""
        local oe_tmp_root=$oe_tmp_pwd
        while [ 1 ];
        do
            oe_tmp_root=$(dirname $oe_tmp_root)
            if [ "$oe_tmp_root" == "/" ]; then
                echo "[WARNING]: you try to launch the script outside oe sdk tree"
                break;
            fi
            if [[ -d $oe_tmp_root/layers/openembedded-core ]] || [[ -d $oe_tmp_root/layers/poky ]]; then
                echo "Normally at this location: $oe_tmp_root"
                break;
            fi
        done
        if [[ "$__resultvar" ]]; then
            eval $__resultvar="ERROR_OE"
        fi
        return
    else
        # Fix build system to use for init: default would be openembedded-core one
        [[ -d $oe_tmp_pwd/layers/poky ]] && _BUILDSYSTEM=layers/poky
        [[ -d $oe_tmp_pwd/layers/openembedded-core ]] && _BUILDSYSTEM=layers/openembedded-core
    fi
    if [[ "$__resultvar" ]]; then
        eval $__resultvar="NOERROR"
    fi
}

######################################################
# Main
# --
#

#----------------------------------------------
# Make sure script has been sourced
#
_verify_env ret
case $ret in
    ERROR_OE | ERROR_ROOT | ERROR_SOURCE)
        if [ "$0" != "$BASH_SOURCE" ]; then
            return 2
        else
            exit 2
        fi
        ;;
    *)
        ;;
esac

# Init parameters
_ENABLE_UI=1
READTIMEOUT=${READTIMEOUT:-60}
TRIALMAX=${TRIALMAX:-100}

#----------------------------------------------
# parsing options
#
while test $# != 0
do
    case "$1" in
    --help)
        stoe_help
        _stoe_utilities
        return 1
        ;;
    --quiet)
        _QUIET=1
        ;;
    --reset)
        _FORCE_RECONF=1
        _INIT=0
        ;;
    --no-ui)
        _ENABLE_UI=0
        ;;
    -*)
        echo "Wrong parameter: $1"
        return 1
        ;;
    *)
        #change buildir directory
        if ! [[ $1 =~ ^build.* ]]; then
            echo "[ERROR] '$1' : please provide BUILD_DIR with 'build' prefix."
            return 1
        fi
        #we want BUILD_DIR without any '/' at the end
        BUILD_DIR=$(echo $1 | sed 's|[/]*$||')
        ;;
    esac
    shift
done

#----------------------------------------------
# Init env variable
#
_stoe_set_env_init

#----------------------------------------------
# Check if Linux Distrib is supported
#
echo "[HOST DISTRIB check]"
_stoe_distrib_check
test "$?" == "1" && { echo "Check aborted: exiting now..."; _stoe_unset; return 1; }

#----------------------------------------------
# Init BUILD_DIR variable
#
if [ -z "${BUILD_DIR}" ] && ! [ -z "$DISTRO" ] && ! [ -z "$MACHINE" ]; then
    # In case DISTRO and MACHINE are provided use them to init BUILD_DIR
    BUILD_DIR="build-${DISTRO//-}-$MACHINE"
fi

if [ -z "${BUILD_DIR}" ]; then
    # Get existing BUILD_DIR list from baseline
    LISTDIR=$(mktemp)
    for l in $(find ${ROOTOE} -maxdepth 1 -wholename "*/build*"); do
        test -f ${l}/conf/local.conf && echo ${l#*${ROOTOE}/} >> ${LISTDIR}
    done
    # Select any existing BUILD_DIR from list
    if  [ -s ${LISTDIR} ]; then
        choice BUILD_DIR "$(_choice_formated_dirs "$(cat ${LISTDIR} | sort)")" $(_default_config_get "$(cat ${LISTDIR} | sort)")
        [ -z "${BUILD_DIR}" ] && { echo "Selection escaped: exiting now..."; _stoe_unset; return 1; }
    fi
    # Reset BUILD_DIR in case for new config choice
    test "${BUILD_DIR}" == "NEW" && BUILD_DIR=""
else
    # Check if configuration files exist to force or not INIT
    test -f ${ROOTOE}/${BUILD_DIR}/conf/bblayers.conf || _INIT=1
    test -f ${ROOTOE}/${BUILD_DIR}/conf/local.conf || _INIT=1
fi

if [[ $_INIT -eq 1 ]] || [[ -z "${BUILD_DIR}" ]]; then
    # There is no available config in baseline: force init from scratch
    _INIT=1

    # Set DISTRO
    if [ -z "$DISTRO" ]; then
        DISTRO_CHOICES=$(_choice_formated_configs distro)
        test "$?" == "1" && { echo "$DISTRO_CHOICES"; _stoe_unset; return 1; }
        # Add nodistro option
        DISTRO_CHOICES=$(echo -e "$DISTRO_CHOICES\nnodistro${_FORMAT_PATTERN}*** DEFAULT OPENEMBEDDED SETTING : DISTRO is not defined ***")
        choice DISTRO "$DISTRO_CHOICES"
        [ -z "$DISTRO" ] && { echo "Selection escaped: exiting now..."; _stoe_unset; return 1; }
    fi

    # Set MACHINE
    if [ -z "$MACHINE" ]; then
        MACHINE_CHOICES=$(_choice_formated_configs machine)
        test "$?" == "1" && { echo "$MACHINE_CHOICES"; _stoe_unset; return 1; }
        choice MACHINE "$MACHINE_CHOICES"
        [ -z "$MACHINE" ] && { echo "Selection escaped: exiting now..."; _stoe_unset; return 1; }
    fi

    # Init BUILD_DIR if not yet set
    test -z "${BUILD_DIR}" && BUILD_DIR="build-${DISTRO//-}-$MACHINE"

    # Check if BUILD_DIR already exists to use previous config (i.e. set _INIT to 0)
    test -f ${ROOTOE}/${BUILD_DIR}/conf/bblayers.conf && _INIT=0
    test -f ${ROOTOE}/${BUILD_DIR}/conf/local.conf && _INIT=0

else
    # Get DISTRO and MACHINE from configuration file
    DISTRO_INIT=$(_stoe_config_read ${ROOTOE}/${BUILD_DIR} DISTRO)
    MACHINE_INIT=$(_stoe_config_read ${ROOTOE}/${BUILD_DIR} MACHINE)

    # If DISTRO value is not set in conf file, then default to nodistro
    [[ ${DISTRO_INIT} =~ \< ]] && DISTRO_INIT="nodistro"

    # Set DISTRO
    if [ -z "$DISTRO" ]; then
        DISTRO=${DISTRO_INIT}
    elif [ "$DISTRO" != "${DISTRO_INIT}" ]; then
        # User has defined a wrong DISTRO for current BUILD_DIR configuration
        echo "[ERROR] DISTRO $DISTRO does not match "${DISTRO_INIT}" already set in ${BUILD_DIR}"
        _stoe_unset
        return 1
    fi

    # Set MACHINE
    if [ -z "$MACHINE" ]; then
        MACHINE=${MACHINE_INIT}
    elif [ "$MACHINE" != "${MACHINE_INIT}" ]; then
        # User has defined a wrong MACHINE for current BUILD_DIR configuration
        echo "[ERROR] MACHINE $MACHINE does not match "${MACHINE_INIT}" already set in ${BUILD_DIR}"
        _stoe_unset
        return 1
    fi
fi

#----------------------------------------------
# Init baseline for full INIT if required
#
if [[ $_FORCE_RECONF -eq 1 ]] && [[ $_INIT -eq 0 ]]; then
    echo ""
    echo "[Removing current config from ${ROOTOE}/${BUILD_DIR}/conf]"
    rm -fv ${ROOTOE}/${BUILD_DIR}/conf/*.conf ${ROOTOE}/${BUILD_DIR}/conf/*.txt
    echo ""
    # Force init to generate configuration files
    _INIT=1
fi

#----------------------------------------------
# Standard Openembedded init
#
echo -en "[source $_BUILDSYSTEM/oe-init-build-env]"
[[ $_INIT -eq 1 ]] && echo "[from nothing]"
[[ $_INIT -eq 0 ]] && echo "[with previous config]"
get_templateconf
test "$?" == "1" && { _stoe_unset; return 1; }
TEMPLATECONF=${_TEMPLATECONF} source ${ROOTOE}/$_BUILDSYSTEM/oe-init-build-env ${BUILD_DIR} > /dev/null
test "$?" == "1" && { _stoe_unset; return 1; }

#----------------------------------------------
# Init ST DISTRO CODE NAME to use for DL_DIR and SSTATE_DIR path
#
get_distrocodename
test "$?" == "1" && { rm -rf $BUILDDIR/conf/*; _stoe_unset; return 1; }

#----------------------------------------------
# Handle EULA acceptance for ST configurations
#
if [[ $_INIT -eq 1 ]]; then
    echo
    echo "[EULA configuration]"
    eula_check
    test "$?" == "1" && { rm -rf $BUILDDIR/conf/*; _stoe_unset; return 1; }
fi

#----------------------------------------------
# Apply specific ST configurations
#
if [[ $_INIT -eq 1 ]]; then
    echo
    echo "[Configure *.conf files]"
    # Configure site.conf with specific settings
    conf_siteconf
    # Configure local.conf with specific settings
    conf_localconf
    # Update bblayer.conf with specific machine bsp layer path
    conf_bblayerconf
    # Copy specific 'conf-notes.txt' file from templateconf to BUILDDIR
    conf_notes
fi

#----------------------------------------------
# Init ST environment variables
#
stoe_set_env

#----------------------------------------------
# Display when no quiet mode required
#
if ! [[ $_QUIET -eq 1 ]]; then
    # Display current configs
    stoe_config_summary $BUILDDIR

    # Display available images
    if [ -f $BUILDDIR/conf/conf-notes.txt ]; then
        cat $BUILDDIR/conf/conf-notes.txt
    else
        stoe_list_images ${ROOTOE}/${_META_LAYER_ROOT} NOERR FILTER
        test "$?" == "1" && { _stoe_unset; return 1; }
    fi
    echo ""
    echo "You can now run 'bitbake <image>'"
    echo ""
fi

#----------------------------------------------
# Init timestamp for default builddir choice
#
_default_config_set

#----------------------------------------------
# Clear user's environment from temporary variables
#
_stoe_unset
