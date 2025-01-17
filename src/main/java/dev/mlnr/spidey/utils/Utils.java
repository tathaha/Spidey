package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.objects.Emojis;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Utils {
	public static final Pattern TEXT_PATTERN = Pattern.compile("[a-zA-Z0-9-_]+");
	public static final int SPIDEY_COLOR = 3288807;
	public static final long SPIDEY_ID = 772446532560486410L;

	private Utils() {}

	public static void sendMessage(BaseGuildMessageChannel channel, String toSend) {
		if (channel.canTalk()) {
			channel.sendMessage(toSend).queue();
		}
	}

	public static void sendMessage(BaseGuildMessageChannel channel, MessageEmbed embed) {
		if (channel.canTalk() && channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
			channel.sendMessageEmbeds(embed).queue();
		}
	}

	public static EmbedBuilder createEmbedBuilder(User user) {
		return new EmbedBuilder().setFooter("Command executed by " + user.getAsTag(), user.getEffectiveAvatarUrl()).setColor(0xFFFFFF);
	}

	public static <K, V> ExpiringMap<K, V> createDefaultExpiringMap() {
		return ExpiringMap.builder()
				.expirationPolicy(ExpirationPolicy.ACCESSED)
				.expiration(2, TimeUnit.MINUTES)
				.build();
	}

	public static void replyErrorWithoutContext(SlashCommandInteractionEvent event, String content) {
		event.reply(Emojis.NO_ENTRY + " " + content).setEphemeral(true).queue();
	}
}