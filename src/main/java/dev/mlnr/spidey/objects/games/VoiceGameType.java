package dev.mlnr.spidey.objects.games;

public enum VoiceGameType {
	YOUTUBE_TOGETHER(755600276941176913L, "YouTube Together"),
	POKER_NIGHT(755827207812677713L, "Poker Night"),
	BETRAYAL_IO(773336526917861400L, "Betrayal.io"),
	FISHINGTON_IO(814288819477020702L, "Fishington.io");
	// CHESS(832012586023256104L, "Chess Game");

	private final long applicationId;
	private final String friendlyName;

	VoiceGameType(long applicationId, String friendlyName) {
		this.applicationId = applicationId;
		this.friendlyName = friendlyName;
	}

	public long getApplicationId() {
		return applicationId;
	}

	public String getFriendlyName() {
		return friendlyName;
	}
}
