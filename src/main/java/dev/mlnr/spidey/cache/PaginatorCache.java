package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Paginator;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class PaginatorCache {
	private final Map<Long, Paginator> paginatorMap = ExpiringMap.builder()
			.expirationPolicy(ExpirationPolicy.CREATED)
			.expiration(3, TimeUnit.MINUTES)
			.asyncExpirationListener((messageId, paginator) -> removePaginator((long) messageId, ((Paginator) paginator)))
			.build();

	private final JDA jda;

	public PaginatorCache(JDA jda) {
		this.jda = jda;
	}

	public void createPaginator(Message message, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer) {
		var channel = message.getTextChannel();
		var embedBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);
		embedBuilder.setFooter("Page 1/" + totalPages);
		pagesConsumer.accept(0, embedBuilder);

		channel.sendMessage(embedBuilder.build()).queue(paginatorMessage -> {
			paginatorMap.put(paginatorMessage.getIdLong(), new Paginator(channel.getIdLong(), message.getIdLong(), message.getAuthor().getIdLong(), totalPages, pagesConsumer));

			Utils.addReaction(paginatorMessage, Emojis.BACKWARDS);
			Utils.addReaction(paginatorMessage, Emojis.FORWARD);
			Utils.addReaction(paginatorMessage, Emojis.WASTEBASKET);
		});
	}

	public Paginator getPaginator(long messageId) {
		return paginatorMap.get(messageId);
	}

	public boolean isPaginator(long messageId) {
		return paginatorMap.containsKey(messageId);
	}

	public void removePaginator(long messageId) {
		removePaginator(messageId, null);
	}

	private void removePaginator(long messageId, Paginator removedPaginator) {
		var paginator = removedPaginator;
		if (paginator == null) {
			paginator = getPaginator(messageId);
			if (paginator == null) {
				return;
			}
			paginatorMap.remove(messageId);
		}
		var channel = jda.getTextChannelById(paginator.getInvokeChannelId());
		if (channel == null) {
			return;
		}
		channel.purgeMessagesById(paginator.getInvokeMessageId(), messageId);
	}
}