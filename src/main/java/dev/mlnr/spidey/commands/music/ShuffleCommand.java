package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;

@SuppressWarnings("unused")
public class ShuffleCommand extends Command {

	public ShuffleCommand() {
		super("shuffle", new String[]{}, Category.MUSIC, Permission.UNKNOWN, 0, 0);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var i18n = ctx.getI18n();
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyError(i18n.get("music.messages.failure.cant_interact", "shuffle the queue"));
			return;
		}
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyError(i18n.get("music.messages.failure.no_music"));
			return;
		}
		var trackScheduler = musicPlayer.getTrackScheduler();
		var queue = trackScheduler.getQueueAsList();
		if (queue.isEmpty()) {
			ctx.replyError(i18n.get("music.messages.failure.queue_empty"));
			return;
		}
		if (queue.size() == 1) {
			ctx.replyError(i18n.get("commands.shuffle.other.only_one"));
			return;
		}
		Collections.shuffle(queue);
		ctx.reactLike();
		ctx.reply(i18n.get("commands.shuffle.other.success"));
	}
}