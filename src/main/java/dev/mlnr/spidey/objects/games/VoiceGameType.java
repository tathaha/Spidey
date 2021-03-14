package dev.mlnr.spidey.objects.games;

public enum VoiceGameType {
	YOUTUBE_TOGETHER(755600276941176913L, "YouTube Together", new String[]{"youtubetogether", "yt2gether", "yttogether", "youtube2gether"}),
	//POKER_NIGHT(755827207812677713L, "Poker Night", new String[]{"poker", "pokernight"}), - currently broken
	BETRAYAL_IO(773336526917861400L, "Betrayal.io", new String[]{"betrayalio", "betrayal"}),
	FISHINGTON_IO(814288819477020702L, "Fishington.io", new String[]{"fishingtionio", "fishington"});

	private final long applicationId;
	private final String friendlyName;
	private final String[] keys;

	VoiceGameType(long applicationId, String friendlyName, String[] keys) {
		this.applicationId = applicationId;
		this.friendlyName = friendlyName;
		this.keys = keys;
	}

	public long getApplicationId() {
		return applicationId;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String[] getKeys() {
		return keys;
	}

	public static VoiceGameType from(String argument) {
		for (var activity : values()) {
			if (argument.equalsIgnoreCase(activity.friendlyName))
				return activity;
			for (var key : activity.keys) {
				if (key.equalsIgnoreCase(argument))
					return activity;
			}
		}
		return null;
	}
}
