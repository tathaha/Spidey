package me.canelex.spidey.commands.informative;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Member;
import me.canelex.jda.api.entities.Message;
import me.canelex.jda.api.entities.User;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;

import java.awt.*;
import java.time.Instant;

@SuppressWarnings({"unused", "ConstantConditions"})
public class MembercountCommand extends Command
{
	public MembercountCommand()
	{
		super("membercount", new String[]{"members"}, "Shows you the membercount of the guild", "membercount",
				Category.INFORMATIVE, Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var memberCache = message.getGuild().getMemberCache();
		final var bots = memberCache.applyStream(stream -> stream.map(Member::getUser).filter(User::isBot).count());
		final var total = memberCache.size();

		final var eb = Utils.createEmbedBuilder(message.getAuthor());
		eb.setAuthor("MEMBERCOUNT");
		eb.setColor(Color.WHITE);
		eb.setTimestamp(Instant.now());
		eb.addField("Total", "**" + total + "**", true);
		eb.addField("Humans", "**" + (total - bots) + "**", true);
		eb.addField("Bots", "**" + bots + "**", true);
		Utils.sendMessage(message.getChannel(), eb.build());
	}
}