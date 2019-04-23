package me.canelex.Spidey;

import java.awt.Color;
import java.util.List;
import me.canelex.Spidey.utils.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Events extends ListenerAdapter {

	@Override
	public final void onGuildMessageReceived(final GuildMessageReceivedEvent e) {

		if (e.getMessage().getContentRaw().startsWith("s!") && !e.getAuthor().isBot()){

			Core.handleCommand(Core.parser.parse(e.getMessage().getContentRaw(), e));

		}

	}

	@Override
	public final void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent e) {

		final Guild guild = e.getGuild();
		final Role muted = guild.getRolesByName("Muted", false).get(0);

		if (e.getRoles().contains(muted)) {

			if (guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong())) != null) {

				final TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
				final EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor("UNMUTE");
				eb.setColor(Color.GREEN);
				eb.setThumbnail(e.getUser().getEffectiveAvatarUrl());
				eb.addField("User", "**" + e.getUser().getAsTag() + "**", false);
				API.sendMessage(log, eb.build());

			}

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

				if (ban.getReason().equals("[Banned by Spidey#2370]")) {

					reason = "Unknown";

				}

				else {

					reason = ban.getReason().substring(24);

				}

			}

			else {

				if (ban.getReason() == null) {

					reason = "Unknown";

				}

				else {

					reason = ban.getReason();

				}

			}

			eb.setAuthor("NEW BAN");
			eb.setThumbnail(user.getEffectiveAvatarUrl());
			eb.setColor(Color.RED);
			eb.addField("User", "**" + user.getAsTag() + "**", true);
			eb.addField("ID", "**" + user.getId() + "**", true);
			eb.addField("Moderator", banner.getAsMention(), true);
			eb.addField("Reason", "**" + reason + "**", true);

			API.sendMessage(log, eb.build());

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
			API.sendMessage(log, eb.build());

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
			API.sendMessage(log, eb.build());

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
			API.sendMessage(log, eb.build());

		}

	}

	@Override
	public final void onGuildLeave(final GuildLeaveEvent e) {

		if (MySQL.getChannelId(e.getGuild().getIdLong()) != null) {

			MySQL.removeData(e.getGuild().getIdLong());

		}

	}

}