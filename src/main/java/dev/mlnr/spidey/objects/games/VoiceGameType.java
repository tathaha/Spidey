package dev.mlnr.spidey.objects.games;

import dev.mlnr.spidey.objects.command.ChoicesEnum;

public enum VoiceGameType implements ChoicesEnum {
	YOUTUBE_TOGETHER("YouTube Together", 880218394199220334L),
	POKER_NIGHT("Poker Night", 755827207812677713L),
	BETRAYAL_IO("Betrayal.io", 773336526917861400L),
	FISHINGTON_IO("Fishington.io", 814288819477020702L),
	CHESS_IN_THE_PARK("Chess In The Park", 832012774040141894L);

	private final String friendlyName;
	private final long applicationId;

	VoiceGameType(String friendlyName, long applicationId) {
		this.friendlyName = friendlyName;
		this.applicationId = applicationId;
	}

	@Override
	public String getFriendlyName() {
		return friendlyName;
	}

	public long getApplicationId() {
		return applicationId;
	}
}
