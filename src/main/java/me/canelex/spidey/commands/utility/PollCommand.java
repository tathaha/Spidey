package me.canelex.spidey.commands.utility;

import me.canelex.spidey.objects.cache.Cache;
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
		super("poll", new String[]{}, "Creates a new poll", "poll <question>", Category.UTILITY, Permission.ADMINISTRATOR, 0, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		if (args.length == 0)
		{
			Utils.returnError("Please enter a question", message);
			return;
		}
		final var guild = message.getGuild();
		final var log = Cache.getLogAsChannel(guild.getIdLong(), message.getJDA());
		final var author = message.getAuthor();
		final var channel = message.getChannel();

		if (log == null)
			return;
		final var question = args[0];
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
			eb.setFooter("Poll created by " + author.getAsTag(), author.getEffectiveAvatarUrl());
			Utils.sendMessage(log, eb.build());
		});
	}
}