package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@SuppressWarnings("unused")
public class PingCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		Utils.sendMessage(e.getChannel(), "**Gateway**/**WebSocket**: **" + e.getJDA().getGatewayPing() + "**ms\n**REST**: **" + e.getJDA().getRestPing().complete() + "**ms", false);

	}

	@Override
	public final String help() { return "Shows you ping info"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String invoke() { return "ping"; }

}