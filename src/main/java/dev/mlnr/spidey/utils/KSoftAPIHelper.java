package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.utils.requests.Requester;
import dev.mlnr.spidey.utils.requests.api.API;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.awt.*;
import java.time.Instant;

public class KSoftAPIHelper
{
    private KSoftAPIHelper() {}

    public static MessageEmbed getNsfwImage(final String query, final Member author)
    {
        final var eb = Utils.createEmbedBuilder(author.getUser());
        final var json = getImageJson(query);
        eb.setColor(Color.PINK);
        eb.setAuthor(json.getString("title"), json.getString("source"));
        eb.setImage(json.getString("image_url"));
        eb.setDescription("A random post from [r/" + query + "](https://reddit.com/r/" + query + ")");
        eb.setTimestamp(Instant.ofEpochSecond(json.getInt("created_at")));
        return eb.build();
    }

    private static DataObject getImageJson(final String query)
    {
        return Requester.executeApiRequest(API.KSOFT_NSFW, query);
    }
}