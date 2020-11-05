package dev.mlnr.spidey.commands.utility;

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
        super("vip", new String[]{}, "Adds/removes a guild to/from VIP guilds", "vip (guild id)", Category.UTILITY, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        if (ctx.getAuthor().getIdLong() != 394607709741252621L)
        {
            ctx.replyError("This command can only be executed by the Developer");
            return;
        }
        final var guildId = args.length == 0 ? ctx.getGuild().getIdLong() : Long.parseLong(args[0]);
        final var vip = !GuildSettingsCache.isVip(guildId);
        GuildSettingsCache.setVip(guildId, vip);
        ctx.reactLike();
        ctx.reply("VIP for guild " + guildId + " has been **" + (vip ? "added" : "removed") + "**.");
    }
}