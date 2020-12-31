package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class UserUtils
{
    private static final Pattern ID_REGEX = Pattern.compile("(\\d{17,18})");

    private UserUtils() {}

    public static void retrieveUser(final String argument, final CommandContext ctx, final Consumer<User> consumer)
    {
        final var message = ctx.getMessage();

        if (Message.MentionType.USER.getPattern().matcher(argument).matches()) // @User
        {
            final var user = message.getMentionedUsers().get(0);
            consumer.accept(user);
            return;
        }

        final var idMatcher = ID_REGEX.matcher(argument);                      // 12345678901234567890
        if (idMatcher.matches())
        {
            ctx.getJDA().retrieveUserById(idMatcher.group()).queue(consumer, failure -> ctx.replyError("User not found"));
            return;
        }

        if (argument.length() >= 2 && argument.length() <= 32)
        {
            message.getGuild().retrieveMembersByPrefix(argument, 1)            // username/nickname
                    .onSuccess(members ->
                    {
                        if (members.isEmpty())
                        {
                            ctx.replyError("User not found");
                            return;
                        }
                        consumer.accept(members.get(0).getUser());
                    });
        }
    }
}