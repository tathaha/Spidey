package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;

@SuppressWarnings("unused")
public class MembersCommand extends Command
{
    public MembersCommand()
    {
        super("members", new String[]{"membercount"}, "Shows you the membercount of the guild", "members", Category.INFORMATIVE, Permission.UNKNOWN, 0, 0);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        final var memberCache = ctx.getGuild().getMemberCache();
        final var bots = memberCache.applyStream(stream -> stream.map(Member::getUser).filter(User::isBot).count());
        final var total = memberCache.size();

        final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
        eb.setAuthor("MEMBERCOUNT");
        eb.setColor(0xFEFEFE);
        eb.setTimestamp(Instant.now());
        eb.addField("Total", "**" + total + "**", true);
        eb.addField("Humans", "**" + (total - bots) + "**", true);
        eb.addField("Bots", "**" + bots + "**", true);
        ctx.reply(eb);
    }
}