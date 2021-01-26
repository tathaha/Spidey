package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class ClearCommand extends Command {

	public ClearCommand() {
		super("clear", new String[]{"clearqueue", "queueclear"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var i18n = ctx.getI18n();
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyError(i18n.get("music.messages.failure.cant_interact", "clear the queue"));
			return;
		}
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyError(i18n.get("music.messages.failure.no_music"));
			return;
		}
		var trackScheduler = musicPlayer.getTrackScheduler();
		var queue = trackScheduler.getQueue();
		if (queue.isEmpty()) {
			ctx.replyError(i18n.get("music.messages.failure.queue_empty"));
			return;
		}
		queue.clear();
		ctx.reply(i18n.get("commands.clear.other.cleared"));
	}
}