package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ArgumentUtils {
	private static final Pattern ID_REGEX = Pattern.compile("(\\d{17,18})");

	private ArgumentUtils() {}

	public static void parseArgumentAsInt(String argument, CommandContext ctx, IntConsumer consumer) {
		try {
			consumer.accept(Integer.parseInt(argument));
		}
		catch (NumberFormatException ex) {
			ctx.replyErrorLocalized("number.invalid");
		}
	}

	public static void parseArgumentAsUnsignedInt(String argument, CommandContext ctx, IntConsumer consumer) {
		try {
			consumer.accept(Integer.parseUnsignedInt(argument));
		}
		catch (NumberFormatException ex) {
			ctx.replyErrorLocalized("number.invalid");
		}
	}

	// TODO somehow unify mentionable parsing (except user)
	public static void parseArgumentAsTextChannel(String argument, CommandContext ctx, Consumer<TextChannel> consumer) {
		var i18n = ctx.getI18n();
		var notFound = i18n.get("argument_parser.not_found.channel") + " " + i18n.get("argument_parser.not_found.text");
		var guild = ctx.getGuild();
		var embedBuilder = Utils.createEmbedBuilder(ctx.getAuthor());

		if (Message.MentionType.CHANNEL.getPattern().matcher(argument).matches()) {
			var mentionedChannels = ctx.getMessage().getMentionedChannels();
			if (mentionedChannels.isEmpty()) {
				ctx.replyError(notFound);
				return;
			}
			consumer.accept(mentionedChannels.get(0));
		}
		else if (ID_REGEX.matcher(argument).matches()) {
			var channelId = Long.parseLong(argument);
			var channel = guild.getTextChannelById(channelId);
			if (channel == null) {
				ctx.replyError(notFound);
				return;
			}
			consumer.accept(channel);
		}
		else if (argument.length() >= 2 && argument.length() <= 32) {
			var textChannels = guild.getTextChannelsByName(argument, true);
			if (textChannels.isEmpty()) {
				ctx.replyErrorLocalized("argument_parser.not_found.name", i18n.get("argument_parser.not_found.channel").toLowerCase());
				return;
			}
			if (textChannels.size() == 1) {
				consumer.accept(textChannels.get(0));
				return;
			}
			createMentionableSelection(embedBuilder, textChannels, ctx, "channel", channel -> channel.getAsMention() + " - ID: " + channel.getIdLong(),
					choice -> consumer.accept(textChannels.get(choice)));
		}
		else {
			ctx.reply(notFound);
		}
	}

	public static void parseArgumentAsRole(String argument, CommandContext ctx, Consumer<Role> consumer) {
		var i18n = ctx.getI18n();
		var notFound = i18n.get("argument_parser.not_found.role") + " " + i18n.get("argument_parser.not_found.text");
		var guild = ctx.getGuild();
		var embedBuilder = Utils.createEmbedBuilder(ctx.getAuthor());

		if (Message.MentionType.ROLE.getPattern().matcher(argument).matches()) {
			var mentionedRoles = ctx.getMessage().getMentionedRoles();
			if (mentionedRoles.isEmpty()) {
				ctx.replyError(notFound);
				return;
			}
			consumer.accept(mentionedRoles.get(0));
		}
		else if (ID_REGEX.matcher(argument).matches()) {
			var roleId = Long.parseLong(argument);
			var role = guild.getRoleById(roleId);
			if (role == null) {
				ctx.replyError(notFound);
				return;
			}
			consumer.accept(role);
		}
		else if (argument.length() >= 2 && argument.length() <= 32) {
			var roles = guild.getRolesByName(argument, true);
			if (roles.isEmpty()) {
				ctx.replyErrorLocalized("argument_parser.not_found.name", i18n.get("argument_parser.not_found.role").toLowerCase());
				return;
			}
			if (roles.size() == 1) {
				consumer.accept(roles.get(0));
				return;
			}
			createMentionableSelection(embedBuilder, roles, ctx, "role", role -> role.getAsMention() + " - ID: " + role.getIdLong(),
					choice -> consumer.accept(roles.get(choice)));
		}
		else {
			ctx.reply(notFound);
		}
	}

	public static void parseArgumentAsUser(String argument, CommandContext ctx, Consumer<User> consumer) {
		var i18n = ctx.getI18n();
		var message = ctx.getMessage();
		var notFound = i18n.get("argument_parser.not_found.user") + " " + i18n.get("argument_parser.not_found.text");
		var author = ctx.getAuthor();
		var embedBuilder = Utils.createEmbedBuilder(author);

		if (Message.MentionType.USER.getPattern().matcher(argument).matches()) {
			var mentionedUsers = message.getMentionedUsers();
			if (mentionedUsers.isEmpty()) {
				ctx.replyError(notFound);
				return;
			}
			consumer.accept(mentionedUsers.get(0));
		}
		else if (ID_REGEX.matcher(argument).matches()) {
			var userId = Long.parseLong(argument);
			if (userId == author.getIdLong()) {
				consumer.accept(author);
				return;
			}
			var jda = ctx.getJDA();
			var selfUser = jda.getSelfUser();
			if (userId == selfUser.getIdLong()) {
				consumer.accept(selfUser);
				return;
			}
			jda.retrieveUserById(userId).queue(consumer, failure -> ctx.replyError(notFound));
		}
		else if (argument.length() >= 2 && argument.length() <= 32) {
			if (argument.equalsIgnoreCase(ctx.getMember().getEffectiveName())) {
				consumer.accept(author);
				return;
			}
			message.getGuild().retrieveMembersByPrefix(argument, 10).onSuccess(members -> {
				if (members.isEmpty()) {
					ctx.replyError(notFound);
					return;
				}
				if (members.size() == 1) {
					consumer.accept(members.get(0).getUser());
					return;
				}
				createMentionableSelection(embedBuilder, members.stream().map(Member::getUser).collect(Collectors.toList()), ctx, "user",
						mentionable -> mentionable.getAsMention() + " - **" + ((User) mentionable).getAsTag() + "**", choice -> consumer.accept(members.get(choice).getUser()));
			});
		}
		else {
			ctx.reply(notFound);
		}
	}

	private static void createMentionableSelection(EmbedBuilder selectionBuilder, List<? extends IMentionable> mentionables, CommandContext ctx, String type,
	                                               Function<IMentionable, String> mapper, IntConsumer choiceConsumer) {
		StringUtils.createSelection(selectionBuilder, mentionables, ctx, type, mapper::apply, ConcurrentUtils.getEventWaiter(), choiceConsumer);
	}
}