package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class MembercountCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final var mcv = e.getGuild().getMemberCache();
		final var tonline = mcv.stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE || member.getOnlineStatus() == OnlineStatus.IDLE || member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB).collect(Collectors.toList());
		final var bonline = tonline.stream().filter(m -> m.getUser().isBot()).count();
		final var total = mcv.size();
		final var online = tonline.size();
		final var bots = mcv.stream().filter(member -> member.getUser().isBot()).count();
		final var ponline = online - bonline;
		final var monline = mcv.stream().filter(Utils::isMobile).count();
		final var wonline = (mcv.stream().filter(Utils::isWeb).count() - bonline);
		final var donline = mcv.stream().filter(Utils::isDesktop).count();

		final var eb = Utils.createEmbedBuilder(e.getAuthor());
		eb.setAuthor("MEMBERCOUNT");
		eb.setColor(Color.WHITE);
		eb.addField("Total", "**" + total + "**", true);
		eb.addField("People", "**" + (total - bots) + "**", true);
		eb.addField("Bots", "**" + bots + "**", true);
		eb.addField("Total online", "**" + online + "**", true);
		eb.addField("People online", "**" + ponline + "**", true);
		eb.addField("Bots online", "**" + bonline + "**", true);
		eb.addField("Desktop users online", "**" + donline + "**", true);
		eb.addField("Mobile users online", "**" + monline + "**", true);
		eb.addField("Web users online", "**" + wonline + "**", true);
		Utils.sendMessage(e.getChannel(), eb.build());

	}

	@Override
	public final String getDescription() { return "Shows you membercount of guild"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String getInvoke() { return "membercount"; }
	@Override
	public final Category getCategory() { return Category.INFORMATIVE; }
	@Override
	public final String getUsage() { return "s!membercount"; }

}