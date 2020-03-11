package me.canelex.spidey.commands;

import me.canelex.jda.api.OnlineStatus;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "ConstantConditions"})
public class MembercountCommand implements ICommand
{
	@Override
	public final void action(final String[] args, final Message message)
	{
		final var memberCache = message.getGuild().getMemberCache();
		final var bots = memberCache.applyStream(stream -> stream.filter(member -> member.getUser().isBot()).count());
		final var totalOnline = memberCache.applyStream(stream -> stream.filter(member -> member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB || member.getOnlineStatus() == OnlineStatus.IDLE || member.getOnlineStatus() == OnlineStatus.ONLINE).collect(Collectors.toList()));
		final var botsOnline = totalOnline.stream().filter(member -> member.getUser().isBot()).count();
		final var humansOnline = totalOnline.size() - botsOnline;
		final var desktopOnline = memberCache.applyStream(stream -> stream.filter(Utils::isDesktop).count());
		final var mobileOnline = memberCache.applyStream(stream -> stream.filter(Utils::isMobile).count());
		final var webOnline = memberCache.applyStream(stream -> stream.filter(Utils::isWeb).count());
		final var total = memberCache.size();

		final var eb = Utils.createEmbedBuilder(message.getAuthor());
		eb.setAuthor("MEMBERCOUNT");
		eb.setColor(Color.WHITE);
		eb.setTimestamp(Instant.now());
		eb.addField("Total", "**" + total + "**", true);
		eb.addField("Humans", "**" + (total - bots) + "**", true);
		eb.addField("Bots", "**" + bots + "**", true);
		eb.addField("Total online", "**" + totalOnline.size() + "**", true);
		eb.addField("Humans online", "**" + humansOnline + "**", true);
		eb.addField("Bots online", "**" + botsOnline + "**", true);
		eb.addField("Desktop users online", "**" + desktopOnline + "**", true);
		eb.addField("Mobile users online", "**" + mobileOnline + "**", true);
		eb.addField("Web users online", "**" + (webOnline - botsOnline) + "**", true);
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