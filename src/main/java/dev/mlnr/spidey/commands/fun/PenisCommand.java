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
        super("penis", new String[]{}, "Shows you what's your or entered user's penis", "penis (User#Discriminator, @user or username/nickname)", Category.FUN, Permission.UNKNOWN, 1, 0);
    }

    @Override
    public void execute(final String[] args, final Message msg)
    {
        final var random = ThreadLocalRandom.current().nextInt(0, 25 + 1); // values from 0 to 25, 25 + 1 'cause 25 has to be inclusive
        final var text = " penis:\n8" + "=".repeat(random) + "D (**" + random + "** cm)";
        final var channel = msg.getTextChannel();
        final var author = msg.getAuthor();
        final var user = args.length == 0 ? author : Utils.getUserFromArgument(args[0], channel, msg);
        if (user == null)
        {
            Utils.returnError("User not found", msg);
            return;
        }
        final var eb = Utils.createEmbedBuilder(author);
        eb.setAuthor("penis size machine");
        eb.setColor(getColorHex(random, 25));
        eb.setDescription((user == author ? "your" : user.getAsMention() + "'s") + text);
        Utils.sendMessage(msg.getTextChannel(), eb.build());
    }
}