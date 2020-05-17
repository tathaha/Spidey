package me.canelex.spidey.commands.utility;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Emojis;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

@SuppressWarnings("unused")
public class PollCommand extends Command
{
	public PollCommand()
	{
		super("poll", new String[]{}, "Creates a new poll", "poll <question>", Category.UTILITY, Permission.ADMINISTRATOR, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var guild = message.getGuild();
		final var log = Utils.getLogChannel(guild.getIdLong());
		final var author = message.getAuthor();
		final var channel = message.getChannel();

		final var requiredPermission = getRequiredPermission();
		if (!Utils.hasPerm(message.getMember(), requiredPermission))
			Utils.getPermissionsError(requiredPermission, message);
		else
		{
			if (log != null)
			{
				final var question = message.getContentRaw().substring(7);
				Utils.deleteMessage(message);
				channel.sendMessage("Poll: **" + question + "**").queue(m ->
				{
					m.addReaction(Emojis.LIKE).queue();
					m.addReaction(Emojis.SHRUG).queue();
					m.addReaction(Emojis.DISLIKE).queue();
					final var eb = Utils.createEmbedBuilder(author);
					eb.setAuthor("NEW POLL");
					eb.setColor(Color.ORANGE);
					eb.addField("Question", "**" + question + "**", false);
					eb.setFooter("Poll created by " + author.getAsTag(), author.getAvatarUrl());
					Utils.sendMessage(log, eb.build());
				});
			}
		}
	}
}