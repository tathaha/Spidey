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

	public static void parseArgumentAsUser(String argument, CommandContext ctx, Consumer<User> consumer) {
		parseArgumentAsEntity(argument, ctx, ArgumentType.USER, o -> consumer.accept(((User) o)));
	}

	public static void parseArgumentAsRole(String argument, CommandContext ctx, Consumer<Role> consumer) {
		parseArgumentAsEntity(argument, ctx, ArgumentType.ROLE, o -> consumer.accept(((Role) o)));
	}

	public static void parseArgumentAsTextChannel(String argument, CommandContext ctx, Consumer<TextChannel> consumer) {
		parseArgumentAsEntity(argument, ctx, ArgumentType.TEXT_CHANNEL, o -> consumer.accept(((TextChannel) o)));
	}

	public static void parseArgumentAsVoiceChannel(String argument, CommandContext ctx, Consumer<VoiceChannel> consumer) {
		parseArgumentAsEntity(argument, ctx, ArgumentType.VOICE_CHANNEL, o -> consumer.accept(((VoiceChannel) o)));
	}

	private static void parseArgumentAsEntity(String argument, CommandContext ctx, ArgumentType argumentType, Consumer<Object> entityConsumer) {
		var i18n = ctx.getI18n();
		var typeLocalized = i18n.get("argument_parser.types." + argumentType.name().toLowerCase());
		var entityNotFound = typeLocalized + " " + i18n.get("argument_parser.not_found.text");
		var idMatcher = ID_REGEX.matcher(argument);
		var author = ctx.getAuthor();
		var guild = ctx.getGuild();
		var typeLocalizedLowercase = typeLocalized.toLowerCase();
		var givenNameNotFound = i18n.get("argument_parser.not_found.given_name", typeLocalizedLowercase);
		var embedBuilder = Utils.createEmbedBuilder(author);

		if (argumentType.getMentionRegex().matcher(argument).matches()) {
			var mentions = ctx.getMessage().getMentions(argumentType.getMentionType());
			if (mentions.isEmpty()) {
				ctx.replyError(entityNotFound);
				return;
			}
			entityConsumer.accept(mentions.get(0));
		}
		else if (idMatcher.find()) {
			var entityId = Long.parseLong(idMatcher.group());

			switch (argumentType) {
				case USER:
					var jda = ctx.getJDA();
					var selfUser = jda.getSelfUser();

					if (entityId == author.getIdLong()) {
						entityConsumer.accept(author);
					}
					else if (entityId == selfUser.getIdLong()) {
						entityConsumer.accept(selfUser);
					}
					else {
						jda.retrieveUserById(entityId).queue(entityConsumer, failure -> ctx.replyError(entityNotFound));
					}
					break;
				case ROLE:
					var role = guild.getRoleById(entityId);
					if (role == null) {
						ctx.replyError(entityNotFound);
						return;
					}
					entityConsumer.accept(role);
					break;
				case TEXT_CHANNEL:
				case VOICE_CHANNEL:
					var channel = argumentType == ArgumentType.TEXT_CHANNEL ? guild.getTextChannelById(entityId) : guild.getVoiceChannelById(entityId);
					if (channel == null) {
						ctx.replyError(entityNotFound);
						return;
					}
					entityConsumer.accept(channel);
					break;
			}
		}
		else if (argument.length() >= 2 && argument.length() <= 32) {
			switch (argumentType) {
				case USER:
					if (argument.equalsIgnoreCase(ctx.getMember().getEffectiveName())) {
						entityConsumer.accept(author);
						return;
					}
					guild.retrieveMembersByPrefix(argument, 10).onSuccess(members -> {
						if (members.isEmpty()) {
							ctx.replyError(givenNameNotFound);
						}
						else if (members.size() == 1) {
							entityConsumer.accept(members.get(0).getUser());
						}
						else {
							var users = members.stream().map(Member::getUser).collect(Collectors.toList());
							createEntitySelection(embedBuilder, users, ctx, typeLocalizedLowercase,
									user -> user.getAsMention() + " - **" + user.getAsTag() + "**",
									choice -> entityConsumer.accept(users.get(choice)));
						}
					});
					break;
				case ROLE:
					var roles = guild.getRolesByName(argument, true);
					if (roles.isEmpty()) {
						ctx.replyError(givenNameNotFound);
					}
					else if (roles.size() == 1) {
						entityConsumer.accept(roles.get(0));
					}
					else {
						createEntitySelection(embedBuilder, roles, ctx, typeLocalized,
								role -> role.getAsMention() + " - ID: " + role.getIdLong(),
								choice -> entityConsumer.accept(roles.get(choice)));
					}
					break;
				case TEXT_CHANNEL:
				case VOICE_CHANNEL:
					var channels = argumentType == ArgumentType.TEXT_CHANNEL
							? guild.getTextChannelsByName(argument, true)
							: guild.getVoiceChannelsByName(argument, true);
					if (channels.isEmpty()) {
						ctx.replyError(givenNameNotFound);
					}
					else if (channels.size() == 1) {
						entityConsumer.accept(channels.get(0));
					}
					else {
						createEntitySelection(embedBuilder, channels, ctx, typeLocalizedLowercase,
								channel -> channel.getAsMention() + " - ID: " + channel.getIdLong(),
								choice -> entityConsumer.accept(channels.get(choice)));
					}
					break;
			}
		}
		else {
			ctx.replyError(entityNotFound);
		}
	}

	private static <T> void createEntitySelection(EmbedBuilder selectionBuilder, List<T> entities, CommandContext ctx, String type,
	                                              Function<T, String> mapper, IntConsumer choiceConsumer) {
		StringUtils.createSelection(selectionBuilder, entities, ctx, type, mapper, ConcurrentUtils.getEventWaiter(), choiceConsumer);
	}

	public enum ArgumentType {
		USER(Message.MentionType.USER),
		ROLE(Message.MentionType.ROLE),
		TEXT_CHANNEL(Message.MentionType.CHANNEL),
		VOICE_CHANNEL(Message.MentionType.CHANNEL);

		private final Message.MentionType mentionType;

		ArgumentType(Message.MentionType mentionType) {
			this.mentionType = mentionType;
		}

		public Message.MentionType getMentionType() {
			return mentionType;
		}

		public Pattern getMentionRegex() {
			return mentionType.getPattern();
		}
	}
}