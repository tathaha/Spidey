package dev.mlnr.spidey.objects.timeout;

import dev.mlnr.spidey.objects.commands.slash.ChoicesEnum;
import dev.mlnr.spidey.utils.StringUtils;

public enum DurationUnit implements ChoicesEnum {
	MINUTES,
	HOURS,
	DAYS,
	WEEKS;

	private final String friendlyName;

	DurationUnit() {
		this.friendlyName = StringUtils.humanizeEnumEntry(this);
	}

	@Override
	public String getFriendlyName() {
		return friendlyName;
	}
}