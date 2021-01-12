package dev.mlnr.spidey.commands.fun;

import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import dev.mlnr.spidey.cache.AkinatorCache;
import dev.mlnr.spidey.objects.akinator.AkinatorData;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

public class AkinatorCommand extends Command
{
    public AkinatorCommand()
    {
        super("akinator", new String[]{"aki"}, Category.FUN, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var i18n = ctx.getI18n();
        try
        {
            final var akinator = new AkiwrapperBuilder().build();
            AkinatorCache.cacheAkinator(ctx.getAuthor().getIdLong(), new AkinatorData(akinator));
            final var user = ctx.getAuthor();
            final var embedBuilder = Utils.createEmbedBuilder(user).setAuthor(i18n.get("commands.akinator.other.of", user.getAsTag())).setColor(Utils.SPIDEY_COLOR);
            embedBuilder.setDescription(i18n.get("commands.akinator.other.question", 1) + " " + akinator.getCurrentQuestion().getQuestion());
            ctx.reply(embedBuilder);
        }
        catch (final ServerNotFoundException ex)
        {
            ctx.replyError(i18n.get("commands.akinator.other.couldnt_create"));
        }
    }
}