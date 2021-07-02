package dev.mlnr.spidey.objects.interactions.buttons;

import dev.mlnr.spidey.cache.InteractionCache;
import dev.mlnr.spidey.objects.I18n;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.Interaction;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.function.BiConsumer;

public class Paginator implements Interaction {
	private final String id;
	private final CommandContext ctx;
	private final int totalPages;
	private final I18n i18n;
	private final BiConsumer<Integer, EmbedBuilder> pagesConsumer;
	private final InteractionCache interactionCache;

	private int currentPage;

	public Paginator(String id, CommandContext ctx, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer,
	                 InteractionCache interactionCache) {
		this.id = id;
		this.ctx = ctx;
		this.totalPages = totalPages;
		this.i18n = ctx.getI18n();
		this.pagesConsumer = pagesConsumer;
		this.interactionCache = interactionCache;
	}

	public void switchPage(Paginator.Action action) {
		var newPageBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);
		switch (action) {
			case BACKWARDS:
				if (currentPage == 0) {
					return;
				}
				var previousPage = currentPage - 1;
				pagesConsumer.accept(previousPage, newPageBuilder);
				newPageBuilder.setFooter(i18n.get("paginator.page", previousPage + 1, totalPages));
				currentPage--;
				break;
			case FORWARD:
				var nextPage = currentPage + 1;
				if (nextPage == totalPages) {
					return;
				}
				pagesConsumer.accept(nextPage, newPageBuilder);
				newPageBuilder.setFooter(i18n.get("paginator.page", nextPage + 1, totalPages));
				currentPage++;
				break;
			case REMOVE:
				interactionCache.removeInteraction(this);
				return;
		}
		ctx.editReply(newPageBuilder);
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
	public InteractionType getType() {
		return Interaction.InteractionType.PAGINATOR;
	}

	@Override
	public Object getObject() {
		return this;
	}

	@Override
	public long getAuthorId() {
		return ctx.getUser().getIdLong();
	}

	public enum Action {
		BACKWARDS,
		FORWARD,
		REMOVE
	}
}