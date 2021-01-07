package dev.mlnr.spidey.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.PaginatorCache;
import dev.mlnr.spidey.handlers.command.CommandHandler;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntConsumer;

import static dev.mlnr.spidey.utils.Utils.purgeMessages;
import static java.lang.Math.min;

public class StringUtils
{
    private StringUtils() {}

    public static String pluralize(final long size, final String base)
    {
        if (size == 1)
            return "1 " + base;
        return size + " " + base + "s";
    }

    public static String getSimilarCommand(final String command)
    {
        return CommandHandler.getCommands().keySet().stream().filter(invoke -> getSimilarity(invoke, command) > 0.5).findFirst().orElse(null);
    }

    private static double getSimilarity(final String s1, final String s2) // https://stackoverflow.com/a/16018452/9046789
    {
        var longer = s1;
        var shorter = s2;
        if (s1.length() < s2.length())
        {
            longer = s2;
            shorter = s1;
        }
        final var longerLength = longer.length();
        if (longerLength == 0)
            return 1.0;
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    private static int editDistance(String s1, String s2)
    {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        final var costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++)
        {
            var lastValue = i;
            for (var j = 0; j <= s2.length(); j++)
            {
                if (i == 0)
                {
                    costs[j] = j;
                    continue;
                }
                if (j <= 0)
                    continue;
                var newValue = costs[j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1))
                    newValue = min(min(newValue, lastValue), costs[j]) + 1;
                costs[j - 1] = lastValue;
                lastValue = newValue;
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static <T> void createSelection(final EmbedBuilder selectionBuilder, final List<T> elements, final CommandContext ctx, final String selectionType, final Function<T, String> mapper,
                                           final IntConsumer choiceConsumer)
    {
        final var descriptionBuilder = selectionBuilder.getDescriptionBuilder();
        final var size = min(elements.size(), 10);
        for (var i = 0; i < size; i++)
        {
            final var elem = elements.get(i);
            descriptionBuilder.append(i + 1).append(". ").append(mapper.apply(elem)).append("\n");
        }
        descriptionBuilder.append("\n\nType a number to select a ").append(selectionType).append(" or `cancel` to cancel the selection.");
        selectionBuilder.setDescription(descriptionBuilder.toString());

        final var channel = ctx.getTextChannel();
        channel.sendMessage(selectionBuilder.build()).queue(selectionMessage ->
        {
            final var message = ctx.getMessage();
            Spidey.getWaiter().waitForEvent(GuildMessageReceivedEvent.class, event -> event.getChannel().equals(channel) && event.getAuthor().equals(ctx.getAuthor()),
                    event ->
                    {
                        final var choiceMessage = event.getMessage();
                        final var content = choiceMessage.getContentRaw();
                        if (content.equalsIgnoreCase("cancel"))
                        {
                            purgeMessages(message, selectionMessage, choiceMessage);
                            return;
                        }
                        var choice = 0;
                        try
                        {
                            choice = Integer.parseUnsignedInt(content);
                        }
                        catch (final NumberFormatException ex)
                        {
                            ctx.replyError("Entered value is either negative or not a number");
                            return;
                        }
                        if (choice == 0 || choice > size)
                        {
                            ctx.replyError("Please enter a number from 1-" + size);
                            return;
                        }
                        choiceConsumer.accept(choice - 1);
                        Utils.deleteMessage(selectionMessage);
                    }, 1, TimeUnit.MINUTES,
                    () ->
                    {
                        ctx.replyError("Sorry, you took too long");
                        purgeMessages(message, selectionMessage);
                    });
        });
    }

    public static String capitalize(final String string)
    {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static void createQueuePaginator(final Message message, final Deque<AudioTrack> queue)
    {
        final var tracksChunks = ListUtils.partition(new ArrayList<>(queue), 10);
        final var descriptions = new HashMap<Integer, StringBuilder>();

        var currentTrack = 0;
        var chunk = 0;
        for (final var tracks : tracksChunks)
        {
            final var chunkBuilder = new StringBuilder();
            for (final var track : tracks)
            {
                chunkBuilder.append(currentTrack + 1).append(". ").append(MusicUtils.formatTrack(track)).append(" [<@").append(track.getUserData(Long.class)).append(">]\n");
                currentTrack++;
            }
            descriptions.put(chunk, chunkBuilder);
            chunk++;
        }

        final var length = queue.stream().mapToLong(track -> track.getInfo().length).sum();
        final var size = queue.size();
        final var pluralized = size == 1 ? "is **1** track" : "are **" + size + "** tracks";
        PaginatorCache.createPaginator(message, descriptions.size(), (page, embedBuilder) ->
        {
            embedBuilder.setAuthor("Queue for " + message.getGuild().getName());
            embedBuilder.setDescription(descriptions.get(page));
            embedBuilder.appendDescription("\n\nThere " + pluralized + " in the queue with total length of **" + MusicUtils.formatDuration(length) + "**");
        });
    }
}