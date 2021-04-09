package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.music.TrackScheduler;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class RepeatCommand extends Command {

	public RepeatCommand() {
		super("repeat", new String[]{"loop"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "set the repeat mode");
			return false;
		}
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		var trackScheduler = musicPlayer.getTrackScheduler();
		if (args.length == 0) {
			if (trackScheduler.getRepeatMode() == null) {
				ctx.replyErrorLocalized("commands.repeat.other.provide");
				return false;
			}
			trackScheduler.setRepeatMode(null);
			ctx.reactLike();
			ctx.replyLocalized("commands.repeat.other.reset");
			return true;
		}
		try {
			var repeatMode = TrackScheduler.RepeatMode.valueOf(args[0].toUpperCase());
			trackScheduler.setRepeatMode(repeatMode);
			ctx.reactLike();
			ctx.replyLocalized("commands.repeat.other.set", args[0]);
			return true;
		}
		catch (IllegalArgumentException ex) {
			ctx.replyError(ctx.getI18n().get("commands.repeat.other.doesnt_exist"), Emojis.DISLIKE);
			return false;
		}
	}
}