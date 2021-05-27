package dev.mlnr.spidey.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.utils.ConcurrentUtils;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.StringUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class SearchCommand extends Command {
	public SearchCommand() {
		super("search", Category.MUSIC, Permission.UNKNOWN, 4,
				new OptionData(OptionType.STRING, "query", "The query to search YouTube for").setRequired(true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var musicPlayer = MusicUtils.checkPlayability(ctx);
		if (musicPlayer == null) {
			return false;
		}
		var query = ctx.getStringOption("query");
		var i18n = ctx.getI18n();
		MusicUtils.loadQuery(musicPlayer, "ytsearch:" + query, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				var selectionEmbedBuilder = MusicUtils.createMusicResponseBuilder();
				selectionEmbedBuilder.setAuthor(i18n.get("commands.search.other.searching", query));

				var tracks = playlist.getTracks();
				StringUtils.createSelection(selectionEmbedBuilder, tracks, ctx, "track", MusicUtils::formatTrack, ConcurrentUtils.getEventWaiter(), choice -> {
					var url = tracks.get(choice).getInfo().uri;
					var loader = new AudioLoader(musicPlayer, url, ctx);
					MusicUtils.loadQuery(musicPlayer, url, loader);
				});
			}

			@Override
			public void noMatches() {
				ctx.replyError(i18n.get("music.messages.failure.no_matches", query));
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				ctx.replyError(i18n.get("commands.search.other.error"));
			}
		});
		return true;
	}
}