package dev.mlnr.spidey.commands.music;

import dev.mlnr.spidey.objects.command.CommandBase;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.music.TrackScheduler;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class RepeatCommand extends CommandBase {
	public RepeatCommand() {
		super("repeat", "Sets the repeat mode", Category.MUSIC, Permission.UNKNOWN, 0,
				new OptionData(OptionType.STRING, "repeat_mode", "The repeat mode to set")
						.addChoices(Arrays.stream(TrackScheduler.RepeatMode.values()).map(
								repeatMode -> new Command.Choice(repeatMode.getFriendlyName(), repeatMode.name())).collect(Collectors.toList()))
						.setRequired(true));
	}

	@Override
	public boolean execute(CommandContext ctx) {
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
		var repeatOption = ctx.getStringOption("repeat_mode");
		var repeatMode = TrackScheduler.RepeatMode.valueOf(repeatOption);
		trackScheduler.setRepeatMode(repeatMode);
		ctx.replyLocalized("commands.repeat.set", repeatMode.getFriendlyName());
		return true;
	}
}