package me.canelex.spidey.utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.awt.*;

public class NSFWHelper
{
    private NSFWHelper()
    {
        super();
    }

    public static MessageEmbed getImage(final String query, final User author)
    {
        final var eb = Utils.createEmbedBuilder(author);
        final var json = getImageUrl(query);
        eb.setColor(Color.PINK);
        eb.setAuthor(json.getString("title"), json.getString("source"));
        eb.setImage(json.getString("image_url"));
        eb.setDescription("A random picture from r/" + query);
        return eb.build();
    }

    private static DataObject getImageUrl(final String query)
    {
        return DataObject.fromJson(Utils.getSiteContent("https://api.ksoft.si/images/rand-reddit/" + query, true));
    }
}