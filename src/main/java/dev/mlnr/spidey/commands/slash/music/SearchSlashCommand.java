package dev.mlnr.spidey.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class SearchSlashCommand extends SlashCommand {
	public SearchSlashCommand() {
		super("search", "Searches a query on the provided service", Category.MUSIC, Permission.UNKNOWN, 4,
				new OptionData(OptionType.STRING, "query", "The query to search your chosen service for", true, true),
				new OptionData(OptionType.STRING, "service", "The service to search the query on or blank to choose YouTube")
						.addChoices(CommandUtils.getChoicesFromEnum(MusicUtils.ServiceType.class)));
		withFlags(SlashCommand.Flags.SHOW_RESPONSE);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		ctx.deferAndRun(() -> {
			var musicPlayer = MusicUtils.checkPlayability(ctx);
			if (musicPlayer == null) {
				return;
			}
			var serviceOption = ctx.getStringOption("service");
			var service = serviceOption == null ? MusicUtils.ServiceType.YOUTUBE : MusicUtils.ServiceType.valueOf(serviceOption);
			var query = ctx.getStringOption("query");

			MusicUtils.saveQueryToHistory(ctx, query);
			MusicUtils.loadQuery(musicPlayer, service.getSearchPrefix() + query, new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					StringUtils.createTrackSelection(ctx, musicPlayer, playlist.getTracks());
				}

				@Override
				public void noMatches() {
					ctx.sendFollowupErrorLocalized("music.messages.failure.no_matches", query);
				}

				@Override
				public void loadFailed(FriendlyException exception) {
					ctx.sendFollowupErrorLocalized("commands.search.error");
				}
			});
		});
		return true;
	}
}