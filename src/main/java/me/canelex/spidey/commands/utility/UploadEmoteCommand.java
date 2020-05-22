package me.canelex.spidey.commands.utility;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ListedEmote;
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
                "uploademote <link> (name)", Category.UTILITY, Permission.MANAGE_EMOTES, 0);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        final var channel = message.getChannel();
        final var guild = message.getGuild();
        final var requiredPermission = getRequiredPermission();

        if (!Utils.hasPerm(message.getMember(), requiredPermission))
        {
            Utils.getPermissionsError(requiredPermission, message);
            return;
        }

        if (args.length < 2)
            Utils.returnError("Please provide a URL to retrieve the emote from", message);

        final var image = new ByteArrayOutputStream();
        try
        {
            final var con = (HttpURLConnection) new URL(args[1]).openConnection();
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
            LOG.error("There was an error while parsing the URL. URL: {}", args[1], ex);
            Utils.returnError("Please provide a valid URL to retrieve the emote from", message);
            return;
        }
        catch (final IOException ex)
        {
            LOG.error("There was an error!", ex);
            Utils.returnError("Unfortunately, we could not create the emote due to an internal error", message);
            return;
        }

        final var maxEmotes = guild.getMaxEmotes();
        final var byteArray = image.toByteArray();
        final var animated = byteArray[0] == 'G' && byteArray[1] == 'I' && byteArray[2] == 'F' && byteArray[3] == '8' && byteArray[4] == '9' && byteArray[5] == 'a';
        final var used = guild.retrieveEmotes().complete().stream()
                                                          .collect(Collectors.partitioningBy(ListedEmote::isAnimated))
                                                          .get(animated).size();

        if (maxEmotes == used)
        {
            Utils.returnError("Guild has the maximum amount of emotes", message);
            return;
        }

        var name = "";
        if (args.length == 3)
            name = args[2];
        else
        {
            final var tmpIndex = args[1].lastIndexOf('/') + 1;
            try
            {
                name = args[1].substring(tmpIndex, args[1].lastIndexOf('.'));
            }
            catch (final IndexOutOfBoundsException ex)
            {
                name = args[1].substring(tmpIndex);
            }
        }
        if (!(name.length() > 1 && name.length() < 33))
        {
            Utils.returnError("The name of the emote has to be between 2 and 32 in length", message);
            return;
        }
        else if (!name.matches("[a-zA-Z0-9-_]+"))
        {
            Utils.returnError("The name of the emote has to be in a valid format", message);
            return;
        }
        if (!Utils.hasPerm(guild.getSelfMember(), requiredPermission))
            Utils.returnError("Spidey does not have the permission to upload emotes", message);
        else
        {
            guild.createEmote(name, Icon.from(byteArray)).queue(emote -> guild.retrieveEmotes().queue(emotes ->
            {
                final var left = maxEmotes - used - 1;
                Utils.sendMessage(channel, "Emote " + emote.getAsMention() + " has been successfully uploaded! Emote slots left: **" + (left == 0 ? "None" : left) + "**");
            }), failure -> Utils.returnError("Unfortunately, we could not create the emote due to an internal error: **" + failure.getMessage() + "**. Please report this message to the Developer", message));
        }
    }
}