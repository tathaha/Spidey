package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.MusicUtils;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PaginatorEvent extends ListenerAdapter {
	private final Cache cache;

	public PaginatorEvent(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
		var paginatorCache = cache.getPaginatorCache();
		var messageId = event.getMessageIdLong();
		if (!paginatorCache.isPaginator(messageId)) {
			return;
		}
		var paginator = paginatorCache.getPaginator(messageId);
		if (event.getUserIdLong() != paginator.getAuthorId()) {
			return;
		}
		var reactionEmote = event.getReactionEmote();
		if (!reactionEmote.isEmoji()) {
			return;
		}
		var emoji = reactionEmote.getEmoji();
		var channel = event.getChannel();
		var currentPage = paginator.getCurrentPage();
		var pagesConsumer = paginator.getPagesConsumer();
		var newPageBuilder = MusicUtils.createMusicResponseBuilder();
		var totalPages = paginator.getTotalPages();

		switch (emoji) {
			case Emojis.WASTEBASKET:
				paginatorCache.removePaginator(messageId);
				return;
			case Emojis.BACKWARDS:
				if (currentPage == 0) {
					return;
				}
				var previousPage = currentPage - 1;
				pagesConsumer.accept(previousPage, newPageBuilder);
				newPageBuilder.setFooter("Page " + (previousPage + 1) + "/" + totalPages);
				paginator.modifyCurrentPage(-1);
				break;
			case Emojis.FORWARD:
				var nextPage = currentPage + 1;
				if (nextPage == totalPages) {
					return;
				}
				pagesConsumer.accept(nextPage, newPageBuilder);
				newPageBuilder.setFooter("Page " + (nextPage + 1) + "/" + totalPages);
				paginator.modifyCurrentPage(+1);
				break;
			default:
				return;
		}
		channel.editMessageById(messageId, newPageBuilder.build()).queue();
	}
}