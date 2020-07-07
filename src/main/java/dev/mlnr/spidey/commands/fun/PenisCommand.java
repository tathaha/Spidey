package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.ThreadLocalRandom;

import static dev.mlnr.spidey.utils.Utils.getColorHex;

@SuppressWarnings("unused")
public class PenisCommand extends Command
{
    public PenisCommand()
    {
        super("penis", new String[]{}, "Shows you what's your or mentioned user's penis", "penis (@someone)", Category.FUN, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var random = ThreadLocalRandom.current().nextInt(0, 25 + 1); // values from 0 to 25, 25 + 1 'cause 25 has to be inclusive
        final var eb = Utils.createEmbedBuilder(msg.getAuthor());
        final var text = " penis:\n8" + "=".repeat(random) + "D (**" + random + "** cm)";
        eb.setAuthor("penis size machine");
        eb.setColor(getColorHex(random, 25));

        if (args.length == 0)
            eb.setDescription("your" + text);
        else if (args.length == 1)
        {
            if (Message.MentionType.USER.getPattern().matcher(args[0]).matches())
                eb.setDescription(msg.getMentionedUsers().get(0).getAsMention() + "'s" + text);
            else
            {
                Utils.returnError("Please mention a user", msg);
                return;
            }
        }
        else
        {
            Utils.returnError("Please mention a user", msg);
            return;
        }
        Utils.sendMessage(msg.getTextChannel(), eb.build());
    }
}