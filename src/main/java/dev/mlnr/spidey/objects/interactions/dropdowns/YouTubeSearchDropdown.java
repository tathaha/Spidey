package dev.mlnr.spidey.objects.interactions.dropdowns;

import dev.mlnr.spidey.cache.InteractionCache;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.Interaction;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import dev.mlnr.spidey.utils.MusicUtils;

public class YouTubeSearchDropdown implements Interaction {
	private final String id;
	private final CommandContext ctx;
	private final MusicPlayer musicPlayer;
	private final InteractionCache interactionCache;

	public YouTubeSearchDropdown(String id, CommandContext ctx, MusicPlayer musicPlayer, InteractionCache interactionCache) {
		this.id = id;
		this.ctx = ctx;
		this.musicPlayer = musicPlayer;
		this.interactionCache = interactionCache;
	}

	public void loadVideo(String link) {
		var loader = new AudioLoader(musicPlayer, link, ctx, true);
		MusicUtils.loadQuery(musicPlayer, link, loader);
		interactionCache.removeInteraction(this);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public CommandContext getCtx() {
		return ctx;
	}

	@Override
	public InteractionType getType() {
		return Interaction.InteractionType.YOUTUBE_SEARCH_DROPDOWN;
	}

	@Override
	public Object getObject() {
		return this;
	}

	@Override
	public long getAuthorId() {
		return ctx.getUser().getIdLong();
	}
}