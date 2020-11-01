package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

@SuppressWarnings("unused")
public class AvatarCommand extends Command
{
	public AvatarCommand()
	{
		super("avatar", new String[]{"pfp", "av"}, "Shows your/entered user's avatar", "avatar (User#Discriminator, @user, user id or username/nickname)", Category.INFORMATIVE, Permission.UNKNOWN, 1, 0);
	}

	@Override
	public void execute(final String[] args, final CommandContext ctx)
	{
		final var author = ctx.getAuthor();
		final var user = args.length == 0 ? author : Utils.getUserFromArgument(args[0], ctx.getTextChannel(), ctx.getMessage());
		if (user == null)
		{
			ctx.replyError("User not found");
			return;
		}
		final var avatarUrl = user.getEffectiveAvatarUrl();
		final var eb = Utils.createEmbedBuilder(author).setColor(0xFEFEFE);
		eb.setAuthor("Avatar of user " + MarkdownSanitizer.escape(user.getAsTag()));
		eb.setDescription("[Avatar link](" + avatarUrl + ")");
		eb.setImage(avatarUrl);
		ctx.reply(eb);
	}
}