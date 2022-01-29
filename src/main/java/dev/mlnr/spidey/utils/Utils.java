package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.cache.GeneralCache;
import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.guild.InviteData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.*;
import java.util.regex.Pattern;

public class Utils {
	public static final Pattern TEXT_PATTERN = Pattern.compile("[a-zA-Z0-9-_]+");
	private static final Executor INVITES_EXECUTOR = Executors.newFixedThreadPool(5, new CountingThreadFactory(() -> "Spidey", "Invites"));
	public static final int SPIDEY_COLOR = 3288807;
	public static final long SPIDEY_ID = 772446532560486410L;

	private Utils() {}

	public static void sendMessage(TextChannel channel, String toSend) {
		if (channel.canTalk()) {
			channel.sendMessage(toSend).queue();
		}
	}

	public static void sendMessage(TextChannel channel, MessageEmbed embed) {
		if (channel.canTalk() && channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
			channel.sendMessageEmbeds(embed).queue();
		}
	}

	public static EmbedBuilder createEmbedBuilder(User user) {
		return new EmbedBuilder().setFooter("Command executed by " + user.getAsTag(), user.getEffectiveAvatarUrl()).setColor(0xFFFFFF);
	}

	public static void storeInvites(Guild guild, GeneralCache generalCache) {
		if (guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)) {
			INVITES_EXECUTOR.execute(() -> {
				var invites = guild.retrieveInvites().complete();
				invites.forEach(invite -> generalCache.getInviteCache().put(invite.getCode(), new InviteData(invite, guild)));
			});
		}
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