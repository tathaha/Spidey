package dev.mlnr.spidey.utils;

import dev.mlnr.spidey.cache.GeneralCache;
import dev.mlnr.spidey.objects.Emojis;
import dev.mlnr.spidey.objects.command.ChoicesEnum;
import dev.mlnr.spidey.objects.guild.InviteData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.commands.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
	public static final Pattern TEXT_PATTERN = Pattern.compile("[a-zA-Z0-9-_]+");
	public static final int SPIDEY_COLOR = 3288807;

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
			guild.retrieveInvites().queue(invites -> invites.forEach(invite -> generalCache.getInviteCache().put(invite.getCode(), new InviteData(invite, guild))));
		}
	}

	public static <K, V> ExpiringMap<K, V> createDefaultExpiringMap() {
		return ExpiringMap.builder()
				.expirationPolicy(ExpirationPolicy.ACCESSED)
				.expiration(2, TimeUnit.MINUTES)
				.build();
	}

	public static <E extends Enum<E> & ChoicesEnum> List<SlashCommand.Choice> getChoicesFromEnum(Class<E> choiceEnum) {
		return Arrays.stream(choiceEnum.getEnumConstants())
				.map(choicesEnum -> new SlashCommand.Choice(choicesEnum.getFriendlyName(), choicesEnum.name())).collect(Collectors.toList());
	}

	public static List<ActionRow> splitComponents(Component... components) {
		var split = ListUtils.partition(Arrays.asList(components), 5);
		return split.stream().map(ActionRow::of).collect(Collectors.toList());
	}

	public static void replyErrorWithoutContext(SlashCommandEvent event, String content) {
		event.reply(Emojis.NO_ENTRY + " " + content).setEphemeral(true).queue();
	}

	public static OptionData createConvenientOption(OptionType type, String name, String description, boolean required) {
		return new OptionData(type, name, description, required, false);
	}
}