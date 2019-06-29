package me.canelex.spidey.commands;

import me.canelex.spidey.MySQL;
import me.canelex.spidey.objects.category.Category;
import me.canelex.spidey.objects.command.ICommand;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class LeaveCommand implements ICommand {

	@Override
	public final void action(final GuildMessageReceivedEvent e) {

		if (e.getMember() != e.getGuild().getOwner()) {

			Utils.sendMessage(e.getChannel(), e.getAuthor().getAsMention() + ", you have to be the guild owner to do this.", false);

		}

		else {

			e.getChannel().sendMessage("Bye.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
			Utils.deleteMessage(e.getMessage());
			Utils.sendPrivateMessageFormat(Objects.requireNonNull(e.getGuild().getOwner()).getUser(), "I've left your server **%s**. If you'd want to invite me back, please use this URL: ||%s||. Thanks for using **Spidey**!", false, e.getGuild().getName(), Utils.getInviteUrl(e.getGuild().getIdLong()));
			MySQL.removeData(e.getGuild().getIdLong());
			e.getGuild().leave().queue();

		}

	}

	@Override
	public final String getDescription() { return "Spidey will leave your server"; }
	@Override
	public final boolean isAdmin() { return true; }
	@Override
	public final String getInvoke() { return "leave"; }
	@Override
	public final Category getCategory() { return Category.UTILITY; }
	@Override
	public final String getUsage() { return "s!leave"; }

}