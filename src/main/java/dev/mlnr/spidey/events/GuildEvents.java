package dev.mlnr.spidey.events;

import dev.mlnr.spidey.DatabaseManager;
import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

public class GuildEvents extends ListenerAdapter {
	private final DatabaseManager databaseManager;
	private final Cache cache;

	public GuildEvents(DatabaseManager databaseManager, Cache cache) {
		this.databaseManager = databaseManager;
		this.cache = cache;
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		var guild = event.getGuild();
		var guildId = guild.getIdLong();
		var defaultChannel = guild.getDefaultChannel();
		var jda = event.getJDA();
		if (defaultChannel != null) {
			Utils.sendMessage(defaultChannel, "Hey! I'm **Spidey**. Thanks for inviting me. To start, check `/info`.");
		}
		Utils.storeInvites(guild, cache.getGeneralCache());
		databaseManager.registerGuild(guildId);

		var memberCount = guild.getMemberCount();
		Utils.sendMessage(jda.getTextChannelById(785630223785787452L), "I've joined guild **" + guild.getName() + "** (**" + guildId + "**) with **" + memberCount + "** members");
		if (memberCount >= 10000)
			cache.getGuildSettingsCache().getMiscSettings(guildId).setSnipingEnabled(false);
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		var guild = event.getGuild();
		var guildId = guild.getIdLong();
		var jda = event.getJDA();
		var generalCache = cache.getGeneralCache();
		generalCache.getInviteCache().entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);
		cache.getMessageCache().pruneCache(guildId);
		cache.getMusicPlayerCache().destroyMusicPlayer(guild);
		generalCache.removeGuild(guildId);
		Utils.sendMessage(jda.getTextChannelById(785630223785787452L), "I've been kicked out of guild **" + guild.getName() + "** (**" + guildId + "**) with **" + guild.getMemberCount() + "** members");
	}

	@Override
	public void onGuildUpdateBoostTier(GuildUpdateBoostTierEvent event) {
		var guild = event.getGuild();
		var miscSettings = cache.getGuildSettingsCache().getMiscSettings(guild.getIdLong());
		var channel = miscSettings.getLogChannel();
		if (channel == null) {
			return;
		}
		var embedBuilder = new EmbedBuilder();
		var i18n = miscSettings.getI18n();

		embedBuilder.setAuthor(i18n.get("events.boost_tier_change.author"));
		embedBuilder.setColor(16023551);
		embedBuilder.setTimestamp(Instant.now());
		embedBuilder.addField(i18n.get("events.boost_tier_change.fields.tier"), "**" + event.getNewBoostTier().getKey() + "**", true);
		embedBuilder.addField(i18n.get("events.boost_tier_change.fields.boosts"), "**" + guild.getBoostCount() + "**", true);
		Utils.sendMessage(channel, embedBuilder.build());
	}
}