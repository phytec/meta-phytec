# HACK: optee-client installs tee-udev.rules rules for group 'tee'. However, if
# group 'tee' doesn't exist in the system we get following error in the journal
# log: systemd-udevd[163]: /usr/lib/udev/rules.d/tee-udev.rules:7 Unknown group
# 'tee', ignoring. Fix it by adding the 'tee' group to the system.
GROUPADD_PARAM:${PN}:append = "; --system tee"
