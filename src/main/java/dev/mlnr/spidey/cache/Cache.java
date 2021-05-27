package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.music.MusicPlayerCache;
import dev.mlnr.spidey.cache.music.VideoSegmentCache;
import net.dv8tion.jda.api.JDA;

public class Cache {
	private final MusicPlayerCache musicPlayerCache;
	private final VideoSegmentCache videoSegmentCache;

	private final AkinatorCache akinatorCache;
	private final GeneralCache generalCache;
	private final GuildSettingsCache guildSettingsCache;
	private final MessageCache messageCache;
	private final PaginatorCache paginatorCache;

	public Cache(Spidey spidey, JDA jda) {
		this.musicPlayerCache = MusicPlayerCache.getInstance();
		this.videoSegmentCache = VideoSegmentCache.getInstance();

		this.akinatorCache = new AkinatorCache();

		this.guildSettingsCache = GuildSettingsCache.getInstance(spidey);
		this.generalCache = new GeneralCache(guildSettingsCache, spidey.getDatabaseManager());

		this.messageCache = new MessageCache();
		this.paginatorCache = new PaginatorCache(jda);
	}

	public MusicPlayerCache getMusicPlayerCache() {
		return musicPlayerCache;
	}

	public VideoSegmentCache getVideoSegmentCache() {
		return videoSegmentCache;
	}

	public AkinatorCache getAkinatorCache() {
		return akinatorCache;
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

	public PaginatorCache getPaginatorCache() {
		return paginatorCache;
	}
}