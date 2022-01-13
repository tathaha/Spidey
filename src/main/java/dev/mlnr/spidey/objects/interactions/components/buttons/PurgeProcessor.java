package dev.mlnr.spidey.objects.interactions.components.buttons;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.components.ComponentAction;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PurgeProcessor extends ComponentAction {
	private final List<String> allMessagesIds;
	private final List<String> pinnedMessagesIds;
	private final User target;

	public PurgeProcessor(String id, CommandContext ctx, List<Message> allMessages, List<Message> pinnedMessages, User target,
	                      ComponentActionCache componentActionCache) {
		super(id, ctx, ComponentAction.ActionType.PURGE_PROMPT, componentActionCache);
		this.allMessagesIds = allMessages.stream().map(Message::getId).collect(Collectors.toList());
		this.pinnedMessagesIds = pinnedMessages.stream().map(Message::getId).collect(Collectors.toList());
		this.target = target;
	}

	public void processPrompt(PurgeProcessor.PromptAction action) {
		uncacheAndDelete();

		switch (action) {
			case ACCEPT:
				break;
			case DENY:
				return;
			case REMOVE:
				allMessagesIds.removeAll(pinnedMessagesIds);
				if (allMessagesIds.isEmpty()) {
					ctx.sendFollowupErrorLocalized("commands.purge.messages.failure.no_messages.unpinned");
					return;
				}
				break;
		}
		proceed();
	}

	public void proceed() {
		ctx.deferAndRun(true, () -> {
			var channel = ctx.getTextChannel();
			var future = CompletableFuture.allOf(channel.purgeMessagesById(allMessagesIds).toArray(new CompletableFuture[0]));
			future.whenCompleteAsync((ignored, throwable) -> {
				var i18n = ctx.getI18n();
				if (throwable != null) {
					ctx.sendFollowupErrorLocalized("internal_error", "purge messages", throwable.getMessage());
					return;
				}
				ctx.sendFollowup(generateSuccessMessage(allMessagesIds.size(), i18n));
			});
		});
	}

	private String generateSuccessMessage(int amount, I18n i18n) {
		return i18n.get("commands.purge.messages.success.text", amount) + " "
				+ (amount == 1 ? i18n.get("commands.purge.messages.success.one") : i18n.get("commands.purge.messages.success.multiple"))
				+ (target == null ? "." : " " + i18n.get("commands.purge.messages.success.user", target) + ".");
	}

	public enum PromptAction {
		ACCEPT,
		DENY,
		REMOVE
	}
}