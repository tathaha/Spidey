package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.ThreadLocalRandom;

import static dev.mlnr.spidey.utils.Utils.getColorHex;

@SuppressWarnings("unused")
public class HowGayCommand extends Command
{
    public HowGayCommand()
    {
        super("howgay", new String[]{"gay"}, "Shows you what's your or mentioned user's gay rate", "howgay (@someone)", Category.FUN, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var prideFlag = "\uD83C\uDFF3\uFE0F\u200D\uD83C\uDF08";
        final var random = ThreadLocalRandom.current().nextInt(0, 100 + 1); // values from 0 to 100, 100 + 1 'cause 100 has to be inclusive
        final var eb = Utils.createEmbedBuilder(msg.getAuthor());
        final var text = " **" + random + "**% gay " + prideFlag;
        eb.setAuthor("gay rate");
        eb.setColor(getColorHex(random, 100));

        if (args.length == 0)
            eb.setDescription("you are" + text);
        else if (args.length == 1)
        {
            if (Message.MentionType.USER.getPattern().matcher(args[0]).matches())
                eb.setDescription(msg.getMentionedUsers().get(0).getAsMention() + " is" + text);
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