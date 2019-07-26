package me.canelex.spidey.commands;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

@SuppressWarnings("unused")
public class AvatarCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		final var eb = Utils.createEmbedBuilder(e.getAuthor()).setColor(Color.WHITE);
		final var musers = e.getMessage().getMentionedUsers();
		final var u = musers.isEmpty() ? e.getAuthor() : musers.get(0);

		eb.setAuthor("Avatar of user " + u.getAsTag());
		eb.setDescription(String.format("[Avatar link](%s)", u.getEffectiveAvatarUrl()));
		eb.setImage(u.getEffectiveAvatarUrl());

		Utils.sendMessage(e.getChannel(), eb.build());

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