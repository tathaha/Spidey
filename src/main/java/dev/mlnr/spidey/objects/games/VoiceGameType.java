package dev.mlnr.spidey.objects.games;

import dev.mlnr.spidey.objects.command.ChoicesEnum;

public enum VoiceGameType implements ChoicesEnum {
	POKER_NIGHT("Poker Night", 755827207812677713L),
	CHESS_IN_THE_PARK("Chess In The Park", 832012774040141894L),
	DOODLE_CREW("Doodle Crew", 878067389634314250L),
	LETTER_TILE("Letter Tile", 879863686565621790L),
	SPELLCAST("SpellCast", 852509694341283871L),
	WATCH_TOGETHER("Watch Together", 880218394199220334L),
	CHECKERS_IN_THE_PARK("Checkers In The Park", 832013003968348200L),
	WORD_SNACKS("Word Snacks", 879863976006127627L);

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
