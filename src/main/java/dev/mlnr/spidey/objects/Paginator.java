package dev.mlnr.spidey.objects;

import dev.mlnr.spidey.cache.PaginatorCache;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.function.BiConsumer;

public class Paginator {
	private final long invokeChannelId;
	private final long paginatorMessageId;
	private final long invokeMessageId;
	private final long authorId;
	private final int totalPages;
	private final BiConsumer<Integer, EmbedBuilder> pagesConsumer;
	private int currentPage;

	private final PaginatorCache paginatorCache;

	public Paginator(long invokeChannelId, long paginatorMessageId, long invokeMessageId, long authorId, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer, PaginatorCache paginatorCache) {
		this.invokeChannelId = invokeChannelId;
		this.paginatorMessageId = paginatorMessageId;
		this.invokeMessageId = invokeMessageId;
		this.authorId = authorId;
		this.totalPages = totalPages;
		this.pagesConsumer = pagesConsumer;

		this.paginatorCache = paginatorCache;
	}

	public void switchPage(Paginator.Action action, TextChannel channel) {
		var newPageBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);
		switch (action) {
			case REMOVE:
				paginatorCache.removePaginator(paginatorMessageId);
				return;
			case BACKWARDS:
				if (currentPage == 0) {
					return;
				}
				var previousPage = currentPage - 1;
				pagesConsumer.accept(previousPage, newPageBuilder);
				newPageBuilder.setFooter("Page " + (previousPage + 1) + "/" + totalPages);
				currentPage--;
				break;
			case FORWARD:
				var nextPage = currentPage + 1;
				if (nextPage == totalPages) {
					return;
				}
				pagesConsumer.accept(nextPage, newPageBuilder);
				newPageBuilder.setFooter("Page " + (nextPage + 1) + "/" + totalPages);
				currentPage++;
				break;
		}
		channel.editMessageById(paginatorMessageId, newPageBuilder.build()).queue();
	}

	public long getInvokeChannelId() {
		return this.invokeChannelId;
	}

	public long getInvokeMessageId() {
		return this.invokeMessageId;
	}

	public long getAuthorId() {
		return this.authorId;
	}

	public enum Action {
		BACKWARDS,
		FORWARD,
		REMOVE
	}
}