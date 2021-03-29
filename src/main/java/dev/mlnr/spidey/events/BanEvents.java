package dev.mlnr.spidey.events;

import dev.mlnr.spidey.cache.Cache;
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
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(guild.getIdLong());
		var channel = miscSettings.getLogChannel();
		if (channel == null) {
			return;
		}
		var user = event.getUser();
		var eb = new EmbedBuilder();
		var i18n = miscSettings.getI18n();

		eb.setDescription(i18n.get("events.ban.reason.without", escape(user.getAsTag())));
		eb.setColor(14495300);
		eb.setFooter(i18n.get("events.ban.footer"), user.getEffectiveAvatarUrl());
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
			var ban = bans.get(0);
			var bannerTag = escape(ban.getUser().getAsTag());
			var reason = ban.getReason();
			reason = (reason == null || reason.isEmpty()) ? i18n.get("events.ban.reason.unknown") : reason.trim();
			eb.appendDescription(i18n.get("events.ban.reason.with", bannerTag, reason));
			Utils.sendMessage(channel, eb.build());
		});
	}

	@Override
	public void onGuildUnban(GuildUnbanEvent event) {
		var guild = event.getGuild();
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(guild.getIdLong());
		var channel = miscSettings.getLogChannel();
		if (channel == null) {
			return;
		}
		var user = event.getUser();
		var escapedTag = escape(user.getAsTag());
		var eb = new EmbedBuilder();
		var i18n = miscSettings.getI18n();

		eb.setDescription(i18n.get("events.unban.user.without", escapedTag));
		eb.setColor(7844437);
		eb.setFooter(i18n.get("events.unban.footer"), user.getEffectiveAvatarUrl());
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
			eb.appendDescription(i18n.get("events.unban.user.with", unbanner.getAsTag()));
			Utils.sendMessage(channel, eb.build());
		});
	}
}