package dev.mlnr.spidey.objects.settings.guild;

import dev.mlnr.spidey.DatabaseManager;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class GuildChannelsSettings implements IGuildSettings {
	private final long guildId;

	private final List<Long> whitelistedChannels;
	private final List<Long> blacklistedChannels;

	private final DatabaseManager databaseManager;

	public GuildChannelsSettings(long guildId, List<Long> whitelistedChannels, List<Long> blacklistedChannels, DatabaseManager databaseManager) {
		this.guildId = guildId;

		this.whitelistedChannels = whitelistedChannels;
		this.blacklistedChannels = blacklistedChannels;

		this.databaseManager = databaseManager;
	}

	// whitelisted channels

	public List<Long> getWhitelistedChannels() {
		return whitelistedChannels;
	}

	public boolean isChannelWhitelisted(TextChannel channel) {
		return whitelistedChannels.isEmpty() ? !isChannelBlacklisted(channel) : isChannelWhitelisted(channel, true);
	}

	public boolean isChannelWhitelisted(TextChannel channel, boolean hardCheck) {
		return whitelistedChannels.contains(channel.getIdLong());
	}

	public void addWhitelistedChannel(TextChannel channel) {
		var channelId = channel.getIdLong();
		whitelistedChannels.add(channelId);
		databaseManager.addWhitelistedChannel(guildId, channelId);
	}

	public void removeWhitelistedChannel(TextChannel channel) {
		var channelId = channel.getIdLong();
		whitelistedChannels.remove(channelId);
		databaseManager.removeWhitelistedChannel(guildId, channelId);
	}

	// blacklisted channels

	public List<Long> getBlacklistedChannels() {
		return blacklistedChannels;
	}

	public boolean isChannelBlacklisted(TextChannel textChannel) {
		return blacklistedChannels.contains(textChannel.getIdLong());
	}

	public void addBlacklistedChannel(TextChannel textChannel) {
		var channelId = textChannel.getIdLong();
		blacklistedChannels.add(channelId);
		databaseManager.addBlacklistedChannel(guildId, channelId);
	}

	public void removeBlacklistedChannel(TextChannel textChannel) {
		var channelId = textChannel.getIdLong();
		blacklistedChannels.remove(channelId);
		databaseManager.removeBlacklistedChannel(guildId, channelId);
	}
}
