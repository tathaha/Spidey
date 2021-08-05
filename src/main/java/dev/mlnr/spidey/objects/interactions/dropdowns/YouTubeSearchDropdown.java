package dev.mlnr.spidey.objects.interactions.dropdowns;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import dev.mlnr.spidey.utils.MusicUtils;

public class YouTubeSearchDropdown extends ComponentAction {
	private final MusicPlayer musicPlayer;

	public static void create(YouTubeSearchDropdown.Context context) {
		new YouTubeSearchDropdown(context);
	}

	private YouTubeSearchDropdown(YouTubeSearchDropdown.Context context) {
		super(context.getId(), context.getCtx(), ComponentAction.ActionType.YOUTUBE_SEARCH_DROPDOWN, context.getComponentActionCache());
		this.musicPlayer = context.getMusicPlayer();
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

	public static class Context extends ComponentAction.Context {
		private final MusicPlayer musicPlayer;

		public Context(String id, CommandContext ctx, MusicPlayer musicPlayer, ComponentActionCache componentActionCache) {
			super(id, ctx, componentActionCache);
			this.musicPlayer = musicPlayer;
		}

		public MusicPlayer getMusicPlayer() {
			return musicPlayer;
		}
	}
}