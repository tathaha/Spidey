package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.utils.UserUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import static dev.mlnr.spidey.utils.Utils.formatDate;

@SuppressWarnings("unused")
public class UserCommand extends Command
{
	public UserCommand()
	{
		super("user", new String[]{}, "Shows info about you or entered user", "user (User#Discriminator, @user, user id or username/nickname)", Category.INFORMATIVE, Permission.UNKNOWN, 1, 2);
	}

	@Override
	public void execute(final String[] args, final CommandContext ctx)
	{
		if (args.length == 0)
		{
			respond(ctx, ctx.getAuthor(), ctx.getMember());
			return;
		}
		UserUtils.retrieveUser(args[0], ctx, retrievedUser ->
				ctx.getGuild().retrieveMember(retrievedUser).queue(member -> respond(ctx, retrievedUser, member), failure -> respond(ctx, retrievedUser, null)));
	}

	private void respond(final CommandContext ctx, final User user, final Member member)
	{
		final var eb = Utils.createEmbedBuilder(ctx.getAuthor());

		eb.setAuthor("USER INFO - " + user.getAsTag());
		eb.setColor(0xFEFEFE);
		eb.setThumbnail(user.getEffectiveAvatarUrl());
		eb.addField("ID", user.getId(), false);
		eb.addField("Account created", formatDate(user.getTimeCreated()), true);

		if (member == null)
		{
			ctx.reply(eb);
			return;
		}
		final var nick = member.getNickname();
		if (nick != null)
			eb.addField("Nickname for this guild", nick, false);

		eb.addField("User joined", formatDate(member.getTimeJoined()), false);

		final var boostingSince = member.getTimeBoosted();
		if (boostingSince != null)
			eb.addField("Boosting since", formatDate(boostingSince), false);

		final var roles = member.getRoles();
		if (!roles.isEmpty())
		{
			final var sb = new StringBuilder();
			var rc = 0;
			for (final var role : roles)
			{
				++rc;
				sb.append(role.getName()).append(rc == roles.size() ? "" : ", ");
			}
			eb.addField("Roles [**" + rc + "**]", sb.length() > 1024 ? "Limit exceeded" : sb.toString(), false);
		}
		ctx.reply(eb);
	}
}