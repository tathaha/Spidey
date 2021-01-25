package dev.mlnr.spidey.objects.akinator;

import dev.mlnr.spidey.cache.AkinatorCache;
import dev.mlnr.spidey.cache.GuildSettingsCache;
import dev.mlnr.spidey.objects.I18n;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AkinatorContext {
	private final GuildMessageReceivedEvent event;

	private final AkinatorCache akinatorCache;
	private final GuildSettingsCache guildSettingsCache;

	public AkinatorContext(GuildMessageReceivedEvent event, AkinatorCache akinatorCache, GuildSettingsCache guildSettingsCache) {
		this.event = event;

		this.akinatorCache = akinatorCache;
		this.guildSettingsCache = guildSettingsCache;
	}

	public Message getMessage() {
		return event.getMessage();
	}

	public TextChannel getChannel() {
		return event.getChannel();
	}

	public AkinatorCache getAkinatorCache() {
		return akinatorCache;
	}

	public I18n getI18n() {
		return guildSettingsCache.getI18n(event.getGuild().getIdLong());
	}
}