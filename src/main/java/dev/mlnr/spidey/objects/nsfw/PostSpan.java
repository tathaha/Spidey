package dev.mlnr.spidey.objects.nsfw;

import dev.mlnr.spidey.objects.commands.slash.ChoicesEnum;

public enum PostSpan implements ChoicesEnum {
	HOUR("Past hour"),
	DAY("Past 24 hours"),
	WEEK("Past week"),
	MONTH("Past month"),
	YEAR("Past year"),
	ALL("All time");

	private final String friendlyName;

	PostSpan(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	@Override
	public String getFriendlyName() {
		return friendlyName;
	}
}