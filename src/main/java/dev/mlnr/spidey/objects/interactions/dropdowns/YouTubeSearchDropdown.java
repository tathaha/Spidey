package dev.mlnr.spidey.objects.interactions.dropdowns;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import dev.mlnr.spidey.utils.MusicUtils;

public class YouTubeSearchDropdown implements ComponentAction {
	private final String id;
	private final CommandContext ctx;
	private final MusicPlayer musicPlayer;
	private final ComponentActionCache componentActionCache;

	public YouTubeSearchDropdown(String id, CommandContext ctx, MusicPlayer musicPlayer, ComponentActionCache componentActionCache) {
		this.id = id;
		this.ctx = ctx;
		this.musicPlayer = musicPlayer;
		this.componentActionCache = componentActionCache;
	}

	public void loadVideo(String link) {
		var loader = new AudioLoader(musicPlayer, link, ctx, true);
		MusicUtils.loadQuery(musicPlayer, link, loader);
		componentActionCache.removeAction(this);
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
	public ActionType getType() {
		return ComponentAction.ActionType.YOUTUBE_SEARCH_DROPDOWN;
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