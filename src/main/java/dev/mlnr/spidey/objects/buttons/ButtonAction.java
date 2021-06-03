package dev.mlnr.spidey.objects.buttons;

import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.interactions.components.Button;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ButtonAction {
	private final String id;
	private final CommandContext ctx;
	private final ActionType type;
	private final Object object;
	private final long authorId;

	public ButtonAction(String id, CommandContext ctx, ActionType type, Object object) {
		this.id = id;
		this.ctx = ctx;
		this.type = type;
		this.object = object;
		this.authorId = ctx.getUser().getIdLong();
	}

	public String getId() {
		return id;
	}

	public CommandContext getCtx() {
		return ctx;
	}

	public ActionType getType() {
		return type;
	}

	public Object getObject() {
		return object;
	}

	public long getAuthorId() {
		return authorId;
	}

	public enum ActionType {
		PAGINATION(ExpirationPolicy.CREATED, 10, TimeUnit.MINUTES, (button, buttonAction) -> {
			var emoji = button.getEmoji();
			Paginator.Action move;
			switch (emoji.getName()) {
				case Emojis.BACKWARDS:
					move = Paginator.Action.BACKWARDS;
					break;
				case Emojis.FORWARD:
					move = Paginator.Action.FORWARD;
					break;
				case Emojis.WASTEBASKET:
					move = Paginator.Action.REMOVE;
					break;
				default:
					return;
			}
			((Paginator) buttonAction.getObject()).switchPage(move);
		}, timeoutContext -> {

		}, removeAction -> {
			var removeContext = removeAction.getCtx();
			removeContext.deleteReply();
			removeContext.getCache().getButtonActionCache().removeButtonAction(removeAction.getId());
		}),
		PURGE_PROMPT(ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES, (button, buttonAction) -> {
			var emoji = button.getEmoji();
			var purgeProcessor = (PurgeProcessor) buttonAction.getObject();
			PurgeProcessor.PromptAction action;
			switch (emoji.getName()) {
				case Emojis.CHECK:
					action = PurgeProcessor.PromptAction.ACCEPT;
					break;
				case Emojis.CROSS:
					action = PurgeProcessor.PromptAction.DENY;
					break;
				case Emojis.WASTEBASKET:
					action = PurgeProcessor.PromptAction.REMOVE;
					break;
				default:
					return;
			}
			purgeProcessor.processPrompt(action);

		}, timeoutContext -> {

		}, removeAction -> {
			var removeContext = removeAction.getCtx();
			removeContext.deleteReply();
			removeContext.getCache().getButtonActionCache().removeButtonAction(removeAction.getId());
		});

		private final ExpirationPolicy expirationPolicy;
		private final long expirationDuration;
		private final TimeUnit expirationUnit;
		private final BiConsumer<Button, ButtonAction> buttonConsumer;
		private final Consumer<CommandContext> timeoutAction;
		private final Consumer<ButtonAction> removeAction;

		ActionType(ExpirationPolicy expirationPolicy, long expirationDuration, TimeUnit expirationUnit, BiConsumer<Button, ButtonAction> buttonConsumer,
		           Consumer<CommandContext> timeoutAction, Consumer<ButtonAction> removeAction) {
			this.expirationPolicy = expirationPolicy;
			this.expirationDuration = expirationDuration;
			this.expirationUnit = expirationUnit;
			this.buttonConsumer = buttonConsumer;
			this.timeoutAction = timeoutAction;
			this.removeAction = removeAction;
		}

		public ExpirationPolicy getExpirationPolicy() {
			return expirationPolicy;
		}

		public long getExpirationDuration() {
			return expirationDuration;
		}

		public TimeUnit getExpirationUnit() {
			return expirationUnit;
		}

		public BiConsumer<Button, ButtonAction> getButtonConsumer() {
			return buttonConsumer;
		}

		public Consumer<CommandContext> getTimeoutAction() {
			return timeoutAction;
		}

		public Consumer<ButtonAction> getRemoveAction() {
			return removeAction;
		}
	}
}