package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.cache.PrefixCache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class PrefixCommand extends Command
{
    public PrefixCommand()
    {
        super("prefix", new String[]{}, "Sets/removes the prefix for this server", "prefix (new prefix, if not given, the prefix will be reset if set)", Category.UTILITY, Permission.ADMINISTRATOR, 0, 4);
    }

    @Override
    public final void execute(final String[] args, final Message msg)
    {
        final var guild = msg.getGuild();
        final var guildId = guild.getIdLong();
        final var channel = msg.getTextChannel();
        final var actualPrefix = PrefixCache.retrievePrefix(guildId);

        if (args.length == 0)
        {
            if (actualPrefix.equals("s!"))
                Utils.returnError("The prefix for this server is already set to the default one", msg);
            else
            {
                PrefixCache.setPrefix(guildId, "s!");
                Utils.sendMessage(channel, ":white_check_mark: The prefix for this server has been reset to `s!`!");
            }
            return;
        }

        final var newPrefix = args[0];
        if (actualPrefix.equals(newPrefix))
        {
            Utils.returnError("The prefix for this server is already set to `" + actualPrefix + "`", msg);
            return;
        }
        if (newPrefix.length() > 10)
        {
            Utils.returnError("The prefix can't be longer than 10 characters", msg);
            return;
        }

        PrefixCache.setPrefix(guildId, newPrefix);
        Utils.sendMessage(channel, ":white_check_mark: The prefix has been successfully changed to `" + newPrefix + "`!");
    }
}