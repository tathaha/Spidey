package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class PlayCommand extends Command {
	public PlayCommand() {
		super("play", "Plays/queues a song", Category.MUSIC, Permission.UNKNOWN, 2, false,
				new OptionData(OptionType.STRING, "query", "The link or query to play", true, true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
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