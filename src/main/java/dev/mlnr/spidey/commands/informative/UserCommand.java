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
	public void execute(String[] args, CommandContext ctx)
	{
		if (args.length == 0)
		{
			respond(ctx, ctx.getAuthor(), ctx.getMember());
			return;
		}
		ctx.getArgumentAsUser(0, user -> ctx.getGuild().retrieveMember(user).queue(member -> respond(ctx, user, member), failure -> respond(ctx, user, null)));
	}

	private void respond(CommandContext ctx, User user, Member member)
	{
		var eb = Utils.createEmbedBuilder(ctx.getAuthor());
		var i18n = ctx.getI18n();

		eb.setAuthor(i18n.get("commands.user.other.title") + " - " + user.getAsTag());
		eb.setThumbnail(user.getEffectiveAvatarUrl());
		eb.addField("ID", user.getId(), false);
		eb.addField(i18n.get("commands.user.other.created"), formatDate(user.getTimeCreated()), true);

		if (member == null)
		{
			ctx.reply(eb);
			return;
		}
		var nick = member.getNickname();
		if (nick != null)
			eb.addField(i18n.get("commands.user.other.nickname"), nick, false);

		eb.addField(i18n.get("commands.user.other.joined"), formatDate(member.getTimeJoined()), false);

		var boostingSince = member.getTimeBoosted();
		if (boostingSince != null)
			eb.addField(i18n.get("commands.user.other.boosting"), formatDate(boostingSince), false);

		var roles = member.getRoles();
		if (!roles.isEmpty())
		{
			var sb = new StringBuilder();
			var rc = 0;
			for (var role : roles)
			{
				++rc;
				sb.append(role.getName()).append(rc == roles.size() ? "" : ", ");
			}
			eb.addField(i18n.get("commands.user.other.roles") + " [**" + roles.size() + "**]",
					sb.length() > 1024 ? i18n.get("limit_exceeded") : sb.toString(), false);
		}
		ctx.reply(eb);
	}
}