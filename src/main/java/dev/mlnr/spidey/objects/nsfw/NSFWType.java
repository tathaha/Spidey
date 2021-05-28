package dev.mlnr.spidey.objects.nsfw;

public enum NSFWType {
	ANAL("Anal", "anal"),
	ASIANS("Asians", "asiansgonewild"),
	ASS("Ass", "ass"),
	BLOWJOBS("Blowjobs", "blowjobs"),
	BOOBS("Boobs", "boobs"),
	CUMSLUTS("Cumsluts", "cumsluts"),
	LEGAL_TEENS("Legal teens", "legalteens"),
	LESBIANS("Lesbians", "lesbians"),
	MILF("Milf", "milf"),
	NSFW("NSFW", "nsfw"),
	PUSSY("Pussy", "pussy");

	private final String friendlyName;
	private final String subreddit;

	NSFWType(String friendlyName, String subreddit) {
		this.friendlyName = friendlyName;
		this.subreddit = subreddit;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getSubreddit() {
		return subreddit;
	}
}