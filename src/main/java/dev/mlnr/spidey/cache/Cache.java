package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.cache.music.VideoSegmentCache;

public class Cache {
	private final MusicPlayerCache musicPlayerCache;
	private final VideoSegmentCache videoSegmentCache;

	private final GeneralCache generalCache;
	private final GuildSettingsCache guildSettingsCache;
	private final MessageCache messageCache;
	private final ButtonActionCache buttonActionCache;

	public Cache(Spidey spidey) {
		this.musicPlayerCache = MusicPlayerCache.getInstance();
		this.videoSegmentCache = VideoSegmentCache.getInstance();

		this.guildSettingsCache = GuildSettingsCache.getInstance(spidey);
		this.generalCache = new GeneralCache(guildSettingsCache, spidey.getDatabaseManager());

		this.messageCache = new MessageCache();
		this.buttonActionCache = new ButtonActionCache();
	}

	public MusicPlayerCache getMusicPlayerCache() {
		return musicPlayerCache;
	}

	public VideoSegmentCache getVideoSegmentCache() {
		return videoSegmentCache;
	}

	public GeneralCache getGeneralCache() {
		return generalCache;
	}

	public GuildSettingsCache getGuildSettingsCache() {
		return guildSettingsCache;
	}

	public MessageCache getMessageCache() {
		return messageCache;
	}

	public ButtonActionCache getButtonActionCache() {
		return buttonActionCache;
	}
}