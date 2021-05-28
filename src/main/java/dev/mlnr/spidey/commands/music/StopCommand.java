package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class StopCommand extends Command {
	public StopCommand() {
		super("stop", "Stops the playback and disconnects the bot", Category.MUSIC, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "stop the playback");
			return false;
		}
		var guild = ctx.getGuild();
		var musicPlayerCache = ctx.getCache().getMusicPlayerCache();
		var musicPlayer = musicPlayerCache.getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		musicPlayerCache.disconnectFromChannel(guild);
		ctx.reply(":wave:");
		return true;
	}
}