package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UploadEmoteCommand extends Command
{
    public UploadEmoteCommand()
    {
        super("uploademote", new String[]{}, Category.UTILITY, Permission.MANAGE_EMOTES, 0, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guild = ctx.getGuild();
        final var i18n = ctx.getI18n();
        if (!guild.getSelfMember().hasPermission(getRequiredPermission()))
        {
            ctx.replyError(i18n.get("commands.uploademote.other.no_perms"));
            return;
        }
        if (args.length == 0)
        {
            ctx.replyError(i18n.get("commands.uploademote.other.provide_url"));
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
                final var index = args[0].lastIndexOf('.');
                final var tmp = args[0].substring(tmpIndex, index); // possible name, if it doesn't throw, check for the extension
                final var ext = args[0].substring(index + 1);
                if (Icon.IconType.fromExtension(ext) == Icon.IconType.UNKNOWN)
                {
                    ctx.replyError(i18n.get("commands.uploademote.other.provide_format"));
                    return;
                }
                name = tmp;
            }
            catch (final IndexOutOfBoundsException ex)
            {
                name = args[0].substring(tmpIndex);
            }
        }
        if (!(name.length() >= 2 && name.length() <= 32))
        {
            ctx.replyError(i18n.get("commands.uploademote.other.name_length"));
            return;
        }
        else if (!Utils.TEXT_PATTERN.matcher(name).matches())
        {
            ctx.replyError(i18n.get("commands.uploademote.other.valid_format"));
            return;
        }
        final var image = new ByteArrayOutputStream();
        try
        {
            final var con = (HttpURLConnection) new URL(args[0]).openConnection(); // TODO execute the request using Requester class
            con.setRequestProperty("User-Agent", "dev.mlnr.spidey");
            try (final var stream = con.getInputStream())
            {
                final var chunk = new byte[4096];
                var bytesRead = 0;
                while ((bytesRead = stream.read(chunk)) > 0)
                {
                    image.write(chunk, 0, bytesRead);
                }
            }
            finally
            {
                con.disconnect();
            }
        }
        catch (final MalformedURLException ex)
        {
            ctx.replyError(i18n.get("commands.uploademote.other.provide_url"));
            return;
        }
        catch (final IOException ex)
        {
            ctx.replyError(i18n.get("internal_error", "upload the emote", ex.getMessage()));
            return;
        }
        final var byteArray = image.toByteArray();
        if (byteArray.length > 256000)
        {
            ctx.replyError(i18n.get("commands.uploademote.other.size"));
            return;
        }
        final var maxEmotes = guild.getMaxEmotes();
        final var animated = byteArray[0] == 'G' && byteArray[1] == 'I' && byteArray[2] == 'F' && byteArray[3] == '8' && byteArray[4] == '9' && byteArray[5] == 'a';
        final var used = guild.getEmoteCache().applyStream(stream -> stream.filter(emote -> !emote.isManaged())
                                                                           .collect(Collectors.partitioningBy(Emote::isAnimated))
                                                                           .get(animated).size());
        if (maxEmotes == used)
        {
            ctx.replyError(i18n.get("commands.uploademote.other.maximum_size"));
            return;
        }
        guild.createEmote(name, Icon.from(byteArray)).queue(emote ->
        {
            final var left = maxEmotes - used - 1;
            ctx.reply(i18n.get("commands.uploademote.other.success", emote.getAsMention(),
                    animated ? "Animated emote" : "Emote", (left == 0 ? "None" : left)));
        }, failure -> ctx.replyError(i18n.get("internal_error", "upload the emote", failure.getMessage())));
    }
}