package dev.mlnr.spidey.commands.slash.fun;

import dev.mlnr.spidey.objects.commands.slash.SlashCommand;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.commands.slash.category.Category;
import dev.mlnr.spidey.objects.games.VoiceGameType;
import dev.mlnr.spidey.utils.CommandUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@SuppressWarnings("unused")
public class VoiceGameSlashCommand extends SlashCommand {
	public VoiceGameSlashCommand() {
		super("voicegame", "Creates an invite for a game for a voice channel", Category.FUN, Permission.CREATE_INSTANT_INVITE, 10,
				new OptionData(OptionType.STRING, "game", "The game to create an invite for", true)
						.addChoices(CommandUtils.getChoicesFromEnum(VoiceGameType.class)),
				new OptionData(OptionType.CHANNEL, "channel", "The channel to create the game invite for")
						.setChannelTypes(ChannelType.VOICE));
		withFlags(SlashCommand.Flags.SHOW_RESPONSE);
	}

	@Override
	public boolean execute(SlashCommandContext ctx) {
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
		var requiredPermission = getRequiredPermission();
		if (!ctx.hasSelfChannelPermissions(channel, requiredPermission)) {
			ctx.replyErrorNoPerm(requiredPermission, "create a session");
			return false;
		}
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var voiceGame = VoiceGameType.valueOf(ctx.getStringOption("game"));
		var voiceChannel = (VoiceChannel) channel;
		voiceChannel.createInvite().setTargetApplication(voiceGame.getApplicationId()).queue(invite -> {
			embedBuilder.setColor(16711680);
			embedBuilder.setDescription(i18n.get("commands.voicegame.click", invite.getCode(), voiceGame.getFriendlyName()));
			ctx.reply(embedBuilder);
		});
		return true;
	}
}