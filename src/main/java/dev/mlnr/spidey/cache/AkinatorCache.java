package dev.mlnr.spidey.cache;

import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import dev.mlnr.spidey.objects.akinator.AkinatorContext;
import dev.mlnr.spidey.objects.akinator.AkinatorData;
import dev.mlnr.spidey.utils.Utils;
import kong.unirest.UnirestException;
import net.dv8tion.jda.api.entities.User;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static dev.mlnr.spidey.utils.Utils.sendMessage;

public class AkinatorCache
{
    private static final Map<Long, AkinatorData> AKINATOR_CACHE = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expiration(2, TimeUnit.MINUTES)
            .build();

    private AkinatorCache() {}

    public static void createAkinator(final User user, final AkinatorContext ctx)
    {
        final var i18n = ctx.getI18n();
        final var channel = ctx.getChannel();
        try
        {
            final var akinator = new AkiwrapperBuilder().build();
            AKINATOR_CACHE.put(user.getIdLong(), new AkinatorData(akinator));
            final var embedBuilder = Utils.createEmbedBuilder(user).setAuthor(i18n.get("commands.akinator.other.of", user.getAsTag())).setColor(Utils.SPIDEY_COLOR);
            embedBuilder.setDescription(i18n.get("commands.akinator.other.question", 1) + " " + akinator.getCurrentQuestion().getQuestion());
            sendMessage(channel, embedBuilder.build());
        }
        catch (final ServerNotFoundException | UnirestException ex)
        {
            sendMessage(channel, i18n.get("commands.akinator.other.couldnt_create"));
        }
    }

    public static AkinatorData getAkinatorData(final long userId)
    {
        return AKINATOR_CACHE.get(userId);
    }

    public static boolean hasAkinator(final long userId)
    {
        return AKINATOR_CACHE.containsKey(userId);
    }

    public static void removeAkinator(final long userId)
    {
        AKINATOR_CACHE.remove(userId);
    }
}