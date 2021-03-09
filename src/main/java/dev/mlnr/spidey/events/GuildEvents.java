package dev.mlnr.spidey.events;

import dev.mlnr.spidey.Spidey;
import dev.mlnr.spidey.cache.Cache;
import dev.mlnr.spidey.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

public class GuildEvents extends ListenerAdapter {
	private final Spidey spidey;
	private final Cache cache;

	public GuildEvents(Spidey spidey, Cache cache) {
		this.spidey = spidey;
		this.cache = cache;
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		var guild = event.getGuild();
		var guildId = guild.getIdLong();
		var defaultChannel = guild.getDefaultChannel();
		var jda = event.getJDA();
		if (defaultChannel != null) {
			Utils.sendMessage(defaultChannel, "Hey! I'm **Spidey**. Thanks for inviting me. To start, check `s!info`.");
		}
		Utils.storeInvites(guild, cache.getGeneralCache());
		spidey.getDatabaseManager().registerGuild(guildId);
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
		var channel = cache.getGuildSettingsCache().getMiscSettings(guild.getIdLong()).getLogChannel();
		if (channel == null) {
			return;
		}
		var eb = new EmbedBuilder();
		eb.setAuthor("GUILD BOOST TIER HAS CHANGED");
		eb.setColor(16023551);
		eb.setTimestamp(Instant.now());
		eb.addField("Boost tier", "**" + event.getNewBoostTier().getKey() + "**", true);
		eb.addField("Boosts", "**" + guild.getBoostCount() + "**", true);
		Utils.sendMessage(channel, eb.build());
	}
}