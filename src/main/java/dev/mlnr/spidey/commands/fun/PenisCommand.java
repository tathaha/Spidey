package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.concurrent.ThreadLocalRandom;

import static dev.mlnr.spidey.utils.Utils.getColorHex;

@SuppressWarnings("unused")
public class PenisCommand extends Command
{
    public PenisCommand()
    {
        super("penis", new String[]{}, "Shows you what's your or entered user's penis", "penis (User#Discriminator, @user, user id or username/nickname)", Category.FUN, Permission.UNKNOWN, 1, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var random = ThreadLocalRandom.current().nextInt(0, 25 + 1); // values from 0 to 25, 25 + 1 'cause 25 has to be inclusive
        final var text = " penis:\n8" + "=".repeat(random) + "D (**" + random + "** cm)";
        final var author = ctx.getAuthor();
        final var user = args.length == 0 ? author : Utils.getUserFromArgument(args[0], ctx.getTextChannel(), ctx.getMessage());
        if (user == null)
        {
            ctx.replyError("User not found");
            return;
        }
        final var eb = Utils.createEmbedBuilder(author);
        eb.setAuthor("penis size machine");
        eb.setColor(getColorHex(random, 25));
        eb.setDescription((user == author ? "your" : user.getAsMention() + "'s") + text);
        ctx.reply(eb);
    }
}