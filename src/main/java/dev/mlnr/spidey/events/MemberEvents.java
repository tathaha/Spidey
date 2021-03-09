package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
		var eb = new EmbedBuilder();
		eb.setTimestamp(Instant.now());
		if (user.isBot()) {
			eb.setFooter("Bot add", avatarUrl);
			eb.setColor(5614830);
			if (!selfMember.hasPermission(Permission.VIEW_AUDIT_LOGS)) {
				eb.setDescription("\uD83E\uDD16 Bot **" + escapedTag + "** (" + userId + ") has been `added` to this server.");
				Utils.sendMessage(channel, eb.build());
				return;
			}
			guild.retrieveAuditLogs().type(ActionType.BOT_ADD).queue(botsAdded -> {
				var last = botsAdded.get(0);
				eb.setDescription("\uD83E\uDD16 **" + escape(last.getUser().getAsTag()) + "** has `added` bot **" + escapedTag + "** (" + userId + ") to this server.");
				Utils.sendMessage(channel, eb.build());
			});
			return;
		}
		eb.setColor(7844437);
		eb.setFooter("User join", avatarUrl);
		eb.setDescription("\uD83D\uDCE5 **" + escapedTag + "** (" + userId + ") has `joined` the server");
		if (!selfMember.hasPermission(Permission.MANAGE_SERVER)) {
			eb.appendDescription(".");
			Utils.sendMessage(channel, eb.build());
			return;
		}
		guild.retrieveInvites().queue(invites -> {
			for (var invite : invites) {
				var inviteData = cache.getGeneralCache().getInviteCache().get(invite.getCode());
				if (inviteData == null || invite.getUses() == inviteData.getUses()) {
					continue;
				}
				inviteData.incrementUses();
				eb.appendDescription(" with invite **" + invite.getUrl() + "** (**" + escape(invite.getInviter().getAsTag()) + "**).");
				Utils.sendMessage(channel, eb.build());
				return;
			}
			eb.appendDescription("."); // no invite was found, send the message either way
			Utils.sendMessage(channel, eb.build());
		});
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		var channel = cache.getGuildSettingsCache().getMiscSettings(event.getGuild().getIdLong()).getLogChannel();
		if (channel == null) {
			return;
		}
		var user = event.getUser();
		var escapedTag = escape(user.getAsTag());
		var avatarUrl = user.getEffectiveAvatarUrl();
		var userId = user.getIdLong();
		var eb = new EmbedBuilder();
		eb.setColor(14495300);
		eb.setTimestamp(Instant.now());
		if (user.isBot()) {
			eb.setDescription("\uD83E\uDD16 Bot **" + escapedTag + "** (" + userId + ") has been `removed` from this server.");
			eb.setFooter("Bot remove", avatarUrl);
			Utils.sendMessage(channel, eb.build());
			return;
		}
		eb.setDescription("\uD83D\uDCE4 **" + escapedTag + "** (" + userId + ") has `left` the server.");
		eb.setFooter("User leave", avatarUrl);
		Utils.sendMessage(channel, eb.build());
	}
}