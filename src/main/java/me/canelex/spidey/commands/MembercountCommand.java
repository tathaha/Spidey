package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class MembercountCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final MemberCacheView mcv = e.getGuild().getMemberCache();
		final List<Member> tonline = mcv.stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE || member.getOnlineStatus() == OnlineStatus.IDLE || member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB).collect(Collectors.toList());
		final long bonline = tonline.stream().filter(m -> m.getUser().isBot()).count();
		final long total = mcv.size();
		final long online = tonline.size();
		final long bots = mcv.stream().filter(member -> member.getUser().isBot()).count();
		final long ponline = online - bonline;
		final long monline = mcv.stream().filter(Utils::isMobile).count();
		final long wonline = (mcv.stream().filter(Utils::isWeb).count() - bonline);
		final long donline = mcv.stream().filter(Utils::isDesktop).count();

		final EmbedBuilder eb = Utils.createEmbedBuilder(e.getAuthor());
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