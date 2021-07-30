package dev.mlnr.spidey.objects.interactions.buttons;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PurgeProcessor implements ComponentAction {
	private final String id;
	private final List<String> allMessagesIds;
	private final List<String> pinnedMessagesIds;
	private final User target;
	private final CommandContext ctx;
	private final ComponentActionCache componentActionCache;

	public PurgeProcessor(String id, List<Message> allMessages, List<Message> pinnedMessages, User target, CommandContext ctx,
	                      ComponentActionCache componentActionCache) {
		this.id = id;
		this.allMessagesIds = allMessages.stream().map(Message::getId).collect(Collectors.toList());
		this.pinnedMessagesIds = pinnedMessages.stream().map(Message::getId).collect(Collectors.toList());
		this.target = target;
		this.ctx = ctx;
		this.componentActionCache = componentActionCache;
	}

	public void processPrompt(PurgeProcessor.PromptAction action) {
		componentActionCache.removeAction(this);

		switch (action) {
			case ACCEPT:
				break;
			case DENY:
				return;
			case REMOVE:
				allMessagesIds.removeAll(pinnedMessagesIds);
				if (allMessagesIds.isEmpty()) {
					ctx.replyErrorLocalized("commands.purge.messages.failure.no_messages.unpinned");
					return;
				}
				break;
		}
		proceed();
	}

	public void proceed() {
		ctx.getEvent().deferReply(true).queue(deferred -> {
			var channel = ctx.getTextChannel();
			var future = CompletableFuture.allOf(channel.purgeMessagesById(allMessagesIds).toArray(new CompletableFuture[0]));
			future.whenCompleteAsync((ignored, throwable) -> {
				var i18n = ctx.getI18n();
				if (throwable != null) {
					ctx.sendFollowupError("internal_error", "purge messages", throwable.getMessage());
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

	@Override
	public String getId() {
		return id;
	}

	@Override
	public CommandContext getCtx() {
		return ctx;
	}

	@Override
	public ActionType getType() {
		return ComponentAction.ActionType.PURGE_PROMPT;
	}

	@Override
	public Object getObject() {
		return this;
	}

	@Override
	public long getAuthorId() {
		return ctx.getUser().getIdLong();
	}

	public enum PromptAction {
		ACCEPT,
		DENY,
		REMOVE
	}
}