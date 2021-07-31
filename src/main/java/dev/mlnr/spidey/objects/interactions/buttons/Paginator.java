package dev.mlnr.spidey.objects.interactions.buttons;

import dev.mlnr.spidey.cache.ComponentActionCache;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.interactions.ComponentAction;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.function.BiConsumer;

public class Paginator extends ComponentAction {
	private final int totalPages;
	private final BiConsumer<Integer, EmbedBuilder> pagesConsumer;

	private int currentPage;

	public Paginator(String id, CommandContext ctx, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer,
	                 ComponentActionCache componentActionCache) {
		super(id, ctx, ComponentAction.ActionType.PAGINATOR, componentActionCache);
		this.totalPages = totalPages;
		this.pagesConsumer = pagesConsumer;
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

	@Override
	public Object getObject() {
		return this;
	}

	public enum Action {
		BACKWARDS,
		FORWARD,
		REMOVE
	}
}