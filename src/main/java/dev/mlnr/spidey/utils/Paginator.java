package dev.mlnr.spidey.utils;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.function.BiConsumer;

public class Paginator
{
    private int currentPage;

    private final long invokeChannelId;
    private final long invokeMessageId;
    private final long authorId;
    private final int totalPages;
    private final BiConsumer<Integer, EmbedBuilder> pagesConsumer;

    public Paginator(final long invokeChannelId, final long invokeMessageId, final long authorId, final int totalPages, final BiConsumer<Integer, EmbedBuilder> pagesConsumer)
    {
        this.invokeChannelId = invokeChannelId;
        this.invokeMessageId = invokeMessageId;
        this.authorId = authorId;
        this.totalPages = totalPages;
        this.pagesConsumer = pagesConsumer;
    }

    public void modifyCurrentPage(final int i)
    {
        this.currentPage += i;
    }

    public int getCurrentPage()
    {
        return this.currentPage;
    }

    public long getInvokeChannelId()
    {
        return this.invokeChannelId;
    }

    public long getInvokeMessageId()
    {
        return this.invokeMessageId;
    }

    public long getAuthorId()
    {
        return this.authorId;
    }

    public int getTotalPages()
    {
        return this.totalPages;
    }

    public BiConsumer<Integer, EmbedBuilder> getPagesConsumer()
    {
        return this.pagesConsumer;
    }
}