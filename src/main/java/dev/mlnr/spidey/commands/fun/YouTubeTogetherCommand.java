package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class YouTubeTogetherCommand extends Command {
	public YouTubeTogetherCommand() {
		super("youtubetogether", new String[]{"yt2gether", "yttogether", "youtube2gether"}, Category.FUN, Permission.CREATE_INSTANT_INVITE, 0, 30);
	}

	@Override
	public void execute(String[] args, CommandContext ctx) {
		var voiceState = ctx.getMember().getVoiceState();
		var i18n = ctx.getI18n();
		var notInVoice = i18n.get("commands.youtubetogether.other.not_in_voice");
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
			ctx.replyError(i18n.get("commands.youtubetogether.other.no_perms"));
			return;
		}
		Requester.launchYouTubeTogetherSession(channel.getId(), code -> {
			var embedBuilder = Utils.createEmbedBuilder(ctx.getAuthor());
			embedBuilder.setColor(16711680);
			embedBuilder.setDescription(i18n.get("commands.youtubetogether.other.click", code));
			ctx.reply(embedBuilder);
		}, error -> ctx.replyError(i18n.get("internal_error", "create a YouTube Together session", error.getMessage())));
	}
}