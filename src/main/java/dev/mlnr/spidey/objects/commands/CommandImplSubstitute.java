package dev.mlnr.spidey.objects.commands;

import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import static dev.mlnr.spidey.utils.StringUtils.formatDate;
import static dev.mlnr.spidey.utils.StringUtils.formatDateRelative;

public class CommandImplSubstitute {
	private CommandImplSubstitute() {}

	public static void avatar(User target, String url, CommandContextBase ctx) {
		var embedBuilder = Utils.createEmbedBuilder(ctx.getUser());
		var avatarUrl = url + "?size=2048";
		embedBuilder.setAuthor(ctx.getI18n().get("commands.avatar.title") + " " + MarkdownSanitizer.escape(target.getAsTag()));
		embedBuilder.setDescription("[Avatar link](" + avatarUrl + ")");
		embedBuilder.setImage(avatarUrl);
		ctx.reply(embedBuilder);
	}

	public static void user(User user, Member member, CommandContextBase ctx) {
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