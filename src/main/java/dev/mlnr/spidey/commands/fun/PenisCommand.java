package dev.mlnr.spidey.commands.fun;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.ThreadLocalRandom;

import static dev.mlnr.spidey.utils.Utils.getColorHex;

@SuppressWarnings("unused")
public class PenisCommand extends Command
{
    public PenisCommand()
    {
        super("penis", new String[]{}, Category.FUN, Permission.UNKNOWN, 1, 2);
    }

    @Override
    public void execute(String[] args, CommandContext ctx)
    {
        if (args.length == 0)
        {
            respond(ctx, ctx.getAuthor());
            return;
        }
        ctx.getArgumentAsUser(0, user -> respond(ctx, user));
    }

    private void respond(CommandContext ctx, User user)
    {
        var random = ThreadLocalRandom.current().nextInt(0, 25 + 1); // values from 0 to 25, 25 + 1 cos 25 has to be inclusive
        var text = ":\n8" + "=".repeat(random) + "D (**" + random + "** cm)";
        var author = ctx.getAuthor();
        var eb = Utils.createEmbedBuilder(author);
        var i18n = ctx.getI18n();
        eb.setAuthor(i18n.get("commands.penis.other.title"));
        eb.setColor(getColorHex(random, 25));
        eb.setDescription((user.equals(author)
                ? i18n.get("commands.penis.other.person.second")
                : i18n.get("commands.penis.other.person.third", user.getAsMention())) + text);
        ctx.reply(eb);
    }
}