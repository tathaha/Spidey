package me.canelex.Spidey.commands;

import java.awt.Color;

import me.canelex.Spidey.MySQL;
import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WarnCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		final String neededPerm = "BAN_MEMBERS";
		final long l = MySQL.getChannelId(e.getGuild().getIdLong());
		
		if (!e.getMessage().getContentRaw().equals("s!warn")) {
			
			if (API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
				
				if (e.getGuild().getTextChannelById(l) != null) {

					TextChannel log = e.getGuild().getTextChannelById(l);
					final String reason = e.getMessage().getContentRaw().substring(7, e.getMessage().getContentRaw().lastIndexOf(" "));

					for (final User u : e.getMessage().getMentionedUsers()) {

						API.sendPrivateMessageFormat(u, ":exclamation: You have been warned on guild **%s** from **%s** for **%s**.", false, e.getGuild().getName(), e.getAuthor().getName(), e.getAuthor().getName());

						API.deleteMessage(e.getMessage());
						final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
						eb.setAuthor("NEW WARN");
						eb.setColor(Color.ORANGE);
						eb.addField("User", u.getAsMention(), true);
						eb.addField("Moderator", e.getAuthor().getAsMention(), true);
						eb.addField("Reason", "**" + reason + "**", true);
						API.sendMessage(log, eb.build());

					}

				}
				
			}
			
			else {
				
				API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);    				
				
			}
			
		}		
		
	}

	@Override
	public final String help() {

		return "Warns user";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}

}