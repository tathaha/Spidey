package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dev.mlnr.spidey.utils.Utils.createEmbedBuilder;

public class ArgumentUtils
{
    private static final Pattern ID_REGEX = Pattern.compile("(\\d{17,18})");

    private ArgumentUtils() {}

    public static OptionalInt parseArgumentAsUnsignedInt(final String argument, final CommandContext ctx)
    {
        try
        {
            return OptionalInt.of(Integer.parseUnsignedInt(argument));
        }
        catch (final NumberFormatException ex)
        {
            ctx.replyError("Entered value is either negative or not a number");
            return OptionalInt.empty();
        }
    }

    public static void parseArgumentAsTextChannel(final String argument, final CommandContext ctx, final Consumer<TextChannel> consumer)
    {
        parseArgumentAsMentionable(argument, ctx, mentionable -> consumer.accept(((TextChannel) mentionable)), Message.MentionType.CHANNEL);
    }

    public static void parseArgumentAsRole(final String argument, final CommandContext ctx, final Consumer<Role> consumer)
    {
        parseArgumentAsMentionable(argument, ctx, mentionable -> consumer.accept(((Role) mentionable)), Message.MentionType.ROLE);
    }

    public static void parseArgumentAsUser(final String argument, final CommandContext ctx, final Consumer<User> consumer)
    {
        parseArgumentAsMentionable(argument, ctx, mentionable -> consumer.accept(((User) mentionable)), Message.MentionType.USER);
    }

    private static void parseArgumentAsMentionable(final String argument, final CommandContext ctx, final Consumer<IMentionable> consumer, final Message.MentionType type)
    {
        final var message = ctx.getMessage();
        final var guild = ctx.getGuild();
        final var author = ctx.getAuthor();
        final var typeName = type.name().toLowerCase();
        final var notFound = StringUtils.capitalize(typeName) + " not found";
        final var embedBuilder = createEmbedBuilder(author).setColor(0xFEFEFE);

        if (type.getPattern().matcher(argument).matches())
        {
            final var mentionable = message.getMentions(type).get(0);
            consumer.accept(mentionable);
            return;
        }

        final var idMatcher = ID_REGEX.matcher(argument);
        if (idMatcher.matches())
        {
            final var mentionableId = Long.parseLong(idMatcher.group());
            if (type == Message.MentionType.USER)
            {
                if (mentionableId == author.getIdLong())
                {
                    consumer.accept(author);
                    return;
                }
                final var jda = ctx.getJDA();
                final var selfUser = jda.getSelfUser();
                if (mentionableId == selfUser.getIdLong())
                {
                    consumer.accept(selfUser);
                    return;
                }
                jda.retrieveUserById(mentionableId).queue(consumer, failure -> ctx.replyError("User not found"));
                return;
            }
            final var mentionable = type == Message.MentionType.CHANNEL ? guild.getTextChannelById(mentionableId) : guild.getRoleById(mentionableId);
            if (mentionable == null)
            {
                ctx.replyError(notFound);
                return;
            }
            consumer.accept(mentionable);
            return;
        }

        if (argument.length() >= 2 && argument.length() <= 32)
        {
            if (type == Message.MentionType.USER)
            {
                if (argument.equalsIgnoreCase(ctx.getMember().getEffectiveName()))
                {
                    consumer.accept(author);
                    return;
                }
                message.getGuild().retrieveMembersByPrefix(argument, 10)
                    .onSuccess(members ->
                    {
                        if (members.isEmpty())
                        {
                            ctx.replyError("User not found");
                            return;
                        }
                        if (members.size() == 1)
                        {
                            consumer.accept(members.get(0).getUser());
                            return;
                        }
                        createMentionableSelection(embedBuilder, members.stream().map(Member::getUser).collect(Collectors.toList()), ctx, "user",
                                mentionable -> mentionable.getAsMention() + " - **" + ((User) mentionable).getAsTag() + "**", choice -> consumer.accept(members.get(choice).getUser()));
                    });
                return;
            }
            final var mentionables = type == Message.MentionType.CHANNEL ? guild.getTextChannelsByName(argument, true) : guild.getRolesByName(argument, true);
            if (mentionables.isEmpty())
            {
                ctx.replyError("No " + typeName.toLowerCase() + "s with given name found");
                return;
            }
            if (mentionables.size() == 1)
            {
                consumer.accept(mentionables.get(0));
                return;
            }
            createMentionableSelection(embedBuilder, mentionables, ctx, typeName, mentionable -> mentionable.getAsMention() + " - ID: " + mentionable.getIdLong(),
                    choice -> consumer.accept(mentionables.get(choice)));
            return;
        }
        ctx.replyError(notFound);
    }

    public static void createMentionableSelection(final EmbedBuilder selectionBuilder, final List<? extends IMentionable> mentionables, final CommandContext ctx, final String type,
                                                  final Function<IMentionable, String> mapper, final IntConsumer choiceConsumer)
    {
        StringUtils.createSelection(selectionBuilder, mentionables, ctx, type, mapper::apply, choiceConsumer);
    }
}