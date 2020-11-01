package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.cache.JoinRoleCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class JoinRoleCommand extends Command
{
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    public JoinRoleCommand()
    {
        super("joinrole", new String[]{}, "Sets/removes the role that is added to a member after joining", "joinrole (id/name of the role or blank to reset)", Category.UTILITY, Permission.ADMINISTRATOR, 1, 4);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var guild = msg.getGuild();
        final var guildId = guild.getIdLong();
        final var channel = msg.getTextChannel();
        final var member = msg.getMember();

        final var dbRole = JoinRoleCache.retrieveJoinRole(guildId);
        if (args.length == 0)
        {
            if (dbRole == 0)
            {
                Utils.returnError("You don't have the join role set", msg);
                return;
            }
            JoinRoleCache.removeJoinRole(guildId);
            Utils.sendMessage(channel, ":white_check_mark: The join role has been removed.");
            return;
        }
        long roleId;
        Role role;
        if (ID_PATTERN.matcher(args[0]).matches())
        {
            final var parsed = Long.parseUnsignedLong(args[0]);
            if (dbRole == parsed)
            {
                Utils.returnError("The join role is already set to this role", msg);
                return;
            }
            final var tmp = guild.getRoleById(parsed);
            if (tmp == null)
            {
                Utils.returnError("There is no such role with given ID", msg);
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
                    Utils.returnError("The name of the role has to be 100 characters long at max", msg);
                    return;
                }
                final var roles = guild.getRolesByName(args[0], false);
                if (roles.isEmpty())
                {
                    Utils.returnError("There is no such role with given name", msg);
                    return;
                }
                final var fromName = roles.get(0);
                final var id = fromName.getIdLong();
                if (dbRole == id)
                {
                    Utils.returnError("The join role is already set to this role", msg);
                    return;
                }
                role = fromName;
                roleId = id;
            }
            else
            {
                Utils.returnError("Please enter a valid ID/role name", msg);
                return;
            }
        }
        if (!member.canInteract(role))
        {
            Utils.returnError("You can't set the join role to a role which you can't interact with", msg);
            return;
        }
        JoinRoleCache.setJoinRole(guildId, roleId);
        Utils.sendMessage(channel, ":white_check_mark: The join role has been set to role `" + role.getName() + "`.");
    }
}