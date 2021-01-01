package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.time.Instant;

@SuppressWarnings("unused")
public class MembersCommand extends Command
{
    public MembersCommand()
    {
        super("members", new String[]{"membercount"}, "Shows you the membercount of the guild", "members", Category.INFORMATIVE, Permission.UNKNOWN, 0, 2);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        ctx.getGuild().loadMembers().onSuccess(members ->
        {
            final var total = members.size();
            final var bots = members.stream().filter(member -> member.getUser().isBot()).count();
            final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
            eb.setAuthor("MEMBERCOUNT");
            eb.setTimestamp(Instant.now());
            eb.addField("Total", "**" + total + "**", true);
            eb.addField("People", "**" + (total - bots) + "**", true);
            eb.addField("Bots", "**" + bots + "**", true);
            ctx.reply(eb);
        });
    }
}