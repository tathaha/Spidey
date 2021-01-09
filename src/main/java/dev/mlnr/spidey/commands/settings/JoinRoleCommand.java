package dev.mlnr.spidey.commands.settings;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class JoinRoleCommand extends Command
{
    public JoinRoleCommand()
    {
        super("joinrole", new String[]{}, "Sets/removes the role that is added to a member after joining", "joinrole (@role, role id or name of the role or blank to reset)",
                Category.SETTINGS, Permission.MANAGE_SERVER, 1, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guildId = ctx.getGuild().getIdLong();
        final var dbRole = GuildSettingsCache.getJoinRoleId(guildId);
        if (args.length == 0)
        {
            if (dbRole == 0)
            {
                ctx.replyError("You don't have the join role set");
                return;
            }
            GuildSettingsCache.removeJoinRole(guildId);
            ctx.reply(":white_check_mark: The join role has been removed.");
            return;
        }
        ctx.getArgumentAsRole(0, role ->
        {
            final var roleId = role.getIdLong();
            if (roleId == dbRole)
            {
                GuildSettingsCache.removeJoinRole(guildId);
                ctx.reply(":white_check_mark: The join role has been reset!");
                return;
            }
            if (!ctx.getMember().canInteract(role))
            {
                ctx.replyError("You cannot set the join role to a role which you cannot interact with");
                return;
            }
            GuildSettingsCache.setJoinRoleId(guildId, roleId);
            ctx.reply(":white_check_mark: The join role has been set to role `" + role.getName() + "`.");
        });
    }
}