package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.games.VoiceGameType;
import dev.mlnr.spidey.utils.Utils;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class VoiceGameCommand extends Command {
	public VoiceGameCommand() {
		super("voicegame", Category.FUN, Permission.CREATE_INSTANT_INVITE, 10,
				new OptionData(OptionType.STRING, "game", "The game to create an invite for")
						.addChoice(VoiceGameType.BETRAYAL_IO.getFriendlyName(), VoiceGameType.BETRAYAL_IO.name())
						.addChoice(VoiceGameType.FISHINGTON_IO.getFriendlyName(), VoiceGameType.FISHINGTON_IO.name())
						.addChoice(VoiceGameType.POKER_NIGHT.getFriendlyName(), VoiceGameType.POKER_NIGHT.name())
						.addChoice(VoiceGameType.YOUTUBE_TOGETHER.getFriendlyName(), VoiceGameType.YOUTUBE_TOGETHER.name())
						.setRequired(true),
				new OptionData(OptionType.CHANNEL, "channel", "The channel to create the game invite for"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var channel = ctx.getChannelOption("channel");
		var i18n = ctx.getI18n();
		if (channel == null) {
			var voiceState = ctx.getMember().getVoiceState();
			var notInVoice = i18n.get("commands.voicegame.other.not_in_voice");
			if (voiceState == null) {
				ctx.replyError(notInVoice);
				return false;
			}
			var voiceStateChannel = voiceState.getChannel();
			if (voiceStateChannel == null) {
				ctx.replyError(notInVoice);
				return false;
			}
			channel = voiceStateChannel;
		}
		if (channel.getType() != ChannelType.VOICE) {
			ctx.replyErrorLocalized("commands.voicegame.other.not_voice");
			return false;
		}
		if (!ctx.getGuild().getSelfMember().hasPermission(channel, Permission.CREATE_INSTANT_INVITE)) {
			ctx.replyErrorLocalized("commands.voicegame.other.no_perms");
			return false;
		}
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var voiceGame = VoiceGameType.valueOf(ctx.getStringOption("game"));
		Requester.launchVoiceGameSession(channel.getId(), voiceGame, code -> {
			embedBuilder.setColor(16711680);
			embedBuilder.setDescription(i18n.get("commands.voicegame.other.click", code, voiceGame.getFriendlyName()));
			ctx.reply(embedBuilder);
		}, error -> ctx.replyErrorLocalized("internal_error", i18n.get("commands.voicegame.other.create"), error.getMessage()));
		return true;
	}
}