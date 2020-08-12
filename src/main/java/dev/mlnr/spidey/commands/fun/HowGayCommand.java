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
        super("howgay", new String[]{"gay"}, "Shows you what's your or mentioned user's gay rate", "howgay (User#Discriminator, @user, user id or username/nickname)", Category.FUN, Permission.UNKNOWN, 1, 0);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var prideFlag = "\uD83C\uDFF3\uFE0F\u200D\uD83C\uDF08";
        final var random = ThreadLocalRandom.current().nextInt(0, 100 + 1); // values from 0 to 100, 100 + 1 'cause 100 has to be inclusive
        final var text = " **" + random + "**% gay " + prideFlag;
        final var channel = msg.getTextChannel();
        final var author = msg.getAuthor();
        final var user = args.length == 0 ? author : Utils.getUserFromArgument(args[0], channel, msg);
        if (user == null)
        {
            Utils.returnError("User not found", msg);
            return;
        }
        final var eb = Utils.createEmbedBuilder(author);
        eb.setAuthor("gay rate");
        eb.setColor(getColorHex(random, 100));
        eb.setDescription((user == author ? "you are" : user.getAsMention() + " is") + text);
        Utils.sendMessage(channel, eb.build());
    }
}