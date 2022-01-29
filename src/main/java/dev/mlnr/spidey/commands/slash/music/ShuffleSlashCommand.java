package dev.mlnr.spidey.commands.slash.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;

@SuppressWarnings("unused")
public class ShuffleSlashCommand extends SlashCommand {
	public ShuffleSlashCommand() {
		super("shuffle", "Shuffles the queue", Category.MUSIC, Permission.UNKNOWN, 0);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
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
			ctx.replyErrorLocalized("commands.shuffle.only_one");
			return false;
		}
		Collections.shuffle(queue);
		ctx.replyLocalized("commands.shuffle.success");
		return true;
	}
}