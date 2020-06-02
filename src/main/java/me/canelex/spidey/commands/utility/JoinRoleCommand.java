package me.canelex.spidey.commands.utility;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
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
        super("joinrole", new String[]{}, "Sets/removes the role that is added to a member after joining",
                "joinrole (id/name of the role, if not given, the role will be reset if set)", Category.UTILITY, Permission.ADMINISTRATOR, 0, 3);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        final var guild = message.getGuild();
        final var guildId = guild.getIdLong();
        final var channel = message.getChannel();
        final var member = message.getMember();

        final var dbRole = MySQL.getRole(guildId);
        if (args.length == 0)
        {
            if (dbRole == 0)
                Utils.returnError("You don't have the join role set", message);
            else
            {
                MySQL.removeRole(guildId);
                Utils.sendMessage(channel, ":white_check_mark: The join role has been removed.");
            }
            return;
        }
        long roleId;
        Role role;
        if (ID_PATTERN.matcher(args[0]).matches())
        {
            final var parsed = Long.parseUnsignedLong(args[0]);
            if (dbRole == parsed)
            {
                Utils.returnError("The join role is already set to this role", message);
                return;
            }
            final var tmp = guild.getRoleById(parsed);
            if (tmp == null)
            {
                Utils.returnError("There is no such role with given ID", message);
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
                    Utils.returnError("The name of the role has to be 100 characters long at max", message);
                    return;
                }
                final var roles = guild.getRolesByName(args[0], false);
                if (roles.isEmpty())
                {
                    Utils.returnError("There is no such role with given name", message);
                    return;
                }
                final var fromName = roles.get(0);
                final var id = fromName.getIdLong();
                if (dbRole == id)
                {
                    Utils.returnError("The join role is already set to this role", message);
                    return;
                }
                role = fromName;
                roleId = id;
            }
            else
            {
                Utils.returnError("Please enter a valid ID/role name", message);
                return;
            }
        }
        if (!member.canInteract(role))
            Utils.returnError("You can't set the join role to a role which you can't interact with", message);
        else
        {
            MySQL.setRole(guildId, roleId);
            Utils.sendMessage(channel, ":white_check_mark: The join role has been set to role `" + role.getName() + "`.");
        }
    }
}