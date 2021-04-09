package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class ClearCommand extends Command {

	public ClearCommand() {
		super("clear", new String[]{"clearqueue", "queueclear"}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "clear the queue");
			return false;
		}
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		var trackScheduler = musicPlayer.getTrackScheduler();
		var queue = trackScheduler.getQueue();
		if (queue.isEmpty()) {
			ctx.replyErrorLocalized("music.messages.failure.queue_empty");
			return false;
		}
		queue.clear();
		ctx.replyLocalized("commands.clear.other.cleared");
		return true;
	}
}