package dev.mlnr.spidey.commands.slash.music;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.objects.music.TrackScheduler;
import dev.mlnr.spidey.utils.CommandUtils;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class RepeatSlashCommand extends SlashCommand {
	public RepeatSlashCommand() {
		super("repeat", "Sets the repeat mode", Category.MUSIC, Permission.UNKNOWN, 0,
				new OptionData(OptionType.STRING, "repeat_mode", "The repeat mode to set", true)
						.addChoices(CommandUtils.getChoicesFromEnum(TrackScheduler.RepeatMode.class)));
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
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