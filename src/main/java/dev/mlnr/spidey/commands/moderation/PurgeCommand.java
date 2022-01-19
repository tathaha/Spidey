package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.objects.interactions.components.buttons.PurgeProcessor;
import dev.mlnr.spidey.utils.StringUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PurgeCommand extends Command {
	public PurgeCommand() {
		super("purge", "Purges messages (by entered user)", Category.MODERATION, Permission.MESSAGE_MANAGE, 6,
				new OptionData(OptionType.INTEGER, "amount", "The amount of messages to purge", true)
						.setRequiredRange(1, 100),
				new OptionData(OptionType.USER, "user", "The user to delete the messages of"));
		withFlags(Command.Flags.NO_THREADS);
	}

	@Override
	public boolean execute(CommandContext ctx) {
		if (!ctx.hasSelfChannelPermissions(ctx.getTextChannel(), getRequiredPermission(), Permission.MESSAGE_HISTORY)) {
			ctx.replyErrorLocalized("commands.purge.messages.failure.no_perms"); // TODO CommandContext#replyNoPerms?
			return false;
		}
		respond(ctx, ctx.getUserOption("user"), ctx.getLongOption("amount"));
		return true;
	}

	private void respond(CommandContext ctx, User target, long limit) {
		var channel = ctx.getTextChannel();
		var i18n = ctx.getI18n();
		channel.getIterableHistory().cache(false).limit(target == null ? (int) limit : 100).queue(messages -> {
			if (messages.isEmpty()) {
				ctx.replyErrorLocalized("commands.purge.messages.failure.no_messages.text");
				return;
			}
			var allMessages = target == null ? messages : messages.stream().filter(msg -> msg.getAuthor().equals(target)).limit(limit).collect(Collectors.toList());
			if (allMessages.isEmpty()) {
				ctx.replyErrorLocalized("commands.purge.messages.failure.no_messages.user", target.getAsTag());
				return;
			}
			var pinnedMessages = allMessages.stream().filter(Message::isPinned).collect(Collectors.toList());
			var purgeProcessorId = StringUtils.randomString(30);
			var componentActionCache = ctx.getCache().getComponentActionCache();
			var purgeProcessor = new PurgeProcessor(purgeProcessorId, ctx, allMessages, pinnedMessages, target, componentActionCache);

			if (pinnedMessages.isEmpty()) {
				purgeProcessor.proceed();
				return;
			}
			var size = pinnedMessages.size();
			var builder = new StringBuilder(size == 1 ? i18n.get("commands.purge.messages.pinned.one")
					: i18n.get("commands.purge.messages.pinned.multiple"));
			builder.append(" ").append(i18n.get("commands.purge.messages.pinned.confirmation.text")).append(" ");
			builder.append(size == 1 ? i18n.get("commands.purge.messages.pinned.confirmation.one")
					: i18n.get("commands.purge.messages.pinned.confirmation.multiple"));
			builder.append("? ").append(i18n.get("commands.purge.messages.pinned.middle_text.text"));
			builder.append(" ").append(size == 1 ? i18n.get("commands.purge.messages.pinned.middle_text.one")
					: i18n.get("commands.purge.messages.pinned.middle_text.multiple"));
			builder.append(i18n.get("commands.purge.messages.pinned.end_text"));

			componentActionCache.createPurgePrompt(purgeProcessor, builder.toString(), ctx);
		}, throwable -> ctx.replyErrorLocalized("internal_error", "purge messages", throwable.getMessage()));
	}
}