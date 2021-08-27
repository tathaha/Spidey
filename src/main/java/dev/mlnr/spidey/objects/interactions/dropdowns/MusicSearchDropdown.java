package dev.mlnr.spidey.objects.interactions.dropdowns;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.objects.music.MusicPlayer;
import dev.mlnr.spidey.utils.MusicUtils;

public class MusicSearchDropdown extends ComponentAction {
	private final MusicPlayer musicPlayer;

	public static void create(MusicSearchDropdown.Context context) {
		new MusicSearchDropdown(context);
	}

	private MusicSearchDropdown(MusicSearchDropdown.Context context) {
		super(context.getId(), context.getCtx(), ActionType.MUSIC_SEARCH_DROPDOWN, context.getComponentActionCache());
		this.musicPlayer = context.getMusicPlayer();
	}

	public void load(String link) {
		var loader = new AudioLoader(musicPlayer, link, ctx, true);
		MusicUtils.loadQuery(musicPlayer, link, loader);
		uncacheAndDelete();
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