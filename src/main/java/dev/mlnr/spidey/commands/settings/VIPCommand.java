package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class VIPCommand extends Command
{
    public VIPCommand()
    {
        super("vip", new String[]{}, Category.SETTINGS, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var i18n = ctx.getI18n();
        if (ctx.getAuthor().getIdLong() != 394607709741252621L)
        {
            ctx.replyError(i18n.get("command_failures.only_dev"));
            return;
        }
        final var guildId = args.length == 0 ? ctx.getGuild().getIdLong() : Long.parseLong(args[0]);
        final var vip = !GuildSettingsCache.isVip(guildId);
        GuildSettingsCache.setVip(guildId, vip);
        ctx.reactLike();
        ctx.reply(i18n.get("commands.vip.other.done", guildId, vip ? "added" : "removed"));
    }
}