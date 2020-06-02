package me.canelex.spidey.commands.utility;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UploadEmoteCommand extends Command
{
    private static final Logger LOG = LoggerFactory.getLogger(UploadEmoteCommand.class);

    public UploadEmoteCommand()
    {
        super("uploademote", new String[]{}, "Uploads the image from the provided url as an emote if possible",
                "uploademote <link> (name)", Category.UTILITY, Permission.MANAGE_EMOTES, 0, 3);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        final var channel = message.getChannel();
        final var guild = message.getGuild();

        if (!Utils.hasPerm(guild.getSelfMember(), getRequiredPermission()))
        {
            Utils.returnError("Spidey does not have the permission to upload emotes", message);
            return;
        }
        if (args.length == 0)
        {
            Utils.returnError("Please provide a URL to retrieve the emote from", message);
            return;
        }

        final var image = new ByteArrayOutputStream();
        try
        {
            final var con = (HttpURLConnection) new URL(args[0]).openConnection();
            con.setRequestProperty("User-Agent", "me.canelex.spidey");

            try (final var stream = con.getInputStream())
            {
                final var chunk = new byte[4096];
                var bytesRead = 0;

                while ((bytesRead = stream.read(chunk)) > 0)
                {
                    image.write(chunk, 0, bytesRead);
                }
            }
            con.disconnect();
        }
        catch (final MalformedURLException ex)
        {
            LOG.error("There was an error while parsing the URL. URL: {}", args[0], ex);
            Utils.returnError("Please provide a valid URL to retrieve the emote from", message);
            return;
        }
        catch (final IOException ex)
        {
            LOG.error("There was an error!", ex);
            Utils.returnError("Unfortunately, we could not create the emote due to an internal error", message);
            return;
        }

        final var byteArray = image.toByteArray();
        if (byteArray.length > 256000)
        {
            Utils.returnError("The emote size has to be less than **256KB**", message);
            return;
        }
        final var maxEmotes = guild.getMaxEmotes();
        final var animated = byteArray[0] == 'G' && byteArray[1] == 'I' && byteArray[2] == 'F' && byteArray[3] == '8' && byteArray[4] == '9' && byteArray[5] == 'a';
        final var used = guild.getEmoteCache().applyStream(stream -> stream.filter(emote -> !emote.isManaged())
                                                                           .collect(Collectors.partitioningBy(Emote::isAnimated))
                                                                           .get(animated).size());

        if (maxEmotes == used)
        {
            Utils.returnError("Guild has the maximum amount of emotes", message);
            return;
        }

        var name = "";
        if (args.length == 2)
            name = args[1];
        else
        {
            final var tmpIndex = args[0].lastIndexOf('/') + 1;
            try
            {
                name = args[0].substring(tmpIndex, args[0].lastIndexOf('.'));
            }
            catch (final IndexOutOfBoundsException ex)
            {
                name = args[0].substring(tmpIndex);
            }
        }
        if (!(name.length() >= 2 && name.length() <= 32))
        {
            Utils.returnError("The name of the emote has to be between 2 and 32 in length", message);
            return;
        }
        else if (!Utils.TEXT_PATTERN.matcher(name).matches())
        {
            Utils.returnError("The name of the emote has to be in a valid format", message);
            return;
        }

        guild.createEmote(name, Icon.from(byteArray)).queue(emote ->
        {
            final var left = maxEmotes - used - 1;
            Utils.sendMessage(channel, "Emote " + emote.getAsMention() + " has been successfully uploaded! Emote slots left: **" + (left == 0 ? "None" : left) + "**");
        }, failure -> Utils.returnError("Unfortunately, we could not create the emote due to an internal error: **" + failure.getMessage() + "**. Please report this message to the Developer", message));
    }
}