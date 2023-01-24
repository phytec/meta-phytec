#@DESCRIPTION: phycore-stm32mp1-6 machine extlinux file configuration

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
UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG[stm32mp153a-phycore-som-nand-512mb] += "phycore-stm32mp1-6-a7-examples"
UBOOT_EXTLINUX_TARGETS_EXTRA_CONFIG[stm32mp153a-phycore-som-nand-512mb] += "phycore-stm32mp1-6-m4-examples"

# -----------------------------------------------------------------------------
# Configure A7 examples labels
# -----------------------------------------------------------------------------
# Define MENU DESCRIPTION overrides for new A7 labels
UBOOT_EXTLINUX_MENU_DESCRIPTION:phycore-stm32mp1-6-a7-examples = "stm32mp153a-phyboard-sargas-rdk-nand-a7-examples"
# Define FDT overrides for new A7 labels
UBOOT_EXTLINUX_FDT:phycore-stm32mp1-6-a7-examples = "/${LINUX_A7_EXAMPLES_DT}.dtb"

# -----------------------------------------------------------------------------
# Configure M4 examples labels
# -----------------------------------------------------------------------------
# Define MENU DESCRIPTION overrides for new M4 labels
UBOOT_EXTLINUX_MENU_DESCRIPTION:phycore-stm32mp1-6-m4-examples = "stm32mp153a-phyboard-sargas-rdk-nand-m4-examples"
# Define FDT overrides for new M4 labels
UBOOT_EXTLINUX_FDT:phycore-stm32mp1-6-m4-examples = "/${CUBE_M4_EXAMPLES_DT}.dtb"

# -----------------------------------------------------------------------------
# Define FDT override for Default label
# -----------------------------------------------------------------------------
UBOOT_EXTLINUX_FDT:phycore-stm32mp1-6 = "${@bb.utils.contains('MACHINE_FEATURES', 'phy-expansions', '', '/${KERNEL_DT}.dtb', d)}"
