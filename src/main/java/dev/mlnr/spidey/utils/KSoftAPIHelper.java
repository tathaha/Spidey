package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.utils.requests.API;
import dev.mlnr.spidey.utils.requests.Requester;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.awt.*;

public class KSoftAPIHelper
{
    private KSoftAPIHelper()
    {
        super();
    }

    public static MessageEmbed getImage(final String query, final Member author, final boolean nsfw)
    {
        final var eb = Utils.createEmbedBuilder(author.getUser());
        final var json = getImageJson(author.getGuild().getIdLong(), query);
        eb.setColor(nsfw ? Color.PINK : Color.GREEN);
        eb.setAuthor(json.getString("title"), json.getString("source"));
        eb.setImage(json.getString("image_url"));
        eb.setDescription("A random picture from r/" + query);
        return eb.build();
    }

    private static DataObject getImageJson(final long guildId, final String query)
    {
        final var response = Requester.executeRequest("https://api.ksoft.si/images/rand-reddit/" + query + "?span=month", API.KSOFT);
        final var source = response.getString("source");
        if (Cache.isPostCached(guildId, source))
            return getImageJson(guildId, query); // TODO fix ratelimit error
        Cache.cachePost(guildId, source);
        return response;
    }
}