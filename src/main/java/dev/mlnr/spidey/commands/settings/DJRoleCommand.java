package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class DJRoleCommand extends Command
{
    public DJRoleCommand()
    {
        super("djrole", new String[]{}, "Sets/removes the DJ role", "djrole (@role, role id or name of the role or blank to reset)", Category.SETTINGS, Permission.MANAGE_SERVER, 1, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guildId = ctx.getGuild().getIdLong();
        final var dbRole = GuildSettingsCache.getDJRoleId(guildId);
        if (args.length == 0)
        {
            if (dbRole == 0)
            {
                ctx.replyError("You don't have the DJ role set");
                return;
            }
            GuildSettingsCache.removeDJRole(guildId);
            ctx.reply(":white_check_mark: The DJ role has been removed.");
            return;
        }
        ctx.getArgumentAsRole(0, role ->
        {
            final var roleId = role.getIdLong();
            if (roleId == dbRole)
            {
                GuildSettingsCache.removeDJRole(guildId);
                ctx.reply(":white_check_mark: The DJ role has been reset!");
                return;
            }
            if (!ctx.getMember().canInteract(role))
            {
                ctx.replyError("You cannot set the DJ role to a role which you cannot interact with");
                return;
            }
            GuildSettingsCache.setDJRoleId(guildId, roleId);
            ctx.reply(":white_check_mark: The DJ role has been set to role `" + role.getName() + "`.");
        });
    }
}