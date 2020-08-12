package dev.mlnr.spidey.commands.utility;

import dev.mlnr.spidey.objects.cache.LogChannelCache;
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
	public final void execute(final String[] args, final Message msg)
	{
		if (args.length == 0)
		{
			Utils.returnError("Please enter a question", msg);
			return;
		}
		final var guild = msg.getGuild();
		final var log = LogChannelCache.getLogAsChannel(guild.getIdLong(), msg.getJDA());
		final var author = msg.getAuthor();
		final var channel = msg.getChannel();

		if (log == null)
			return;
		final var question = args[0];
		Utils.deleteMessage(msg);
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