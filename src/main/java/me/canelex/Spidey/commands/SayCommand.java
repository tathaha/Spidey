package me.canelex.Spidey.commands;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SayCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		final String neededPerm = "BAN_MEMBERS";
		
		if (!API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
			
			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);
			
		}
		
		else {
			
			API.deleteMessage(e.getMessage());
    		String toSay = e.getMessage().getContentRaw().substring(6);
    		
    		if (e.getMessage().getMentionedChannels().isEmpty()) {
    			
    			API.sendMessage(e.getChannel(), toSay, false);
    			
    		}
    		
    		else {
    			
    			final TextChannel ch = e.getMessage().getMentionedChannels().get(0);
    			toSay = toSay.substring(0, toSay.lastIndexOf(" "));
    			API.sendMessage(ch, toSay, false);
    			
    		}    			
			
		}
		
	}

	@Override
	public final String help() {

		return "Spidey will say something for you (in specified channel)";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {

		return;
		
	}

}