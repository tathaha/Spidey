package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.*;
import dev.mlnr.spidey.cache.music.*;

public class Cache {
	private final MusicPlayerCache musicPlayerCache;
	private final VideoSegmentCache videoSegmentCache;
	private final SearchHistoryCache searchHistoryCache;

	private final GeneralCache generalCache;
	private final GuildSettingsCache guildSettingsCache;
	private final MessageCache messageCache;
	private final ComponentActionCache componentActionCache;

	public Cache(Spidey spidey) {
		var databaseManager = spidey.getDatabaseManager();

		this.musicPlayerCache = MusicPlayerCache.getInstance();
		this.videoSegmentCache = VideoSegmentCache.getInstance();
		this.searchHistoryCache = new SearchHistoryCache(databaseManager);

		this.guildSettingsCache = GuildSettingsCache.getInstance(spidey);
		this.generalCache = new GeneralCache(guildSettingsCache, databaseManager);

		this.messageCache = new MessageCache();
		this.componentActionCache = new ComponentActionCache();
	}

	public MusicPlayerCache getMusicPlayerCache() {
		return musicPlayerCache;
	}

	public VideoSegmentCache getVideoSegmentCache() {
		return videoSegmentCache;
	}

	public SearchHistoryCache getSearchHistoryCache() {
		return searchHistoryCache;
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

	public ComponentActionCache getComponentActionCache() {
		return componentActionCache;
	}
}