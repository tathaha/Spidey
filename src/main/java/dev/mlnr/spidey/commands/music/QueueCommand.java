package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.StringUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class QueueCommand extends Command {

	public QueueCommand() {
		super("queue", new String[]{"q"}, Category.MUSIC, Permission.UNKNOWN, 0, 3);
	}

	@Override
	public boolean execute(String[] args, CommandContext ctx) {
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		var trackScheduler = musicPlayer.getTrackScheduler();
		var queue = trackScheduler.getQueueAsList();
		if (queue.isEmpty()) {
			ctx.replyLocalized("music.messages.failure.queue_empty");
			return false;
		}
		StringUtils.createQueuePaginator(ctx, queue);
		return true;
	}
}