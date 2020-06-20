package me.canelex.spidey.utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.awt.*;

public class KSoftAPIHelper
{
    private KSoftAPIHelper()
    {
        super();
    }

    public static MessageEmbed getImage(final String query, final User author, final boolean nsfw)
    {
        final var eb = Utils.createEmbedBuilder(author);
        final var json = getImageJson(query);
        eb.setColor(nsfw ? Color.PINK : Color.GREEN);
        eb.setAuthor(json.getString("title"), json.getString("source"));
        eb.setImage(json.getString("image_url"));
        eb.setDescription("A random picture from r/" + query);
        return eb.build();
    }

    private static DataObject getImageJson(final String query)
    {
        return DataObject.fromJson(Utils.getSiteContent("https://api.ksoft.si/images/rand-reddit/" + query, true));
    }
}