package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.utils.Emojis;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

import static net.dv8tion.jda.api.utils.MarkdownSanitizer.escape;

public class BanEvents extends ListenerAdapter {
	private final Cache cache;

	public BanEvents(Cache cache) {
		this.cache = cache;
	}

	@Override
	public void onGuildBan(GuildBanEvent event) {
		var guild = event.getGuild();
		var channel = cache.getGuildSettingsCache().getMiscSettings(guild.getIdLong()).getLogChannel();
		if (channel == null) {
			return;
		}
		var user = event.getUser();
		var escapedTag = escape(user.getAsTag());
		var eb = new EmbedBuilder();
		eb.setDescription("\uD83D\uDD28 **" + escapedTag + "** has been `banned`");
		eb.setColor(14495300);
		eb.setFooter("User ban", user.getEffectiveAvatarUrl());
		eb.setTimestamp(Instant.now());
		if (!guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
			eb.appendDescription(".");
			Utils.sendMessage(channel, eb.build());
			return;
		}
		guild.retrieveAuditLogs().type(ActionType.BAN).limit(1).queue(bans -> {
			if (bans.isEmpty()) {
				return;
			}
			var last = bans.get(0);
			var bannerTag = escape(last.getUser().getAsTag());
			var reason = last.getReason();
			reason = reason == null || reason.isEmpty() ? "unknown reason" : reason.trim();
			eb.appendDescription(" by **" + bannerTag + "** for **" + reason + "**.");
			Utils.sendMessage(channel, eb.build());
		});
	}

	@Override
	public void onGuildUnban(GuildUnbanEvent event) {
		var guild = event.getGuild();
		var channel = cache.getGuildSettingsCache().getMiscSettings(guild.getIdLong()).getLogChannel();
		if (channel == null) {
			return;
		}
		var user = event.getUser();
		var escapedTag = escape(user.getAsTag());
		var eb = new EmbedBuilder();
		eb.setDescription(Emojis.CHECK + " **" + escapedTag + "** has been `unbanned`");
		eb.setColor(7844437);
		eb.setFooter("User unban", user.getEffectiveAvatarUrl());
		eb.setTimestamp(Instant.now());
		if (!guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
			eb.appendDescription(".");
			Utils.sendMessage(channel, eb.build());
			return;
		}
		guild.retrieveAuditLogs().type(ActionType.UNBAN).limit(1).queue(unbans -> {
			if (unbans.isEmpty()) {
				return;
			}
			var last = unbans.get(0);
			var unbanner = last.getUser();
			if (unbanner == null) {
				eb.appendDescription(".");
			}
			else {
				eb.appendDescription(" by **" + unbanner.getAsTag() + "**.");
			}
			Utils.sendMessage(channel, eb.build());
		});
	}
}