package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class DJRoleCommand extends Command
{
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    public DJRoleCommand()
    {
        super("djrole", new String[]{}, "Sets/removes the DJ role", "djrole (id/name of the role or blank to reset)", Category.UTILITY, Permission.MANAGE_SERVER, 1, 4);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var guild = ctx.getGuild();
        final var guildId = guild.getIdLong();
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
        long roleId;
        Role role;
        if (ID_PATTERN.matcher(args[0]).matches())
        {
            final var parsed = Long.parseUnsignedLong(args[0]);
            if (dbRole == parsed)
            {
                ctx.replyError("The DJ role is already set to this role");
                return;
            }
            final var tmp = guild.getRoleById(parsed);
            if (tmp == null)
            {
                ctx.replyError("There is no such role with given ID");
                return;
            }
            role = tmp;
            roleId = parsed;
        }
        else
        {
            if (Utils.TEXT_PATTERN.matcher(args[0]).matches())
            {
                if (args[0].length() > 100)
                {
                    ctx.replyError("The name of the role has to be 100 characters long at max");
                    return;
                }
                final var roles = guild.getRolesByName(args[0], false);
                if (roles.isEmpty())
                {
                    ctx.replyError("There is no such role with given name");
                    return;
                }
                final var fromName = roles.get(0);
                final var id = fromName.getIdLong();
                if (dbRole == id)
                {
                    ctx.replyError("The join role is already set to this role");
                    return;
                }
                role = fromName;
                roleId = id;
            }
            else
            {
                ctx.replyError("Please enter a valid ID/role name");
                return;
            }
        }
        GuildSettingsCache.setDJRoleId(guildId, roleId);
        ctx.reply(":white_check_mark: The DJ role has been set to role `" + role.getName() + "`.");
    }
}