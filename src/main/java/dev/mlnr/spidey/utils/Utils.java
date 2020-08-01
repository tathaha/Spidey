package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.Core;
import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.objects.cache.MessageCache;
import dev.mlnr.spidey.objects.command.CommandHandler;
import dev.mlnr.spidey.objects.invites.InviteData;
import dev.mlnr.spidey.utils.requests.API;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static net.dv8tion.jda.api.entities.Activity.listening;
import static net.dv8tion.jda.api.entities.Activity.watching;

public class Utils
{
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("EE, d.LLL y | HH:mm:ss");
    private static final Calendar CAL = Calendar.getInstance();
    public static final Pattern TEXT_PATTERN = Pattern.compile("[a-zA-Z0-9-_]+");

    private Utils()
    {
        super();
    }

    public static void sendMessage(final TextChannel ch, final String toSend)
    {
        if (ch.canTalk())
            ch.sendMessage(toSend).queue();
    }

    public static void sendMessage(final TextChannel ch, final MessageEmbed embed)
    {
        if (ch.canTalk() && ch.getGuild().getSelfMember().hasPermission(ch, Permission.MESSAGE_EMBED_LINKS))
            ch.sendMessage(embed).queue();
    }

    public static void deleteMessage(final Message msg)
    {
        final var channel = msg.getTextChannel();
        if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
            msg.delete().queue();
    }

    public static boolean canSetVanityUrl(final Guild g)
    {
        return g.getFeatures().contains("VANITY_URL");
    }

    public static EmbedBuilder createEmbedBuilder(final User u)
    {
        return new EmbedBuilder().setFooter("Command executed by " + u.getAsTag(), u.getEffectiveAvatarUrl());
    }

    public static void addReaction(final Message message, final String reaction)
    {
        if (message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION))
            message.addReaction(reaction).queue();
    }

    public static void returnError(final String errMsg, final Message origin)
    {
        final var channel = origin.getTextChannel();
        addReaction(origin, Emojis.CROSS);
        if (!channel.canTalk())
            return;
        channel.sendMessage(String.format(":no_entry: %s.", errMsg))
                .delay(Duration.ofSeconds(5))
                .flatMap(Message::delete)
                .queue(success -> deleteMessage(origin));
    }

    public static String generateSuccess(final int count, final User u)
    {
        return ":white_check_mark: Successfully deleted **" + count + "** message" + (count > 1 ? "s" : "") + (u == null ? "." : String.format(" by user **%s**.", u.getAsTag()));
    }

    public static void startup(final JDA jda)
    {
        CommandHandler.registerCommands();
        final var executor = Core.getExecutor();
        final ArrayList<Supplier<Activity>> activities = new ArrayList<>(asList(
                () -> listening("your commands"),
                () -> watching("you"),
                () -> watching(jda.getGuildCache().size() + " guilds"),
                () -> watching(jda.getUserCache().size() + " users")
        ));
        executor.scheduleAtFixedRate(() -> jda.getPresence().setActivity(nextActivity(activities)), 0, 30, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(() ->
                MessageCache.getCache().entrySet().removeIf(entry -> entry.getValue().getCreation().isBefore(OffsetDateTime.now().minusMinutes(10).toInstant())),
                1, 1, TimeUnit.HOURS);
    }

    private static Activity nextActivity(final ArrayList<Supplier<Activity>> activities)
    {
        return activities.get(RANDOM.nextInt(activities.size())).get();
    }

    public static DataObject getJson(final String url, final API api)
    {
        return DataObject.fromJson(Requester.executeRequest(url, api));
    }

    public static void storeInvites(final Guild guild)
    {
        if (guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER))
            guild.retrieveInvites().queue(invites -> invites.forEach(invite -> Cache.getInviteCache().put(invite.getCode(), new InviteData(invite))));
    }

    public static String getTime(final long millis)
    {
        CAL.setTimeInMillis(millis);
        return SDF.format(CAL.getTime());
    }

    public static int getColorHex(final int value, final int max)
    {
        final var r = ((255 * value) / max);
        final var g = (255 * (max - value)) / max;
        return ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (0);
    }
}
