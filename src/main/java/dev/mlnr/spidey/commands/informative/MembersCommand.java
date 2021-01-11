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
        super("members", new String[]{"membercount"}, Category.INFORMATIVE, Permission.UNKNOWN, 0, 2);
    }

    @Override
    public void execute(final String[] args, final CommandContext ctx)
    {
        ctx.getGuild().loadMembers().onSuccess(members ->
        {
            final var total = members.size();
            final var bots = members.stream().filter(member -> member.getUser().isBot()).count();
            final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
            final var i18n = ctx.getI18n();

            eb.setAuthor(i18n.get("commands.members.other.title"));
            eb.setTimestamp(Instant.now());
            eb.addField(i18n.get("commands.members.other.total"), "**" + total + "**", true);
            eb.addField(i18n.get("commands.members.other.people"), "**" + (total - bots) + "**", true);
            eb.addField(i18n.get("commands.members.other.bots"), "**" + bots + "**", true);
            ctx.reply(eb);
        });
    }
}