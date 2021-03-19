package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.games.VoiceGameType;
import dev.mlnr.spidey.utils.Utils;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class VoiceGameCommand extends Command {
	public VoiceGameCommand() {
		super("voicegame", new String[]{"voiceactivity", "vcgame", "vcactivity"}, Category.FUN, Permission.CREATE_INSTANT_INVITE, 1, 10);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var voiceState = ctx.getMember().getVoiceState();
		var i18n = ctx.getI18n();
		var notInVoice = i18n.get("commands.voicegame.other.not_in_voice");
		if (voiceState == null) {
			ctx.replyError(notInVoice);
			return;
		}
		var channel = voiceState.getChannel();
		if (channel == null) {
			ctx.replyError(notInVoice);
			return;
		}
		if (!ctx.getGuild().getSelfMember().hasPermission(channel, Permission.CREATE_INSTANT_INVITE)) {
			ctx.replyErrorLocalized("commands.voicegame.other.no_perms");
			return;
		}
		var embedBuilder = Utils.createEmbedBuilder(ctx.getAuthor());
		if (args.length == 0) {
			embedBuilder.setAuthor(i18n.get("commands.voicegame.other.available_games"));
			var availableGamesBuilder = embedBuilder.getDescriptionBuilder();
			for (var voiceGame : VoiceGameType.values()) {
				var keys = Arrays.stream(voiceGame.getKeys()).collect(Collectors.joining(", ", "**", "**"));
				availableGamesBuilder.append("**").append(voiceGame.getFriendlyName()).append("**").append(" - ").append(keys).append("\n");
			}
			var prefix = ctx.getCache().getGuildSettingsCache().getMiscSettings(ctx.getGuild().getIdLong()).getPrefix();
			availableGamesBuilder.append("\n").append(i18n.get("commands.voicegame.other.pick", prefix));
			ctx.reply(embedBuilder);
			return;
		}
		var voiceGame = VoiceGameType.from(args[0]);
		if (voiceGame == null) {
			ctx.replyErrorLocalized("commands.voicegame.other.no_game");
			return;
		}
		Requester.launchYouTubeTogetherSession(channel.getId(), voiceGame, code -> {
			embedBuilder.setColor(16711680);
			embedBuilder.setDescription(i18n.get("commands.voicegame.other.click", code, voiceGame.getFriendlyName()));
			ctx.reply(embedBuilder);
		}, error -> ctx.replyErrorLocalized("internal_error", i18n.get("commands.voicegame.other.create"), error.getMessage()));
	}
}