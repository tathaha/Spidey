package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.utils.StringUtils;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.time.Instant;

import static net.dv8tion.jda.api.utils.MarkdownSanitizer.escape;

public class MemberEvents extends ListenerAdapter {
	private final Cache cache;

	public MemberEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		var guild = event.getGuild();
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(guild.getIdLong());
		var channel = miscSettings.getLogChannel();
		if (channel == null) {
			return;
		}
		var joinRole = miscSettings.getJoinRole();
		var selfMember = guild.getSelfMember();
		var user = event.getUser();
		var userId = user.getIdLong();

		if (joinRole != null && selfMember.canInteract(joinRole)) {
			guild.addRoleToMember(userId, joinRole).queue();
		}

		var escapedTag = escape(user.getAsTag());
		var avatarUrl = user.getEffectiveAvatarUrl();
		var embedBuilder = new EmbedBuilder();
		var i18n = miscSettings.getI18n();

		if (user.isBot()) {
			embedBuilder.setFooter(i18n.get("events.member_join.bot.footer"), avatarUrl);
			embedBuilder.setColor(5614830);

			if (!selfMember.hasPermission(Permission.VIEW_AUDIT_LOGS)) {
				embedBuilder.setDescription(i18n.get("events.member_join.bot.user.without", escapedTag, userId));
				Utils.sendMessage(channel, embedBuilder.build());
				return;
			}
			guild.retrieveAuditLogs().type(ActionType.BOT_ADD).queue(botsAdded -> {
				var last = botsAdded.get(0);
				embedBuilder.setDescription(i18n.get("events.member_join.bot.user.with", escape(last.getUser().getAsTag()), escapedTag, userId));
				Utils.sendMessage(channel, embedBuilder.build());
			});
			return;
		}
		embedBuilder.setColor(7844437);
		embedBuilder.setTimestamp(Instant.now());
		embedBuilder.setFooter(i18n.get("events.member_join.user.footer"), avatarUrl);
		embedBuilder.setDescription(i18n.get("events.member_join.user.message.base", escapedTag, userId));

		var created = user.getTimeCreated();
		var timestamp = i18n.get("events.member_join.user.message.created", StringUtils.formatDate(created), TimeFormat.RELATIVE.format(created));
		if (!selfMember.hasPermission(Permission.MANAGE_SERVER)) {
			embedBuilder.appendDescription(".").appendDescription(timestamp);
			Utils.sendMessage(channel, embedBuilder.build());
			return;
		}
		guild.retrieveInvites().queue(invites -> {
			for (var invite : invites) {
				var inviteData = cache.getGeneralCache().getInviteCache().get(invite.getCode());
				if (inviteData == null || invite.getUses() == inviteData.getUses()) {
					continue;
				}
				inviteData.incrementUses();
				embedBuilder.appendDescription(i18n.get("events.member_join.user.message.invite", invite.getUrl(), escape(invite.getInviter().getAsTag())));
				embedBuilder.appendDescription(timestamp);
				Utils.sendMessage(channel, embedBuilder.build());
				return;
			}
			embedBuilder.appendDescription("."); // no invite was found, send the message either way
			embedBuilder.appendDescription(timestamp);
			Utils.sendMessage(channel, embedBuilder.build());
		});
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(event.getGuild().getIdLong());
		var channel = miscSettings.getLogChannel();
		if (channel == null) {
			return;
		}
		var user = event.getUser();
		var escapedTag = escape(user.getAsTag());
		var avatarUrl = user.getEffectiveAvatarUrl();
		var userId = user.getIdLong();
		var embedBuilder = new EmbedBuilder();
		var i18n = miscSettings.getI18n();

		embedBuilder.setColor(14495300);
		embedBuilder.setTimestamp(Instant.now());

		if (user.isBot()) {
			embedBuilder.setDescription(i18n.get("events.member_remove.bot.text", escapedTag, userId));
			embedBuilder.setFooter(i18n.get("events.member_remove.bot.footer"), avatarUrl);
			Utils.sendMessage(channel, embedBuilder.build());
			return;
		}
		embedBuilder.setDescription(i18n.get("events.member_remove.user.text", escapedTag, userId));
		embedBuilder.setFooter(i18n.get("events.member_remove.user.footer"), avatarUrl);
		Utils.sendMessage(channel, embedBuilder.build());
	}
}