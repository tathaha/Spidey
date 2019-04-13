package me.canelex.Spidey.commands;

import java.awt.Color;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class InfoCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		final JDA jda = e.getJDA();
		final User author = e.getAuthor();
		final TextChannel msgCh = e.getChannel();

		final User dev = jda.retrieveApplicationInfo().complete().getOwner();
		final EmbedBuilder eb = API.createEmbedBuilder(author);
		eb.setAuthor("About me", "https://canelex.ymastersk.net", jda.getSelfUser().getEffectiveAvatarUrl());
		eb.setColor(Color.WHITE);
		eb.addField("Developer", dev.getAsMention(), true);
		eb.addField("Release channel", "**STABLE**", true);
		eb.addField("I'm running on JDA version", "**" + JDAInfo.VERSION + "**", true);		
		API.sendMessage(msgCh, eb.build());		
		
	}

	@Override
	public final String help() {

		return "Shows you info about me";
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {}
	
}