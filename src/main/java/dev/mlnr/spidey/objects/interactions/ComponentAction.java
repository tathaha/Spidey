package dev.mlnr.spidey.objects.interactions;

import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.buttons.Paginator;
import dev.mlnr.spidey.objects.interactions.buttons.PurgeProcessor;
import dev.mlnr.spidey.objects.interactions.dropdowns.YouTubeSearchDropdown;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public interface ComponentAction {
	String getId();

	CommandContext getCtx();

	ActionType getType();

	Object getObject();

	long getAuthorId();

	enum ActionType {
		// buttons
		PAGINATOR(ExpirationPolicy.CREATED, 5, TimeUnit.MINUTES, (moveName, interaction) -> {
			var move = Paginator.Action.valueOf(moveName);
			((Paginator) interaction.getObject()).switchPage(move);
		}),
		PURGE_PROMPT(ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES, (actionName, interaction) -> {
			var action = PurgeProcessor.PromptAction.valueOf(actionName);
			((PurgeProcessor) interaction.getObject()).processPrompt(action);
		}),
		// dropdowns
		YOUTUBE_SEARCH_DROPDOWN(ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES, (link, interaction) -> {
			((YouTubeSearchDropdown) interaction.getObject()).loadVideo(link);
		});

		private final ExpirationPolicy expirationPolicy;
		private final long expirationDuration;
		private final TimeUnit expirationUnit;
		private final BiConsumer<String, ComponentAction> actionConsumer;

		ActionType(ExpirationPolicy expirationPolicy, long expirationDuration, TimeUnit expirationUnit, BiConsumer<String, ComponentAction> actionConsumer) {
			this.expirationPolicy = expirationPolicy;
			this.expirationDuration = expirationDuration;
			this.expirationUnit = expirationUnit;
			this.actionConsumer = actionConsumer;
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

		public BiConsumer<String, ComponentAction> getActionConsumer() {
			return actionConsumer;
		}
	}
}