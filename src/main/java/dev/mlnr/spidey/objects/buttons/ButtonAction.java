package dev.mlnr.spidey.objects.buttons;

import dev.mlnr.spidey.objects.command.CommandContext;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public interface ButtonAction {
	String getId();

	CommandContext getCtx();

	ActionType getType();

	Object getObject();

	long getAuthorId();

	enum ActionType {
		PAGINATION(ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES, (moveName, buttonAction) -> {
			var move = Paginator.Action.valueOf(moveName);
			((Paginator) buttonAction.getObject()).switchPage(move);
		}),
		PURGE_PROMPT(ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES, (actionName, buttonAction) -> {
			var action = PurgeProcessor.PromptAction.valueOf(actionName);
			var purgeProcessor = (PurgeProcessor) buttonAction.getObject();
			purgeProcessor.processPrompt(action);
		});

		private final ExpirationPolicy expirationPolicy;
		private final long expirationDuration;
		private final TimeUnit expirationUnit;
		private final BiConsumer<String, ButtonAction> buttonConsumer;

		ActionType(ExpirationPolicy expirationPolicy, long expirationDuration, TimeUnit expirationUnit, BiConsumer<String, ButtonAction> buttonConsumer) {
			this.expirationPolicy = expirationPolicy;
			this.expirationDuration = expirationDuration;
			this.expirationUnit = expirationUnit;
			this.buttonConsumer = buttonConsumer;
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

		public BiConsumer<String, ButtonAction> getButtonConsumer() {
			return buttonConsumer;
		}
	}
}