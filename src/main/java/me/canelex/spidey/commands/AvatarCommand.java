package me.canelex.spidey.commands;

import me.canelex.spidey.objects.category.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

@SuppressWarnings("unused")
public class AvatarCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final EmbedBuilder eb = Utils.createEmbedBuilder(e.getAuthor());

		if (e.getMessage().getMentionedUsers().isEmpty()) {

			eb.setAuthor("Avatar of user " + e.getAuthor().getAsTag());
			eb.setDescription(String.format("[Avatar link](%s)", e.getAuthor().getEffectiveAvatarUrl()));
			eb.setImage(e.getAuthor().getEffectiveAvatarUrl());
			eb.setColor(Color.WHITE);
			Utils.sendMessage(e.getChannel(), eb.build());
			eb.clear();

		}

		else {

			final User u = e.getMessage().getMentionedUsers().get(0);
			eb.setAuthor("Avatar of user " + u.getAsTag());
			eb.setDescription(String.format("[Avatar link](%s)", u.getEffectiveAvatarUrl()));
			eb.setImage(u.getEffectiveAvatarUrl());
			eb.setColor(Color.WHITE);
			Utils.sendMessage(e.getChannel(), eb.build());
			eb.clear();

		}

	}

	@Override
	public final String getDescription() { return "Shows yours or mentioned user's avatar"; }
	@Override
	public final boolean isAdmin() { return false; }
	@Override
	public final String getInvoke() { return "avatar"; }
	@Override
	public final Category getCategory() { return Category.INFORMATIVE; }
	@Override
	public final String getUsage() { return "s!avatar (@someone)"; }

}