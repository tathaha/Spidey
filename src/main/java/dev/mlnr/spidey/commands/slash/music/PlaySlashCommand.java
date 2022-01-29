package dev.mlnr.spidey.commands.slash.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class PlaySlashCommand extends SlashCommand {
	public PlaySlashCommand() {
		super("play", "Plays/queues a song", Category.MUSIC, Permission.UNKNOWN, 2,
				new OptionData(OptionType.STRING, "query", "The link or query to play", true, true));
		withFlags(SlashCommand.Flags.SHOW_RESPONSE);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
		ctx.deferAndRun(() -> {
			var musicPlayer = MusicUtils.checkPlayability(ctx);
			if (musicPlayer == null) {
				return;
			}
			var query = ctx.getStringOption("query");
			var loader = new AudioLoader(musicPlayer, query, ctx);
			MusicUtils.saveQueryToHistory(ctx, query);
			MusicUtils.loadQuery(musicPlayer, query, loader);
		});
		return true;
	}
}