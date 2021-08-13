package dev.mlnr.spidey.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import dev.mlnr.spidey.utils.StringUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class SearchCommand extends Command {
	public SearchCommand() {
		super("search", "Searches a query on the provided service", Category.MUSIC, Permission.UNKNOWN, 4, false,
				new OptionData(OptionType.STRING, "query", "The query to search your chosen service for", true),
				new OptionData(OptionType.STRING, "service", "The service to search the query on or blank to choose YouTube")
						.addChoices(Utils.getChoicesFromEnum(MusicUtils.ServiceType.class)));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var musicPlayer = MusicUtils.checkPlayability(ctx);
		if (musicPlayer == null) {
			return false;
		}
		var serviceOption = ctx.getStringOption("service");
		var service = serviceOption == null ? MusicUtils.ServiceType.YOUTUBE : MusicUtils.ServiceType.valueOf(serviceOption);
		var query = ctx.getStringOption("query");
		MusicUtils.loadQuery(musicPlayer, service.getSearchPrefix() + query, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				StringUtils.createTrackSelection(ctx, musicPlayer, playlist.getTracks());
			}

			@Override
			public void noMatches() {
				ctx.replyErrorLocalized("music.messages.failure.no_matches", query);
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				ctx.replyErrorLocalized("commands.search.error");
			}
		});
		return true;
	}
}