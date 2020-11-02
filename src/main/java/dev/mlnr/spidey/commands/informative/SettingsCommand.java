package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.cache.JoinRoleCache;
import dev.mlnr.spidey.cache.LogChannelCache;
import dev.mlnr.spidey.cache.PrefixCache;
import dev.mlnr.spidey.cache.music.DJRoleCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import static java.lang.String.format;

@SuppressWarnings("unused")
public class SettingsCommand extends Command
{
    public SettingsCommand()
    {
        super("settings", new String[]{}, "Shows the current settings for this guild", "settings", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guildId = ctx.getGuild().getIdLong();
        final var jda = ctx.getJDA();
        final var prefix = PrefixCache.getPrefix(guildId);
        final var eb = Utils.createEmbedBuilder(ctx.getAuthor()).setColor(0xFEFEFE);
        eb.setAuthor("Current settings for this guild");

        final var set = " (set one with " + prefix + "%s)";
        final var pattern = "%s (%d)";

        final var logChannel = LogChannelCache.getLogAsChannel(guildId, jda);
        eb.addField("Log channel", logChannel == null ? "None" + format(set, "log") : format(pattern, logChannel.getName(), logChannel.getIdLong()), false);

        final var joinRole = JoinRoleCache.getJoinRole(guildId, jda);
        eb.addField("Join role", joinRole == null ? "None" + format(set, "joinrole") : format(pattern, joinRole.getName(), joinRole.getIdLong()), false);

        final var djRole = DJRoleCache.getDJRole(guildId, jda);
        eb.addField("DJ role", djRole == null ? "None" + format(set, "djrole") : format(pattern, djRole.getName(), djRole.getIdLong()), false);

        eb.addField("Prefix", prefix + (prefix.equals("s!") ? " (set a custom prefix with s!prefix)" : ""), false);

        ctx.reply(eb);
    }
}