#@DESCRIPTION: phycore-stm32mp1-2 machine extlinux file configuration

# -----------------------------------------------------------------------------
# Append specific examples configs
# -----------------------------------------------------------------------------
# Define new label(s) to append to the label list from current target
# UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG[extra-target] = "newlabel1 newlabel2"
# This will create new 'extlinux.conf' file with name 'extra-target_extlinux.conf'
# that will use UBOOT_EXTLINUX_LABELS label list defined for current target plus
# the 'newlabel1' and 'newlabel2' labels.
# -----------------------------------------------------------------------------
# Define extra label configuration
UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG[phycore-stm32mp1-2] += "phycore-stm32mp1-2-a7-examples"
UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG[phycore-stm32mp1-2] += "phycore-stm32mp1-2-m4-examples"

# -----------------------------------------------------------------------------
# Configure A7 examples labels
# -----------------------------------------------------------------------------
# Define MENU DESCRIPTION overrides for new A7 labels
UBOOT_EXTLINUX_MENU_DESCRIPTION_phycore-stm32mp1-2-a7-examples = "phycore-stm32mp1-2-a7-examples"
# Define FDT overrides for new A7 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-a7-examples = "/phycore-stm32mp1-2-a7-examples.dtb"

# -----------------------------------------------------------------------------
# Configure M4 examples labels
# -----------------------------------------------------------------------------
# Define MENU DESCRIPTION overrides for new M4 labels
UBOOT_EXTLINUX_MENU_DESCRIPTION_phycore-stm32mp1-2-m4-examples = "phycore-stm32mp1-2-m4-examples"
# Define FDT overrides for new M4 labels
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2-m4-examples = "/phycore-stm32mp1-2-m4-examples.dtb"

# -----------------------------------------------------------------------------
# Define FDT override for Default label
# -----------------------------------------------------------------------------
UBOOT_EXTLINUX_FDT_phycore-stm32mp1-2 = "${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', '', '/phycore-stm32mp1-2.dtb', d)}"
