package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.cache.settings.GuildSettingsCache;
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
        final var prefix = GuildSettingsCache.getPrefix(guildId);
        final var eb = Utils.createEmbedBuilder(ctx.getAuthor()).setColor(0xFEFEFE);
        eb.setAuthor("Current settings for this guild");

        final var setTemplate = " (set one with " + prefix + "%s)";
        final var pattern = "%s (%d)";

        final var logChannel = GuildSettingsCache.getLogChannel(guildId);
        eb.addField("Log channel", logChannel == null ? "None" + format(setTemplate, "log") : format(pattern, logChannel.getName(), logChannel.getIdLong()), false);

        final var joinRole = GuildSettingsCache.getJoinRole(guildId);
        eb.addField("Join role", joinRole == null ? "None" + format(setTemplate, "joinrole") : format(pattern, joinRole.getName(), joinRole.getIdLong()), false);

        final var djRole = GuildSettingsCache.getDJRole(guildId);
        eb.addField("DJ role", djRole == null ? "None" + format(setTemplate, "djrole") : format(pattern, djRole.getName(), djRole.getIdLong()), false);

        eb.addField("Prefix", prefix + (prefix.equals("s!") ? " (set a custom prefix with s!prefix)" : ""), false);

        ctx.reply(eb);
    }
}