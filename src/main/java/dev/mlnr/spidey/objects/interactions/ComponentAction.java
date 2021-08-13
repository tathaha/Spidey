package dev.mlnr.spidey.objects.interactions;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.interactions.buttons.AkinatorGame;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.buttons.Paginator;
import dev.mlnr.spidey.objects.interactions.buttons.PurgeProcessor;
import dev.mlnr.spidey.objects.interactions.dropdowns.MusicSearchDropdown;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public abstract class ComponentAction {
	private final String id;
	public final CommandContext ctx;
	private final ActionType type;
	private final long authorId;
	private final ComponentActionCache componentActionCache;

	protected ComponentAction(String id, CommandContext ctx, ActionType type, ComponentActionCache componentActionCache) {
		this.id = id;
		this.ctx = ctx;
		this.type = type;
		this.authorId = ctx.getUser().getIdLong();
		this.componentActionCache = componentActionCache;

		componentActionCache.addAction(this);
	}

	public final String getId() {
		return id;
	}

	public final CommandContext getCtx() {
		return ctx;
	}

	public final ActionType getType() {
		return type;
	}

	public final long getAuthorId() {
		return authorId;
	}

	public final void uncacheAndDelete() {
		componentActionCache.removeAction(this);
	}

	public final void uncache() {
		componentActionCache.removeAction(this, false);
	}

	public abstract Object getObject();

	public enum ActionType {
		// buttons
		PAGINATOR(ExpirationPolicy.CREATED, 5, TimeUnit.MINUTES, (moveName, action) -> {
			var move = Paginator.Action.valueOf(moveName);
			((Paginator) action.getObject()).switchPage(move);
		}),
		PURGE_PROMPT(ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES, (actionName, action) -> {
			var promptAction = PurgeProcessor.PromptAction.valueOf(actionName);
			((PurgeProcessor) action.getObject()).processPrompt(promptAction);
		}),
		AKINATOR(ExpirationPolicy.CREATED, 10, TimeUnit.MINUTES, (answerName, action) -> {
			var answer = AkinatorGame.Answer.valueOf(answerName);
			((AkinatorGame) action.getObject()).answerCurrentQuestion(answer);
		}),
		// dropdowns
		MUSIC_SEARCH_DROPDOWN(ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES, (link, action) -> {
			((MusicSearchDropdown) action.getObject()).load(link);
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

	public static class Context {
		private final String id;
		private final CommandContext ctx;
		private final ComponentActionCache componentActionCache;

		public Context(String id, CommandContext ctx, ComponentActionCache componentActionCache) {
			this.id = id;
			this.ctx = ctx;
			this.componentActionCache = componentActionCache;
		}

		public final String getId() {
			return id;
		}

		public final CommandContext getCtx() {
			return ctx;
		}

		public final ComponentActionCache getComponentActionCache() {
			return componentActionCache;
		}
	}
}