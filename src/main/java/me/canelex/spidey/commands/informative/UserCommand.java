package me.canelex.spidey.commands.informative;

import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;

@SuppressWarnings("unused")
public class UserCommand extends Command
{
	public UserCommand()
	{
		super("user", new String[]{}, "Shows info about you or mentioned user", "user (@someone)", Category.INFORMATIVE,
				Permission.UNKNOWN, 0, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var author = message.getAuthor();
		final var eb = Utils.createEmbedBuilder(author);
		final var musers = message.getMentionedUsers();
		final var u = musers.isEmpty() ? author : musers.get(0);
		final var guild = message.getGuild();
		final var m = guild.getMember(u);
		final var nick = m.getNickname();
		
		eb.setAuthor("USER INFO - " + u.getAsTag());
		eb.setColor(Color.WHITE);
		eb.setThumbnail(u.getEffectiveAvatarUrl());
		eb.addField("ID", u.getId(), false);

		if (nick != null)
			eb.addField("Nickname for this guild", nick, false);

		eb.addField("Account created", Utils.getTime(u.getTimeCreated().toInstant().toEpochMilli()), true);
		eb.addField("User joined", Utils.getTime(m.getTimeJoined().toInstant().toEpochMilli()), false);

		if (guild.getBoosters().contains(m))
			eb.addField("Boosting since", Utils.getTime(m.getTimeBoosted().toInstant().toEpochMilli()), false);

		if (!m.getRoles().isEmpty())
		{
			var i = 0;
			final var s = new StringBuilder();

			for (final var role : m.getRoles())
			{
				i++;
				if (i == m.getRoles().size())
					s.append(role.getName());
				else
					s.append(role.getName()).append(", ");
			}
			eb.addField("Roles [**" + i + "**]", s.toString(), false);
		}
		Utils.sendMessage(message.getChannel(), eb.build());
	}
}