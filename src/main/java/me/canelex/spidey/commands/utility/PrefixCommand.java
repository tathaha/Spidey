package me.canelex.spidey.commands.utility;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;

@SuppressWarnings("unused")
public class PrefixCommand extends Command
{
    public PrefixCommand()
    {
        super("prefix", new String[]{}, "Sets/removes the prefix for this server",
                "prefix (new prefix, if not given, the prefix will be reset if set)", Category.UTILITY, Permission.ADMINISTRATOR, 0);
    }

    @Override
    public final void execute(final String[] args, final Message message)
    {
        final var guild = message.getGuild();
        final var guildId = guild.getIdLong();
        final var channel = message.getChannel();
        final var requiredPermission = getRequiredPermission();
        final var actualPrefix = Utils.getPrefix(guildId);

        if (!Utils.hasPerm(message.getMember(), requiredPermission))
        {
            Utils.getPermissionsError(requiredPermission, message);
            return;
        }
        if (args.length == 1)
        {
            if (actualPrefix.equals("s!"))
                Utils.returnError("The prefix for this server is already set to the default one", message);
            else
            {
                Utils.setPrefix(guildId, "s!");
                Utils.sendMessage(channel, ":white_check_mark: The prefix for this server has been reset to `s!`!");
            }
            return;
        }

        final var newPrefix = args[1];
        if (actualPrefix.equals(newPrefix))
        {
            Utils.returnError("The prefix for this server is already set to `" + actualPrefix + "`", message);
            return;
        }
        if (newPrefix.contains("`"))
        {
            Utils.returnError("The prefix can't contain **`** character", message);
            return;
        }
        if (newPrefix.length() > 10)
        {
            Utils.returnError("The prefix can't be longer than 10 characters", message);
            return;
        }

        Utils.setPrefix(guildId, newPrefix);
        Utils.sendMessage(channel, ":white_check_mark: The prefix has been successfully changed to `" + newPrefix + "`!");
    }
}