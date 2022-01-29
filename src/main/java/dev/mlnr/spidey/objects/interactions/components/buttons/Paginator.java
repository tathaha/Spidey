package dev.mlnr.spidey.objects.interactions.components.buttons;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.commands.slash.SlashCommandContext;
import dev.mlnr.spidey.objects.interactions.components.ComponentAction;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.function.BiConsumer;

public class Paginator extends ComponentAction {
	private final int totalPages;
	private final BiConsumer<Integer, EmbedBuilder> pagesConsumer;

	private int currentPage;

	public static void create(Paginator.Context context) {
		new Paginator(context);
	}

	private Paginator(Paginator.Context context) {
		super(context.getId(), context.getCtx(), ComponentAction.ActionType.PAGINATOR, context.getComponentActionCache());
		this.totalPages = context.getTotalPages();
		this.pagesConsumer = context.getPagesConsumer();
	}

	public void switchPage(Paginator.Action action) {
		var newPageBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);
		var i18n = ctx.getI18n();
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
				uncacheAndDelete();
				return;
		}
		ctx.editReply(newPageBuilder);
	}

	public enum Action {
		BACKWARDS,
		FORWARD,
		REMOVE
	}

	public static class Context extends ComponentAction.Context {
		private final int totalPages;
		private final BiConsumer<Integer, EmbedBuilder> pagesConsumer;

		public Context(String id, SlashCommandContext ctx, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer,
		               ComponentActionCache componentActionCache) {
			super(id, ctx, componentActionCache);
			this.totalPages = totalPages;
			this.pagesConsumer = pagesConsumer;
		}

		public int getTotalPages() {
			return totalPages;
		}

		public BiConsumer<Integer, EmbedBuilder> getPagesConsumer() {
			return pagesConsumer;
		}
	}
}