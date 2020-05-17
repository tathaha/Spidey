package me.canelex.spidey.commands.utility;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class JoinRoleCommand extends Command
{
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

    public JoinRoleCommand()
    {
        super("joinrole", new String[]{}, "Sets/removes the Role that is added to a member after joining",
                "joinrole (id of the Role, if not given, the Role will be reset if set)", Category.UTILITY, Permission.ADMINISTRATOR, 0);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        final var guild = message.getGuild();
        final var guildId = guild.getIdLong();
        final var channel = message.getChannel();
        final var member = message.getMember();
        final var requiredPermission = getRequiredPermission();

        if (!Utils.hasPerm(member, requiredPermission))
        {
            Utils.getPermissionsError(requiredPermission, message);
            return;
        }

        final var dbRole = MySQL.getRole(guildId);
        if (args.length == 1)
        {
            if (dbRole == 0)
                Utils.returnError("You don't have the join Role set", message);
            else
            {
                MySQL.removeRole(guildId);
                Utils.sendMessage(channel, ":white_check_mark: The join Role has been removed.");
            }
            return;
        }
        if (!ID_PATTERN.matcher(args[1]).matches())
        {
            Utils.returnError("Please enter a valid ID", message);
            return;
        }

        final var roleId = Long.parseUnsignedLong(args[1]);
        if (dbRole == roleId)
        {
            Utils.returnError("The join Role is already set to this Role", message);
            return;
        }

        final var role = guild.getRoleById(roleId);
        if (role == null)
        {
            Utils.returnError("There is no such Role in this guild", message);
            return;
        }
        if (!member.canInteract(role))
            Utils.returnError("You can't set the join Role to a Role which you can't interact with", message);
        else
        {
            MySQL.setRole(guildId, roleId);
            Utils.sendMessage(channel, ":white_check_mark: The join Role has been set to Role `" + role.getName() + "`.");
        }
    }
}