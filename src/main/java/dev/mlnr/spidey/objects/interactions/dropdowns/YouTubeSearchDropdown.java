package dev.mlnr.spidey.objects.interactions.dropdowns;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import dev.mlnr.spidey.utils.MusicUtils;

public class YouTubeSearchDropdown extends ComponentAction {
	private final MusicPlayer musicPlayer;

	public YouTubeSearchDropdown(String id, CommandContext ctx, MusicPlayer musicPlayer, ComponentActionCache componentActionCache) {
		super(id, ctx, ComponentAction.ActionType.YOUTUBE_SEARCH_DROPDOWN, componentActionCache);
		this.musicPlayer = musicPlayer;
	}

	public void loadVideo(String link) {
		var loader = new AudioLoader(musicPlayer, link, ctx, true);
		MusicUtils.loadQuery(musicPlayer, link, loader);
		uncacheAndDelete();
	}

	@Override
	public Object getObject() {
		return this;
	}
}