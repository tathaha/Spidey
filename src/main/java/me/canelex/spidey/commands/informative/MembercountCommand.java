package me.canelex.spidey.commands.informative;

import me.canelex.jda.api.entities.Member;
import me.canelex.jda.api.entities.Message;
import me.canelex.jda.api.entities.User;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "ConstantConditions"})
public class MembercountCommand implements ICommand
{
	@Override
	public final void action(final String[] args, final Message message)
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

	@Override
	public final String getDescription() { return "Shows you the membercount of the guild"; }
	@Override
	public final String getInvoke() { return "membercount"; }
	@Override
	public final Category getCategory() { return Category.INFORMATIVE; }
	@Override
	public final String getUsage() { return "s!membercount | s!members"; }
	@Override
	public final List<String> getAliases() { return Collections.singletonList("members"); }
}