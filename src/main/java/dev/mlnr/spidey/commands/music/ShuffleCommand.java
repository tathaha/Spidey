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
	public boolean execute(CommandContext ctx) {
		if (!MusicUtils.canInteract(ctx.getMember())) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact", "shuffle the queue");
			return false;
		}
		var guild = ctx.getGuild();
		var musicPlayer = ctx.getCache().getMusicPlayerCache().getMusicPlayer(guild);
		if (musicPlayer == null) {
			ctx.replyErrorLocalized("music.messages.failure.no_music");
			return false;
		}
		var trackScheduler = musicPlayer.getTrackScheduler();
		var queue = trackScheduler.getQueueAsList();
		if (queue.isEmpty()) {
			ctx.replyErrorLocalized("music.messages.failure.queue_empty");
			return false;
		}
		if (queue.size() == 1) {
			ctx.replyErrorLocalized("commands.shuffle.other.only_one");
			return false;
		}
		Collections.shuffle(queue);
		ctx.reactLike();
		ctx.replyLocalized("commands.shuffle.other.success");
		return true;
	}
}