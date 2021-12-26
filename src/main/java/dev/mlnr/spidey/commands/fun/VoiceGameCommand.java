package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.games.VoiceGameType;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class VoiceGameCommand extends Command {
	public VoiceGameCommand() {
		super("voicegame", "Creates an invite for a game for a voice channel", Category.FUN, Permission.CREATE_INSTANT_INVITE, 10, false,
				Utils.createConvenientOption(OptionType.STRING, "game", "The game to create an invite for", true)
						.addChoices(Utils.getChoicesFromEnum(VoiceGameType.class)),
				new OptionData(OptionType.CHANNEL, "channel", "The channel to create the game invite for")
						.setChannelTypes(ChannelType.VOICE));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var channel = ctx.getChannelOption("channel");
		var i18n = ctx.getI18n();
		if (channel == null) {
			var voiceStateChannel = ctx.getMember().getVoiceState().getChannel();
			if (voiceStateChannel == null) {
				ctx.replyErrorLocalized("commands.voicegame.not_in_voice");
				return false;
			}
			channel = voiceStateChannel;
		}
		var voiceChannel = (VoiceChannel) channel;
		if (!ctx.getGuild().getSelfMember().hasPermission(voiceChannel, Permission.CREATE_INSTANT_INVITE)) {
			ctx.replyErrorLocalized("commands.voicegame.no_perms");
			return false;
		}
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var voiceGame = VoiceGameType.valueOf(ctx.getStringOption("game"));
		voiceChannel.createInvite().setTargetApplication(voiceGame.getApplicationId()).queue(invite -> {
			embedBuilder.setColor(16711680);
			embedBuilder.setDescription(i18n.get("commands.voicegame.click", invite.getCode(), voiceGame.getFriendlyName()));
			ctx.reply(embedBuilder);
		});
		return true;
	}
}