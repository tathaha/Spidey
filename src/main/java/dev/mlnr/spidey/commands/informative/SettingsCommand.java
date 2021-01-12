package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.cache.GuildSettingsCache;
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
        super("settings", new String[]{}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guildId = ctx.getGuild().getIdLong();
        final var prefix = GuildSettingsCache.getPrefix(guildId);
        final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
        final var i18n = ctx.getI18n();

        eb.setAuthor(i18n.get("commands.settings.other.title"));

        final var setTemplate = " (" + i18n.get("commands.settings.other.set") + " " + prefix + "%s)";
        final var none = i18n.get("commands.settings.other.none");

        final var logChannel = GuildSettingsCache.getLogChannel(guildId);
        eb.addField(i18n.get("commands.settings.other.log"),
                logChannel == null ? none + format(setTemplate, "log") : logChannel.getAsMention(), false);

        final var joinRole = GuildSettingsCache.getJoinRole(guildId);
        eb.addField(i18n.get("commands.settings.other.join"),
                joinRole == null ? none + format(setTemplate, "joinrole") : joinRole.getAsMention(), false);

        final var djRole = GuildSettingsCache.getDJRole(guildId);
        eb.addField(i18n.get("commands.settings.other.dj"),
                djRole == null ? none + format(setTemplate, "djrole") : djRole.getAsMention(), false);

        eb.addField("Prefix", prefix + (prefix.equals("s!") ? " (" + i18n.get("commands.settings.other.prefix") + ")" : ""), false);

        ctx.reply(eb);
    }
}