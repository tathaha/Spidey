package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.music.AudioLoader;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class PlayCommand extends CommandBase {
	public PlayCommand() {
		super("play", "Plays/queues a song", Category.MUSIC, Permission.UNKNOWN, 2,
				new OptionData(OptionType.STRING, "query", "The YouTube link or query to play", true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var musicPlayer = MusicUtils.checkPlayability(ctx);
		if (musicPlayer == null) {
			return false;
		}
		var query = ctx.getStringOption("query");
		var toLoad = MusicUtils.YOUTUBE_URL_PATTERN.matcher(query).matches() ? query : "ytsearch:" + query;
		var loader = new AudioLoader(musicPlayer, toLoad, ctx);
		MusicUtils.loadQuery(musicPlayer, toLoad, loader);
		return true;
	}
}