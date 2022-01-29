package dev.mlnr.spidey.commands.slash.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class RemoveSlashCommand extends SlashCommand {
	public RemoveSlashCommand() {
		super("remove", "Removes a song from the queue based on its position", Category.MUSIC, Permission.UNKNOWN, 0,
				new OptionData(OptionType.INTEGER, "position", "The position of the track to remove", true)
						.setMinValue(1));
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
			ctx.replyErrorLocalized("music.messages.failure.queue_empty");
			return false;
		}
		var trackPosition = ctx.getLongOption("position");
		var size = queue.size();
		if (trackPosition > size) {
			ctx.replyErrorLocalized("commands.remove.range", size);
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