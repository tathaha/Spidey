package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class AvatarCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		if (e.getMessage().getMentionedUsers().isEmpty()) {

			final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
			eb.setAuthor("Avatar of user " + e.getAuthor().getAsTag());
			eb.setImage(e.getAuthor().getEffectiveAvatarUrl());
			eb.setColor(Color.WHITE);
			API.sendMessage(e.getChannel(), eb.build());

		}

		else {

			final User u = e.getMessage().getMentionedUsers().get(0);
			final EmbedBuilder eb = API.createEmbedBuilder(u);
			eb.setAuthor("Avatar of user " + u.getAsTag());
			eb.setImage(u.getEffectiveAvatarUrl());
			eb.setColor(Color.WHITE);
			API.sendMessage(e.getChannel(), eb.build());

		}

	}

	@Override
	public final String help() {

		return "Shows avatar of you or mentioned user";

	}

	@Override
	public final boolean isAdmin() {
		return false;
	}

}