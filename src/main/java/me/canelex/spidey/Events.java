package me.canelex.spidey;

import me.canelex.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class Events extends ListenerAdapter {

	@Override
	public final void onGuildMessageReceived(final GuildMessageReceivedEvent e) {

		final Guild guild = e.getGuild();

		if (e.getMessage().getContentRaw().startsWith("s!") && !e.getAuthor().isBot()){

			Core.handleCommand(Core.parser.parse(e.getMessage().getContentRaw(), e));

		}

		if (e.getMessage().getType() == MessageType.GUILD_MEMBER_BOOST && guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {
			Utils.deleteMessage(e.getMessage());
			final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor("NEW BOOST");
			eb.setColor(16023551);
			eb.setThumbnail(e.getAuthor().getEffectiveAvatarUrl());
			eb.addField("Booster", "**" + e.getAuthor().getAsTag() + "**", true);
			eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
			Utils.sendMessage(log, eb.build());
		}

	}

	@Override
	public final void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent e) {

		final Guild guild = e.getGuild();
		final Role muted = guild.getRolesByName("Muted", false).get(0);

		if (e.getRoles().contains(muted) && guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor("UNMUTE");
			eb.setColor(Color.GREEN);
			eb.setThumbnail(e.getUser().getEffectiveAvatarUrl());
			eb.addField("User", "**" + e.getUser().getAsTag() + "**", false);
			Utils.sendMessage(log, eb.build());

		}

	}

	@Override
	public final void onGuildBan(final GuildBanEvent e) {

		final User user = e.getUser();
		final Guild guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final Ban ban = guild.retrieveBan(user).complete();
			final List<AuditLogEntry> auditbans = guild.retrieveAuditLogs().type(ActionType.BAN).complete();
			final User banner = auditbans.get(0).getUser();
			final EmbedBuilder eb = new EmbedBuilder();
			String reason;

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

		final User user = e.getUser();
		final Guild guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final EmbedBuilder eb = new EmbedBuilder();
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

		final User user = e.getUser();
		final Guild guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final EmbedBuilder eb = new EmbedBuilder();
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

		final User user = e.getUser();
		final Guild guild = e.getGuild();

		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

			final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final EmbedBuilder eb = new EmbedBuilder();
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
		final Guild guild = e.getGuild();
		if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {
			final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			final EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor("GUILD BOOST TIER HAS CHANGED");
			eb.setColor(16023551);
			eb.addField("Boost tier", "**" + e.getNewBoostTier().getKey() + "**", true);
			eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
			Utils.sendMessage(log, eb.build());
		}
	}

}