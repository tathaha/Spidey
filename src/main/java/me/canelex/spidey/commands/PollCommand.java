package me.canelex.spidey.commands;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.API;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class PollCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final String neededPerm = "BAN_MEMBERS";

		@SuppressWarnings("ConstantConditions") final TextChannel log = e.getGuild().getTextChannelById(MySQL.getChannelId(e.getGuild().getIdLong()));

		if (e.getMember() != null && !API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {

			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);

		}

		else {

			final String question = e.getMessage().getContentRaw().substring(7);
			API.deleteMessage(e.getMessage());
			e.getChannel().sendMessage("Poll: **" + question + "**").queue(m -> {

				m.addReaction(Emojis.LIKE).queue();
				m.addReaction(Emojis.SHRUG).queue();
				m.addReaction(Emojis.DISLIKE).queue();
				final EmbedBuilder eb = API.createEmbedBuilder(e.getAuthor());
				eb.setAuthor("NEW POLL");
				eb.setColor(Color.ORANGE);
				eb.addField("Question", "**" + question + "**", false);
				eb.setFooter("Poll created by " + e.getAuthor().getAsTag(), e.getAuthor().getEffectiveAvatarUrl());
				assert log != null;
				API.sendMessage(log, eb.build());

			});

		}

	}

	@Override
	public final String help() {

		return "Creates a new poll";

	}

	@Override
	public final boolean isAdmin() {
		return true;
	}

}