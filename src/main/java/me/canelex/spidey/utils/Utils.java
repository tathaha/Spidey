package me.canelex.spidey.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.classgraph.ClassGraph;
import me.canelex.jda.api.EmbedBuilder;
import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.*;
import me.canelex.jda.api.utils.data.DataObject;
import me.canelex.spidey.Core;
import me.canelex.spidey.Events;
import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.objects.invites.WrappedInvite;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static me.canelex.jda.api.entities.Activity.listening;
import static me.canelex.jda.api.entities.Activity.watching;

public class Utils
{
    private static final String INVITE_LINK = "https://discordapp.com/oauth2/authorize?client_id=468523263853592576&scope=bot&permissions=1342188724";
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
    private static final ClassGraph graph = new ClassGraph().whitelistPackages("me.canelex.spidey.commands").enableAllInfo().ignoreClassVisibility();
    private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Spidey").setUncaughtExceptionHandler((t, e) -> LOG.error("There was an exception in thread {}: {}", t.getName(), e.getMessage())).build();
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(threadFactory);
    private static final char[] SUFFIXES = {'k', 'M', 'B'};
    private static final HashMap<Long, String> PREFIXES = new HashMap<>();
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private Utils()
    {
        super();
    }

    public static boolean hasPerm(final Member toCheck, final Permission perm)
    {
        return toCheck.hasPermission(perm);
    }

    public static void sendMessage(final MessageChannel ch, final String toSend)
    {
        ch.sendMessage(toSend).queue();
    }

    public static void sendMessage(final MessageChannel ch, final MessageEmbed embed)
    {
        ch.sendMessage(embed).queue();
    }

    public static void sendPrivateMessage(final User user, final String toSend)
    {
        user.openPrivateChannel()
            .flatMap(channel -> channel.sendMessage(toSend))
            .queue();
    }

    public static void deleteMessage(final Message msg)
    {
        msg.delete().queue();
    }

    public static boolean canSetVanityUrl(final Guild g)
    {
        return g.getFeatures().contains("VANITY_URL");
    }

    public static String replaceLast(final String text, final String regex, final String replacement)
    {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    public static EmbedBuilder createEmbedBuilder(final User u)
    {
        return new EmbedBuilder().setFooter("Command executed by " + u.getAsTag(), u.getAvatarUrl());
    }

    public static String getInviteUrl()
    {
        return INVITE_LINK;
    }

    public static void sendPrivateMessageFormat(final User u, final String message, final Object... args)
    {
        sendPrivateMessage(u, String.format(message, args));
    }

    public static void returnError(final String errMsg, final Message origin)
    {
        origin.addReaction(Emojis.CROSS).queue();
        origin.getTextChannel().sendMessage(String.format(":no_entry: %s.", errMsg))
                               .delay(Duration.ofSeconds(5))
                               .flatMap(Message::delete)
                               .flatMap(ignored -> origin.delete())
                               .queue();
    }

    public static String generateSuccess(final int count, final User u)
    {
        return ":white_check_mark: Successfully deleted **" + count + "** message" + (count > 1 ? "s" : "") + (u == null ? "." : String.format(" by user **%s**.", u.getAsTag()));
    }

    public static void startup()
    {
        final var jda = Core.getJDA();
        final var commandsMap = Core.getCommands();
        final ArrayList<Supplier<Activity>> activities = new ArrayList<>(asList(
                () -> listening("your commands"),
                () -> watching("you"),
                () -> watching(jda.getGuildCache().size() + " guilds"),
                () -> watching(jda.getUserCache().size() + " users")
        ));

        commandsMap.clear(); //just to make sure that the commands map is empty
        try (final var result = graph.scan())
        {
            for (final var cls : result.getClassesImplementing("me.canelex.spidey.objects.command.ICommand"))
            {
                final var cmd = (ICommand) cls.loadClass().getDeclaredConstructor().newInstance();
                commandsMap.put(cmd.getInvoke(), cmd);
                cmd.getAliases().forEach(alias -> commandsMap.put(alias, cmd));
            }
        }
        catch (final Exception e)
        {
            LOG.error("There was an error while registering the commands!", e);
        }

        EXECUTOR.scheduleAtFixedRate(() ->
            jda.getPresence().setActivity(nextActivity(activities)), 0L, 30L, TimeUnit.SECONDS);
    }

    private static Activity nextActivity(final ArrayList<Supplier<Activity>> activities)
    {
        return activities.get(random.nextInt(activities.size())).get();
    }

    public static String getSiteContent(final String url)
    {
        var content = "";
        try
        {
            final var con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "me.canelex.spidey");

            var inputLine = "";
            final var response = new StringBuilder();

            try (final var in = new BufferedReader(new InputStreamReader(con.getInputStream())))
            {
                while ((inputLine = in.readLine()) != null)
                {
                    response.append(inputLine);
                }
            }
            con.disconnect();
            content = response.toString();
        }
        catch (final IOException e)
        {
            LOG.error("There was an error parsing the site content!", e);
        }
        return content;
    }

    public static DataObject getJson(final String url)
    {
        return DataObject.fromJson(getSiteContent(url));
    }

    public static void storeInvites(final Guild guild)
    {
        guild.retrieveInvites().queue(invites -> invites.forEach(invite -> Events.getInvites().put(invite.getCode(), new WrappedInvite(invite))), failure -> sendPrivateMessage(guild.getOwner().getUser(), "I'm not able to attach the invite a user joined with as i don't have permission to manage the server."));
    }

    public static String cleanString(final String original)
    {
        return StringEscapeUtils.unescapeJava(
            StringEscapeUtils.unescapeHtml4(
                original
                    .replaceAll("<.*?>", "")
                    .replaceAll("\"", "")));
    }

    public static String getBuildDate()
    {
        final var cal = Calendar.getInstance();
        cal.setTimeInMillis(new File("SpideyDev.jar").lastModified());
        return new SimpleDateFormat("EE, d.LLL Y | HH:mm:ss", new Locale("en", "EN")).format(cal.getTime());
    }

    public static String getCompactNumber(final long number)
    {
        final var sn = String.valueOf(number);
        final var length = sn.length();
        if (number < 1000)
            return sn;
        final var magnitude = (length - 1) / 3;
        var digits = (length - 1) % 3 + 1;

        var value = new char[4];
        for (var i = 0; i < digits; i++)
        {
            value[i] = sn.charAt(i);
        }
        if (digits == 1 && sn.charAt(1) != '0')
        {
            value[digits++] = '.';
            value[digits++] = sn.charAt(1);
        }
        value[digits++] = SUFFIXES[magnitude - 1];
        return new String(value, 0, digits);
    }

    public static void setPrefix(final long guildId, final String prefix)
    {
        MySQL.setPrefix(guildId, prefix);
        PREFIXES.put(guildId, prefix);
    }

    private static String getPrefixFromRequest(final long guildId)
    {
        final var tmp = MySQL.getPrefix(guildId);
        final var prefix = tmp.length() == 0 ? "s!" : tmp;
        PREFIXES.put(guildId, prefix);
        return prefix;
    }

    public static String getPrefix(final long guildId)
    {
        return Objects.requireNonNullElseGet(PREFIXES.get(guildId), () -> getPrefixFromRequest(guildId));
    }
}