package dev.mlnr.spidey.commands.slash.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.StringUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class QueueSlashCommand extends SlashCommand {
	public QueueSlashCommand() {
		super("queue", "Lists the current queue", Category.MUSIC, Permission.UNKNOWN, 3);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
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