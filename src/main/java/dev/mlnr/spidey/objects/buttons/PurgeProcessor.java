package dev.mlnr.spidey.objects.buttons;

import dev.mlnr.spidey.cache.ButtonActionCache;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PurgeProcessor implements ButtonAction {
	private final String id;
	private final List<Message> allMessages;
	private final List<Message> pinnedMessages;
	private final User target;
	private final CommandContext ctx;
	private final ButtonActionCache buttonActionCache;

	public PurgeProcessor(String id, List<Message> allMessages, List<Message> pinnedMessages, User target, CommandContext ctx,
	                      ButtonActionCache buttonActionCache) {
		this.id = id;
		this.allMessages = allMessages;
		this.pinnedMessages = pinnedMessages;
		this.target = target;
		this.ctx = ctx;
		this.buttonActionCache = buttonActionCache;
	}

	public void processPrompt(PurgeProcessor.PromptAction action) {
		buttonActionCache.removeButtonAction(this);

		switch (action) {
			case ACCEPT:
				break;
			case DENY:
				return;
			case REMOVE:
				allMessages.removeAll(pinnedMessages);
				if (allMessages.isEmpty()) {
					ctx.replyErrorLocalized("commands.purge.messages.failure.no_messages.unpinned");
					return;
				}
				break;
		}
		proceed();
	}

	public void proceed() {
		ctx.getEvent().deferReply().queue();

		var channel = ctx.getTextChannel();
		var future = CompletableFuture.allOf(channel.purgeMessages(allMessages).toArray(new CompletableFuture[0]));
		future.whenCompleteAsync((ignored, throwable) -> {
			var i18n = ctx.getI18n();
			if (throwable != null) {
				ctx.replyErrorLocalized("internal_error", "purge messages", throwable.getMessage());
				return;
			}
			ctx.getEvent().getHook().sendMessage(generateSuccessMessage(allMessages.size(), i18n)).setEphemeral(true).queue();
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
		return ButtonAction.ActionType.PURGE_PROMPT;
	}

	@Override
	public Object getActionObject() {
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