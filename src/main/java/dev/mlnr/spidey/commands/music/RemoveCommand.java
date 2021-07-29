package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class RemoveCommand extends Command {
	public RemoveCommand() {
		super("remove", "Removes a song from the queue based on its position", Category.MUSIC, Permission.UNKNOWN, 0,
				new OptionData(OptionType.INTEGER, "position", "The position of the track to remove", true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
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
		var trackPosition = ctx.getLongOption("position");
		var size = queue.size();
		if (trackPosition < 1 || trackPosition > size) {
			ctx.replyErrorLocalized("number_out_of_range", size);
			return false;
		}
		var actualPosition = (int) (trackPosition - 1);
		var selectedTrack = queue.get(actualPosition);
		if (!MusicUtils.canInteract(ctx.getMember(), selectedTrack)) {
			ctx.replyErrorLocalized("music.messages.failure.cant_interact_requester", "remove someone else's song from the queue");
			return false;
		}
		queue.remove(actualPosition);
		ctx.replyLocalized("commands.remove.removed", selectedTrack.getInfo().title);
		return true;
	}
}