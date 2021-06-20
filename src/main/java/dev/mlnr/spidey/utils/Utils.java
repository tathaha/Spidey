package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.cache.GeneralCache;
import dev.mlnr.spidey.objects.command.ChoicesEnum;
import dev.mlnr.spidey.objects.guild.InviteData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
	public static final Pattern TEXT_PATTERN = Pattern.compile("[a-zA-Z0-9-_]+");
	public static final int SPIDEY_COLOR = 3288807;
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EE, d.LLL y | HH:mm:ss");

	private Utils() {}

	public static void sendMessage(TextChannel channel, String toSend) {
		if (channel.canTalk()) {
			channel.sendMessage(toSend).queue();
		}
	}

	public static void sendMessage(TextChannel channel, MessageEmbed embed) {
		if (channel.canTalk() && channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
			channel.sendMessage(embed).queue();
		}
	}

	public static void deleteMessage(Message msg) {
		var channel = msg.getTextChannel();
		if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
			msg.delete().queue();
		}
	}

	public static EmbedBuilder createEmbedBuilder(User user) {
		return new EmbedBuilder().setFooter("Command executed by " + user.getAsTag(), user.getEffectiveAvatarUrl()).setColor(0xFEFEFE);
	}

	public static void storeInvites(Guild guild, GeneralCache generalCache) {
		if (guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)) {
			guild.retrieveInvites().queue(invites -> invites.forEach(invite -> generalCache.getInviteCache().put(invite.getCode(), new InviteData(invite, guild))));
		}
	}

	public static String formatDate(OffsetDateTime date) {
		return DATE_FORMATTER.format(date);
	}

	public static <K, V> ExpiringMap<K, V> createDefaultExpiringMap() {
		return ExpiringMap.builder()
				.expirationPolicy(ExpirationPolicy.ACCESSED)
				.expiration(2, TimeUnit.MINUTES)
				.build();
	}

	public static <E extends Enum<E> & ChoicesEnum> List<Command.Choice> getChoicesFromEnum(Class<E> choiceEnum) {
		return Arrays.stream(choiceEnum.getEnumConstants())
				.map(choicesEnum -> new Command.Choice(choicesEnum.getFriendlyName(), choicesEnum.name())).collect(Collectors.toList());
	}
}