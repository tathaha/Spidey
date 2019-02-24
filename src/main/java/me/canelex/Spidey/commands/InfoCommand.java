package me.canelex.Spidey.commands;

import java.awt.Color;

import me.canelex.Spidey.objects.command.Command;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class InfoCommand implements Command {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
		JDA jda = e.getJDA();
		User author = e.getAuthor();
		Guild guild = e.getGuild();
		TextChannel msgCh = e.getChannel();

    	User dev = jda.retrieveApplicationInfo().complete().getOwner();
		EmbedBuilder eb = API.createEmbedBuilder(author);
		eb.setAuthor("About bot", "https://canelex.ymastersk.net", jda.getSelfUser().getEffectiveAvatarUrl());
		eb.setColor(Color.WHITE);
		eb.addField("Developer", dev.getAsMention(), true);
		eb.addField("Release channel", "**STABLE**", true);
		eb.addField("I'm running on JDA version", JDAInfo.VERSION, true);
		eb.setThumbnail(guild.getIconUrl());		
		API.sendMessage(msgCh, eb.build());		
		
	}

	@Override
	public String help() {

		return "Shows you info about Spidey";
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}
	
}