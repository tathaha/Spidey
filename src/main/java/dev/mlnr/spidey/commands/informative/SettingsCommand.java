package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import static java.lang.String.format;

@SuppressWarnings("unused")
public class SettingsCommand extends Command
{
    public SettingsCommand()
    {
        super("settings", new String[]{}, "Shows the current settings for this guild", "settings", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var guildId = msg.getGuild().getIdLong();
        final var jda = msg.getJDA();
        final var prefix = Cache.retrievePrefix(guildId);
        final var eb = Utils.createEmbedBuilder(msg.getAuthor()).setColor(0xFEFEFE);
        eb.setAuthor("Current settings for this guild");

        final var set = " (set one with " + prefix + "%s)";
        final var pattern = "%s (%d)";

        final var logChannel = Cache.getLogAsChannel(guildId, jda);
        eb.addField("Log channel", logChannel == null ? "None" + format(set, "log") : format(pattern, logChannel.getName(), logChannel.getIdLong()), false);

        final var joinRole = Cache.getJoinRole(guildId, jda);
        eb.addField("Join role", joinRole == null ? "None" + format(set, "joinrole") : format(pattern, joinRole.getName(), joinRole.getIdLong()), false);

        eb.addField("Prefix", prefix + (prefix.equals("s!") ? " (set a custom prefix with s!prefix)" : ""), false);

        Utils.sendMessage(msg.getTextChannel(), eb.build());
    }
}