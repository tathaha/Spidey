package me.canelex.spidey.commands.informative;

import me.canelex.jda.api.Permission;
import me.canelex.jda.api.entities.Message;
import me.canelex.spidey.objects.command.Category;
import me.canelex.spidey.objects.command.Command;
import me.canelex.spidey.utils.Utils;

import java.awt.*;

@SuppressWarnings("unused")
public class AvatarCommand extends Command
{
	public AvatarCommand()
	{
		super("avatar", new String[]{}, "Shows avatar of you or of the mentioned user", "avatar (@someone)", Category.INFORMATIVE,
				Permission.UNKNOWN, 0);
	}

	@Override
	public final void execute(final String[] args, final Message message)
	{
		final var author = message.getAuthor();
		final var eb = Utils.createEmbedBuilder(author).setColor(Color.WHITE);
		final var users = message.getMentionedUsers();
		final var u = users.isEmpty() ? author : users.get(0);
		final var avatarUrl = u.getAvatarUrl();

		eb.setAuthor("Avatar of user " + u.getAsTag());
		eb.setDescription(String.format("[Avatar link](%s)", avatarUrl));
		eb.setImage(avatarUrl);

		Utils.sendMessage(message.getChannel(), eb.build());
	}
}