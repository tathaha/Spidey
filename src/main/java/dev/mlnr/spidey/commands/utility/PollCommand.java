package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.cache.Cache;
import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

import static dev.mlnr.spidey.utils.Utils.addReaction;

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
			addReaction(m, Emojis.LIKE);
			addReaction(m, Emojis.SHRUG);
			addReaction(m, Emojis.DISLIKE);
			final var eb = Utils.createEmbedBuilder(author);
			eb.setAuthor("NEW POLL");
			eb.setColor(Color.ORANGE);
			eb.addField("Question", "**" + question + "**", false);
			eb.setFooter("Poll created by " + author.getAsTag(), author.getEffectiveAvatarUrl());
			Utils.sendMessage(log, eb.build());
		});
	}
}