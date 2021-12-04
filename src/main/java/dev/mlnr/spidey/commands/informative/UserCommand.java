package dev.mlnr.spidey.commands.informative;

import dev.mlnr.spidey.objects.command.Command;
import dev.mlnr.spidey.objects.command.CommandContext;
import dev.mlnr.spidey.objects.command.category.Category;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import static dev.mlnr.spidey.utils.StringUtils.formatDate;
import static dev.mlnr.spidey.utils.StringUtils.formatDateRelative;

@SuppressWarnings("unused")
public class UserCommand extends Command {
	public UserCommand() {
		super("user", "Shows info about you or entered user", Category.INFORMATIVE, Permission.UNKNOWN, 2,
				new OptionData(OptionType.USER, "user", "The user to get the info about"));
	}

	@Override
	public boolean execute(CommandContext ctx) {
		var user = ctx.getUserOption("user");
		var author = ctx.getUser();
		if (user == null || user.equals(author)) {
			respond(ctx, author, ctx.getMember());
		}
		else {
			var member = ctx.getMemberOption("user");
			respond(ctx, user, member);
		}
		return true;
	}

	private void respond(CommandContext ctx, User user, Member member) {
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var i18n = ctx.getI18n();

		var timeCreated = user.getTimeCreated();
		embedBuilder.setAuthor(i18n.get("commands.user.title") + " - " + user.getAsTag());
		embedBuilder.setThumbnail(user.getEffectiveAvatarUrl());
		embedBuilder.addField("ID", user.getId(), false);
		embedBuilder.addField(i18n.get("commands.user.created"), formatDate(timeCreated) + formatDateRelative(timeCreated), true);

		if (member == null) {
			ctx.reply(embedBuilder);
			return;
		}
		var nick = member.getNickname();
		if (nick != null) {
			embedBuilder.addField(i18n.get("commands.user.nickname"), nick, false);
		}

		var timeJoined = member.getTimeJoined();
		embedBuilder.addField(i18n.get("commands.user.joined"), formatDate(timeJoined) + formatDateRelative(timeJoined), false);

		var boostingSince = member.getTimeBoosted();
		if (boostingSince != null) {
			embedBuilder.addField(i18n.get("commands.user.boosting"), formatDate(boostingSince), false);
		}

		var roles = member.getRoles();
		if (!roles.isEmpty()) {
			var sb = new StringBuilder();
			var rc = 0;
			for (var role : roles) {
				++rc;
				sb.append(role.getName()).append(rc == roles.size() ? "" : ", ");
			}
			embedBuilder.addField(i18n.get("commands.user.roles") + " [**" + roles.size() + "**]",
					sb.length() > 1024 ? i18n.get("limit_exceeded") : sb.toString(), false);
		}
		ctx.reply(embedBuilder);
	}
}