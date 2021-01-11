package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Category;
import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
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
		super("user", new String[]{}, Category.INFORMATIVE, Permission.UNKNOWN, 1, 2);
	}

	@Override
	public void execute(final String[] args, final CommandContext ctx)
	{
		if (args.length == 0)
		{
			respond(ctx, ctx.getAuthor(), ctx.getMember());
			return;
		}
		ctx.getArgumentAsUser(0, user -> ctx.getGuild().retrieveMember(user).queue(member -> respond(ctx, user, member), failure -> respond(ctx, user, null)));
	}

	private void respond(final CommandContext ctx, final User user, final Member member)
	{
		final var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		final var i18n = ctx.getI18n();

		eb.setAuthor(i18n.get("commands.user.other.title") + " - " + user.getAsTag());
		eb.setThumbnail(user.getEffectiveAvatarUrl());
		eb.addField("ID", user.getId(), false);
		eb.addField(i18n.get("commands.user.other.created"), formatDate(user.getTimeCreated()), true);

		if (member == null)
		{
			ctx.reply(eb);
			return;
		}
		final var nick = member.getNickname();
		if (nick != null)
			eb.addField(i18n.get("commands.user.other.nickname"), nick, false);

		eb.addField(i18n.get("commands.user.other.joined"), formatDate(member.getTimeJoined()), false);

		final var boostingSince = member.getTimeBoosted();
		if (boostingSince != null)
			eb.addField(i18n.get("commands.user.other.boosting"), formatDate(boostingSince), false);

		final var roles = member.getRoles();
		if (!roles.isEmpty())
		{
			final var sb = new StringBuilder();
			roles.forEach(role -> sb.append(role.getName()).append(", "));
			eb.addField(i18n.get("commands.user.other.roles") + " [**" + roles.size() + "**]",
					sb.length() > 1024 ? i18n.get("limit_exceeded") : sb.toString(), false);
		}
		ctx.reply(eb);
	}
}