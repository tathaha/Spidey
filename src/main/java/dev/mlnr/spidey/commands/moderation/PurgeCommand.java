package dev.mlnr.spidey.commands.moderation;

import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.ConcurrentUtils;
import dev.mlnr.spidey.utils.Emojis;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.mlnr.spidey.utils.Utils.addReaction;
import static dev.mlnr.spidey.utils.Utils.deleteMessage;

@SuppressWarnings({"unused", "StringBufferReplaceableByString"})
public class PurgeCommand extends Command {
	public PurgeCommand() {
		super("purge", "Purges messages (by entered user)", Category.MODERATION, Permission.MESSAGE_MANAGE, 6,
				new OptionData(OptionType.INTEGER, "amount", "The amount of messages to purge", true),
				new OptionData(OptionType.USER, "user", "The user to delete the messages of"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var guild = ctx.getGuild();
		if (!guild.getSelfMember().hasPermission(ctx.getTextChannel(), getRequiredPermission(), Permission.MESSAGE_HISTORY)) {
			ctx.replyErrorLocalized("commands.purge.messages.failure.no_perms");
			return false;
		}
		var amount = ctx.getLongOption("amount");
		if (amount < 1 || amount > 100) {
			ctx.replyErrorLocalized("number.range", 100);
			return false;
		}
		var user = ctx.getUserOption("user");
		if (user == null) {
			respond(ctx, null, amount);
			return true;
		}
		respond(ctx, user, amount);
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
			var msgs = target == null ? messages : messages.stream().filter(msg -> msg.getAuthor().equals(target)).limit(limit).collect(Collectors.toList());
			if (msgs.isEmpty()) {
				ctx.replyErrorLocalized("commands.purge.messages.failure.no_messages.user", target.getAsTag());
				return;
			}
			var pinned = msgs.stream().filter(Message::isPinned).collect(Collectors.toList());
			if (pinned.isEmpty()) {
				proceed(msgs, target, ctx);
				return;
			}
			var size = pinned.size();
			var builder = new StringBuilder(size == 1 ? i18n.get("commands.purge.messages.pinned.one")
					: i18n.get("commands.purge.messages.pinned.multiple"));
			builder.append(" ").append(i18n.get("commands.purge.messages.pinned.confirmation.text")).append(" ");
			builder.append(size == 1 ? i18n.get("commands.purge.messages.pinned.confirmation.one")
					: i18n.get("commands.purge.messages.pinned.confirmation.multiple"));
			builder.append("? ").append(i18n.get("commands.purge.messages.pinned.middle_text.text"));
			builder.append(" ").append(size == 1 ? i18n.get("commands.purge.messages.pinned.middle_text.one")
					: i18n.get("commands.purge.messages.pinned.middle_text_multiple"));
			builder.append(i18n.get("commands.purge.messages.pinned.end_text"));

			channel.sendMessage(builder.toString()).queue(sentMessage -> {
				addReaction(sentMessage, Emojis.CHECK);
				addReaction(sentMessage, Emojis.WASTEBASKET);
				addReaction(sentMessage, Emojis.CROSS);

				ConcurrentUtils.getEventWaiter().waitForEvent(GuildMessageReactionAddEvent.class,
						ev -> ev.getUser().equals(ctx.getUser()) && ev.getMessageIdLong() == sentMessage.getIdLong(),
						ev -> {
							switch (ev.getReactionEmote().getName()) {
								case Emojis.CHECK:
									deleteMessage(sentMessage);
									break;
								case Emojis.CROSS:
									deleteMessage(sentMessage);
									return;
								case Emojis.WASTEBASKET:
									msgs.removeAll(pinned);
									deleteMessage(sentMessage);
									if (msgs.isEmpty()) {
										ctx.replyErrorLocalized("commands.purge.messages.failure.no_messages.unpinned");
										return;
									}
									break;
								default:
							}
							proceed(msgs, target, ctx);
						}, 1, TimeUnit.MINUTES, () -> {
							deleteMessage(sentMessage);
							ctx.replyErrorLocalized("took_too_long");
						});
			});
		}, throwable -> ctx.replyErrorLocalized("internal_error", "purge messages", throwable.getMessage()));
	}

	private void proceed(List<Message> toDelete, User user, CommandContext ctx) {
		ctx.getEvent().deferReply(true).queue();

		var channel = ctx.getTextChannel();
		var future = CompletableFuture.allOf(channel.purgeMessages(toDelete).toArray(new CompletableFuture[0]));
		future.whenCompleteAsync((ignored, throwable) -> {
			var i18n = ctx.getI18n();
			if (throwable != null) {
				ctx.replyErrorLocalized("internal_error", "purge messages", throwable.getMessage());
				return;
			}
			ctx.getEvent().getHook().sendMessage(generateSuccessMessage(toDelete.size(), user, i18n)).queue();
		});
	}

	private String generateSuccessMessage(int amount, User user, I18n i18n) {
		return i18n.get("commands.purge.messages.success.text", amount) + " "
				+ (amount == 1 ? i18n.get("commands.purge.messages.success.one") : i18n.get("commands.purge.messages.success.multiple"))
				+ (user == null ? "." : " " + i18n.get("commands.purge.messages.success.user", user) + ".");
	}
}