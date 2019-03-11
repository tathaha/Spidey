package me.canelex.Spidey.commands;

import java.awt.Color;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SupportGuildsCommand implements ICommand {

	@Override
	public boolean called(GuildMessageReceivedEvent e) {

		return true;
		
	}

	@Override
	public void action(GuildMessageReceivedEvent e) {
		
		EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
		eb.setAuthor("Guilds of Spidey", "https://discord.gg/cnAgKrv", e.getJDA().getSelfUser().getEffectiveAvatarUrl());
		eb.addField("Spidey's Guild", "[Click to join](https://discord.gg/cnAgKrv)", true);
		eb.addField("Spidey's Test Builds Guild", "[Click to join](https://discord.gg/sR4ygqU)", true);
		eb.setColor(Color.BLACK);
		API.sendMessage(e.getChannel(), eb.build());
		
	}

	@Override
	public String help() {

		return "Shows you (support) guilds of Spidey";
		
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		
		return;
		
	}

}