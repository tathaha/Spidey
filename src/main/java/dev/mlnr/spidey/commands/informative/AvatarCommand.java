package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

@SuppressWarnings("unused")
public class AvatarCommand extends Command
{
	public AvatarCommand()
	{
		super("avatar", new String[]{"pfp", "av"}, "Shows your/entered user's avatar", "avatar (User#Discriminator, @user, user id or username/nickname)", Category.INFORMATIVE, Permission.UNKNOWN, 1, 0);
	}

	@Override
	public final void execute(final String[] args, final Message msg)
	{
		final var channel = msg.getTextChannel();
		final var author = msg.getAuthor();
		final var user = args.length == 0 ? author : Utils.getUserFromArgument(args[0], channel, msg);
		if (user == null)
		{
			Utils.returnError("User not found", msg);
			return;
		}
		final var avatarUrl = user.getEffectiveAvatarUrl();
		final var eb = Utils.createEmbedBuilder(author).setColor(0xFEFEFE);
		eb.setAuthor("Avatar of user " + MarkdownSanitizer.escape(user.getAsTag()));
		eb.setDescription("[Avatar link](" + avatarUrl + ")");
		eb.setImage(avatarUrl);
		Utils.sendMessage(channel, eb.build());
	}
}