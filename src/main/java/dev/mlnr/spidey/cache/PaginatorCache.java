package dev.mlnr.spidey.cache;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Paginator;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class PaginatorCache
{
    private static final Map<Long, Paginator> PAGINATOR_CACHE = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(3, TimeUnit.MINUTES)
            .asyncExpirationListener((messageId, paginator) -> removePaginator((long) messageId, ((Paginator) paginator)))
            .build();

    private PaginatorCache() {}

    public static void createPaginator(Message message, int totalPages, BiConsumer<Integer, EmbedBuilder> pagesConsumer)
    {
        var channel = message.getTextChannel();
        var author = message.getAuthor();
        var embedBuilder = new EmbedBuilder().setColor(Utils.SPIDEY_COLOR);
        embedBuilder.setFooter("Page 1/" + totalPages);
        pagesConsumer.accept(0, embedBuilder);

        channel.sendMessage(embedBuilder.build()).queue(paginatorMessage ->
        {
            var paginatorMessageId = paginatorMessage.getIdLong();
            var authorId = author.getIdLong();
            PAGINATOR_CACHE.put(paginatorMessageId, new Paginator(channel.getIdLong(), message.getIdLong(), authorId, totalPages, pagesConsumer));

            Utils.addReaction(paginatorMessage, Emojis.BACKWARDS);
            Utils.addReaction(paginatorMessage, Emojis.FORWARD);
            Utils.addReaction(paginatorMessage, Emojis.WASTEBASKET);

            Spidey.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class, ev -> ev.getMessageIdLong() == paginatorMessageId && ev.getUserIdLong() == authorId, ev -> {}, 1, TimeUnit.MINUTES,
                    () -> removePaginator(paginatorMessageId));
        });
    }

    public static Paginator getPaginator(long messageId)
    {
        return PAGINATOR_CACHE.get(messageId);
    }

    public static boolean isPaginator(long messageId)
    {
        return PAGINATOR_CACHE.containsKey(messageId);
    }

    public static void removePaginator(long messageId)
    {
        removePaginator(messageId, null);
    }

    private static void removePaginator(long messageId, Paginator removedPaginator)
    {
        var paginator = removedPaginator;
        if (paginator == null)
        {
            paginator = getPaginator(messageId);
            if (paginator == null)
                return;
            PAGINATOR_CACHE.remove(messageId);
        }
        var channel = Spidey.getJDA().getTextChannelById(paginator.getInvokeChannelId());
        if (channel == null)
            return;
        channel.purgeMessagesById(paginator.getInvokeMessageId(), messageId);
    }
}