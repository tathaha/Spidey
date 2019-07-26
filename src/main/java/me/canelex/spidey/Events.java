package me.canelex.spidey;

import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

@SuppressWarnings("ConstantConditions")
public class Events extends ListenerAdapter {

	@Override
	public final void onGuildMessageReceived(final GuildMessageReceivedEvent e) {

		final var guild = e.getGuild();
		final var content = e.getMessage().getContentRaw();

		if (content.startsWith("s!") && !e.getAuthor().isBot()){
			Core.handleCommand(Core.parser.parse(e.getMessage().getContentRaw(), e));
		}

		if (e.getMessage().getType() == MessageType.GUILD_MEMBER_BOOST && guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {
			Utils.deleteMessage(e.getMessage());
			final var log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final var eb = new EmbedBuilder();
			eb.setAuthor("NEW BOOST");
			eb.setColor(16023551);
			eb.setThumbnail(e.getAuthor().getEffectiveAvatarUrl());
			eb.addField("Booster", "**" + e.getAuthor().getAsTag() + "**", true);
			eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
			Utils.sendMessage(log, eb.build());
		}

	}

	@Override
	public final void onGuildBan(final GuildBanEvent e) {

		final var user = e.getUser();
		final var guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final var log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final var ban = guild.retrieveBan(user).complete();
			final var auditbans = guild.retrieveAuditLogs().type(ActionType.BAN).complete();
			final var banner = auditbans.get(0).getUser();
			final var eb = new EmbedBuilder();
			var reason = "";
			if (banner != null && banner.equals(e.getJDA().getSelfUser())) {
				reason = (ban.getReason().equals("[Banned by Spidey#2370]") ?  "Unknown" : ban.getReason().substring(24));
			}
			else {
				reason = (ban.getReason() == null ? "Unknown" : ban.getReason());
			}
			eb.setAuthor("NEW BAN");
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.setColor(Color.RED);
			eb.addField("User", "**" + user.getAsTag() + "**", true);
			eb.addField("ID", "**" + user.getId() + "**", true);
			eb.addField("Moderator", banner == null ? "Unknown" : banner.getAsMention(), true);
			eb.addField("Reason", "**" + reason + "**", true);

			Utils.sendMessage(log, eb.build());

		}

	}

	@Override
	public final void onGuildUnban(final GuildUnbanEvent e) {

		final var user = e.getUser();
		final var guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final var log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final var eb = new EmbedBuilder();
			eb.setAuthor("UNBAN");
			eb.setColor(Color.GREEN);
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.addField("User", "**" + user.getAsTag() + "**", true);
			eb.addField("ID", "**" + user.getId() + "**", true);
			Utils.sendMessage(log, eb.build());

		}

	}

	@Override
	public final void onGuildMemberLeave(final GuildMemberLeaveEvent e) {

		final var user = e.getUser();
		final var guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final var log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final var eb = new EmbedBuilder();
			eb.setAuthor("USER HAS LEFT");
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.setColor(Color.RED);
			eb.addField("User", "**" + user.getAsTag() + "**", true);
			eb.addField("ID", "**" + user.getId() + "**", true);
			Utils.sendMessage(log, eb.build());

		}

	}

	@Override
	public final void onGuildMemberJoin(final GuildMemberJoinEvent e) {

		final var user = e.getUser();
		final var guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final var log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final var eb = new EmbedBuilder();
			eb.setAuthor("USER HAS JOINED");
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.setColor(Color.GREEN);
			eb.addField("User", "**" + user.getAsTag() + "**", true);
			eb.addField("ID", "**" + user.getId() + "**", true);
			Utils.sendMessage(log, eb.build());

		}

	}

	@Override
	public final void onGuildLeave(final GuildLeaveEvent e) {
		if (MySQL.getChannelId(e.getGuild().getIdLong()) != null) {
			MySQL.removeData(e.getGuild().getIdLong());
		}
	}

	@Override
	public final void onGuildUpdateBoostTier(final GuildUpdateBoostTierEvent e) {
		final var guild = e.getGuild();
		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {
			final var log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final var eb = new EmbedBuilder();
			eb.setAuthor("GUILD BOOST TIER HAS CHANGED");
			eb.setColor(16023551);
			eb.addField("Boost tier", "**" + e.getNewBoostTier().getKey() + "**", true);
			eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
			Utils.sendMessage(log, eb.build());
		}
	}

}